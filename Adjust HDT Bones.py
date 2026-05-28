import bpy
from mathutils import Vector
import math
# ----------------------------
# CONFIG
# ----------------------------
MESH_NAME = "fox_tail_0"                 # mesh object that contains the vertex groups
ROOT_BONE = "CME Tail Spine [Spn0]"        # first bone in the chain (script follows children)
CHILD_PICK_MODE = "CLOSEST_TO_TAIL"  # "ONLY_CHILD" or "CLOSEST_TO_TAIL"
START_CHILD_NAME = "CME TailBone01"  # set to None to use auto-pick
CASE_INSENSITIVE_CHILD = True

VERTEX_GROUPS = [
    "HDT TailBone001",
    "HDT TailBone002",
    "HDT TailBone003",
    "HDT TailBone004",
    "HDT TailBone005",
    "HDT TailBone006",
    "HDT TailBone007",
    "HDT TailBone008",
    "HDT TailBone009",
    "HDT TailBone0010",
    "HDT TailBone0011",
]



WEIGHT_THRESH = 0.001
TOP_N_VERTS = 120
TOP_FRAC = 0.25

SMOOTH_ITERS = 8

SCALE_CHAIN_TO_FIT = True
MIN_BONE_LEN = 0.001

CREATE_END_BONE = False
END_SUFFIX = "_end"
END_MIN_LEN = 0.005

ROLL_MODE = "GLOBAL_Z"  # or "GLOBAL_Y"

# ----------------------------
# Helpers
# ----------------------------
def smooth_points(points, iters):
    if iters <= 0 or len(points) < 3:
        return points
    pts = points[:]
    for _ in range(iters):
        new = pts[:]
        for i in range(1, len(pts) - 1):
            new[i] = (pts[i-1] + pts[i] + pts[i+1]) / 3.0
        pts = new
    return pts

def arc_lengths(points):
    d = [0.0]
    for i in range(1, len(points)):
        d.append(d[-1] + (points[i] - points[i-1]).length)
    return d

def sample_poly(points, cumlen, t):
    total = cumlen[-1]
    if total <= 1e-9:
        return points[0].copy()
    t = max(0.0, min(total, t))
    for i in range(1, len(points)):
        if cumlen[i] >= t:
            seg_len = cumlen[i] - cumlen[i-1]
            if seg_len <= 1e-9:
                return points[i].copy()
            alpha = (t - cumlen[i-1]) / seg_len
            return points[i-1].lerp(points[i], alpha)
    return points[-1].copy()

def tangent_poly(points, cumlen, t):
    total = cumlen[-1]
    if total <= 1e-9:
        return Vector((0,0,1))
    t = max(0.0, min(total, t))
    for i in range(1, len(points)):
        if cumlen[i] >= t:
            v = points[i] - points[i-1]
            return v.normalized() if v.length > 1e-9 else Vector((0,0,1))
    v = points[-1] - points[-2]
    return v.normalized() if v.length > 1e-9 else Vector((0,0,1))

def ensure_end_bone(eb, last_bone, suffix):
    name = last_bone.name + suffix
    end = eb.get(name)
    if end:
        return end
    end = eb.new(name)
    end.parent = last_bone
    end.use_connect = False
    try:
        end.use_deform = False
    except Exception:
        pass
    return end

def recalc_roll(arm_obj, bones, mode):
    bpy.ops.object.mode_set(mode='EDIT')
    for b in arm_obj.data.edit_bones:
        b.select = False
    for b in bones:
        b.select = True
    if mode == "GLOBAL_Z":
        bpy.ops.armature.calculate_roll(type='GLOBAL_POS_Z')
    else:
        bpy.ops.armature.calculate_roll(type='GLOBAL_POS_Y')

def pick_first_child(root_edit_bone, wanted_name=None):
    kids = list(root_edit_bone.children)
    if not kids:
        return None

    if wanted_name:
        want = wanted_name.casefold() if CASE_INSENSITIVE_CHILD else wanted_name
        for c in kids:
            cn = c.name.casefold() if CASE_INSENSITIVE_CHILD else c.name
            if cn == want:
                return c
        # If not found, fall through to auto-pick

    if CHILD_PICK_MODE == "ONLY_CHILD":
        return kids[0]

    # default: choose child closest to root tail
    kids.sort(key=lambda c: (c.head - root_edit_bone.tail).length)
    return kids[0]

def centroid_world_topweighted_evaluated(mesh_obj, vg, weight_thresh, top_n, top_frac):
    """Centroid in WORLD space from evaluated mesh, using top-weighted verts from vg."""
    if not vg:
        return None

    weights = []
    for v in mesh_obj.data.vertices:
        w = 0.0
        for g in v.groups:
            if g.group == vg.index:
                w = g.weight
                break
        if w > weight_thresh:
            weights.append((v.index, w))
    if not weights:
        return None

    weights.sort(key=lambda t: t[1], reverse=True)
    max_by_frac = int(len(weights) * top_frac) if top_frac < 1.0 else len(weights)
    take = min(len(weights), top_n, max_by_frac if max_by_frac > 0 else len(weights))
    chosen = weights[:take]

    depsgraph = bpy.context.evaluated_depsgraph_get()
    mesh_eval_obj = mesh_obj.evaluated_get(depsgraph)
    me_eval = mesh_eval_obj.to_mesh()
    mw_eval = mesh_eval_obj.matrix_world

    total_w = 0.0
    accum = Vector((0,0,0))
    try:
        for vidx, w in chosen:
            accum += (mw_eval @ me_eval.vertices[vidx].co) * w
            total_w += w
    finally:
        mesh_eval_obj.to_mesh_clear()

    if total_w <= 1e-12:
        return None
    return accum / total_w

# ----------------------------
# Main
# ----------------------------
arm = bpy.context.active_object
if not arm or arm.type != "ARMATURE":
    raise RuntimeError("Select the ARMATURE as the active object.")

mesh = bpy.data.objects.get(MESH_NAME)
if not mesh or mesh.type != "MESH":
    raise RuntimeError(f"Mesh '{MESH_NAME}' not found (or not a mesh).")

bpy.context.view_layer.objects.active = arm
bpy.ops.object.mode_set(mode='EDIT')
eb = arm.data.edit_bones

root = eb.get(ROOT_BONE)
if not root:
    raise RuntimeError(f"Root bone '{ROOT_BONE}' not found in armature.")

start = pick_first_child(root, START_CHILD_NAME)
if not start:
    raise RuntimeError(f"Root bone '{ROOT_BONE}' has no children to start the chain.")

# Build chain from start down by single-child walk
chain = [start]
cur = start
for _ in range(len(VERTEX_GROUPS) - 1):
    kids = list(cur.children)
    if not kids:
        break
    kids.sort(key=lambda c: (c.head - cur.tail).length)
    cur = kids[0]
    chain.append(cur)

count = min(len(chain), len(VERTEX_GROUPS))
mw_inv = arm.matrix_world.inverted()

# Attachment anchor (WORLD): where the chain currently starts (do NOT move root; we anchor the fit here)
anchor_world = arm.matrix_world @ chain[0].head

# 1) Build guide points (WORLD) from vertex groups
pts_world = []
for i in range(count):
    vg = mesh.vertex_groups.get(VERTEX_GROUPS[i])
    p = centroid_world_topweighted_evaluated(mesh, vg, WEIGHT_THRESH, TOP_N_VERTS, TOP_FRAC)
    if p is None:
        # fallback: current bone head
        p = arm.matrix_world @ chain[i].head
    pts_world.append(p)

# Smooth guide
pts_world = smooth_points(pts_world, SMOOTH_ITERS)

# 2) Anchor the guide to the current chain start (prevents drifting / "root scale" looking wrong)
offset = anchor_world - pts_world[0]
pts_world = [p + offset for p in pts_world]

# 3) Arc-length parameterization
cum = arc_lengths(pts_world)
curve_len = cum[-1]
if curve_len <= 1e-6:
    raise RuntimeError("Guide curve length is ~0; check vertex groups / smoothing.")

# 4) Bone lengths (do NOT touch root; only chain bones)
orig_lens = [max((chain[i].tail - chain[i].head).length, MIN_BONE_LEN) for i in range(count)]
chain_len = sum(orig_lens)

scale = 1.0
if SCALE_CHAIN_TO_FIT and chain_len > 1e-9:
    scale = curve_len / chain_len

lens = [L * scale for L in orig_lens]

# 5) Place heads along curve (WORLD)
s = 0.0
heads_world = []
for i in range(count):
    heads_world.append(sample_poly(pts_world, cum, s))
    if i < count - 1:
        s += lens[i]

# Ensure last head doesn't go past end
heads_world[-1] = sample_poly(pts_world, cum, min(curve_len, s))

# 6) Write bones (armature-local)
for i in range(count):
    chain[i].head = mw_inv @ heads_world[i]

for i in range(count):
    t = sum(lens[:i])
    tan = tangent_poly(pts_world, cum, t)
    tail_world = heads_world[i] + tan * lens[i]
    chain[i].tail = mw_inv @ tail_world

# 7) Optional end bone
if CREATE_END_BONE:
    last = chain[count - 1]
    end = ensure_end_bone(eb, last, END_SUFFIX)

    end_head_world = sample_poly(pts_world, cum, curve_len)
    end.head = mw_inv @ end_head_world

    tan_end = tangent_poly(pts_world, cum, curve_len)
    end_len = max(lens[-1], END_MIN_LEN)
    end.tail = mw_inv @ (end_head_world + tan_end * end_len)

# Roll cleanup only on chain bones (root untouched)
recalc_roll(arm, chain[:count], ROLL_MODE)

bpy.ops.object.mode_set(mode='OBJECT')
print(f"Done. Root '{ROOT_BONE}' untouched. curve_len={curve_len:.3f} chain_len={chain_len:.3f} scale={scale:.4f}")