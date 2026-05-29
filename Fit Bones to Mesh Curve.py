import bpy
from mathutils import Vector


# -----------------------------
# Settings
# -----------------------------

GUIDE_MESH_NAME = "WingGuideMesh"

CHAIN_BONE_NAMES = [
    "NPC Spine2 [Spn2]",   # optional anchor/root
    "Right Wing.001",
    "Right Wing.002",
    "Right Wing.003",
]

# False = first bone is anchor only and left untouched.
# True = first bone is fitted too.
INCLUDE_FIRST_BONE_AS_FITTED_ROOT = False

REVERSE_GUIDE_DIRECTION = True

# "EQUAL" = each bone gets equal curve distance.
# "ORIGINAL_LENGTH_RATIO" = preserves original relative bone lengths.
DISTRIBUTION_MODE = "EQUAL"


# -----------------------------
# Helpers
# -----------------------------

def get_active_armature():
    obj = bpy.context.object
    if obj and obj.type == "ARMATURE":
        return obj
    return None


def get_guide_mesh():
    obj = bpy.data.objects.get(GUIDE_MESH_NAME)

    if obj is None:
        raise Exception(f'Guide mesh "{GUIDE_MESH_NAME}" not found.')

    if obj.type != "MESH":
        raise Exception(f'"{GUIDE_MESH_NAME}" must be a mesh object.')

    return obj


def build_polyline_from_edges(mesh_obj):
    mesh = mesh_obj.data

    adjacency = {}

    for edge in mesh.edges:
        a, b = edge.vertices
        adjacency.setdefault(a, []).append(b)
        adjacency.setdefault(b, []).append(a)

    endpoints = [idx for idx, neighbors in adjacency.items() if len(neighbors) == 1]

    if len(endpoints) != 2:
        raise Exception(
            "Guide mesh must be a single open edge chain with exactly two endpoints. "
            f"Found {len(endpoints)} endpoints."
        )

    start = endpoints[0]
    ordered_indices = [start]

    previous = None
    current = start

    while True:
        neighbors = adjacency[current]

        next_candidates = [n for n in neighbors if n != previous]

        if not next_candidates:
            break

        next_idx = next_candidates[0]

        ordered_indices.append(next_idx)

        previous = current
        current = next_idx

    if len(ordered_indices) != len(adjacency):
        raise Exception(
            "Guide mesh appears to be branched, disconnected, or contains extra edges."
        )

    points = [
        mesh_obj.matrix_world @ mesh.vertices[i].co
        for i in ordered_indices
    ]

    return points


def get_polyline_length(points):
    total = 0.0

    for i in range(len(points) - 1):
        total += (points[i + 1] - points[i]).length

    return total


def point_at_distance(points, distance):
    if distance <= 0.0:
        return points[0]

    walked = 0.0

    for i in range(len(points) - 1):
        a = points[i]
        b = points[i + 1]

        segment_length = (b - a).length

        if walked + segment_length >= distance:
            t = (distance - walked) / segment_length if segment_length > 0 else 0.0
            return a.lerp(b, t)

        walked += segment_length

    return points[-1]


def resample_polyline_equal(points, segment_count):
    total_length = get_polyline_length(points)

    if total_length <= 0.0001:
        raise Exception("Guide mesh polyline has near-zero length.")

    result = []

    for i in range(segment_count + 1):
        d = total_length * (i / segment_count)
        result.append(point_at_distance(points, d))

    return result


def resample_polyline_by_ratios(points, ratios):
    total_length = get_polyline_length(points)

    if total_length <= 0.0001:
        raise Exception("Guide mesh polyline has near-zero length.")

    total_ratio = sum(ratios)

    if total_ratio <= 0.0001:
        raise Exception("Bone length ratios are invalid.")

    result = [points[0]]
    walked_ratio = 0.0

    for ratio in ratios:
        walked_ratio += ratio
        d = total_length * (walked_ratio / total_ratio)
        result.append(point_at_distance(points, d))

    return result


# -----------------------------
# Main
# -----------------------------

arm_obj = get_active_armature()

if arm_obj is None:
    raise Exception("Active object must be an armature.")

guide_obj = get_guide_mesh()

old_mode = arm_obj.mode

bpy.context.view_layer.objects.active = arm_obj
bpy.ops.object.mode_set(mode="EDIT")

edit_bones = arm_obj.data.edit_bones

for name in CHAIN_BONE_NAMES:
    if name not in edit_bones:
        raise Exception(f"Bone not found: {name}")

full_chain = [edit_bones[name] for name in CHAIN_BONE_NAMES]

if INCLUDE_FIRST_BONE_AS_FITTED_ROOT:
    fitted_chain = full_chain
else:
    fitted_chain = full_chain[1:]

if not fitted_chain:
    raise Exception("No fitted bones. Add more bones or enable INCLUDE_FIRST_BONE_AS_FITTED_ROOT.")

guide_points_world = build_polyline_from_edges(guide_obj)

if REVERSE_GUIDE_DIRECTION:
    guide_points_world.reverse()

if DISTRIBUTION_MODE.upper() == "EQUAL":
    sampled_world = resample_polyline_equal(
        guide_points_world,
        len(fitted_chain)
    )

elif DISTRIBUTION_MODE.upper() == "ORIGINAL_LENGTH_RATIO":
    original_lengths = [bone.length for bone in fitted_chain]
    sampled_world = resample_polyline_by_ratios(
        guide_points_world,
        original_lengths
    )

else:
    raise Exception('DISTRIBUTION_MODE must be "EQUAL" or "ORIGINAL_LENGTH_RATIO".')

sampled_local = [
    arm_obj.matrix_world.inverted() @ p
    for p in sampled_world
]

for i, bone in enumerate(fitted_chain):
    new_head = sampled_local[i]
    new_tail = sampled_local[i + 1]

    if (new_tail - new_head).length < 0.0001:
        print(f"{bone.name}: skipped, zero-length segment.")
        continue

    bone.head = new_head
    bone.tail = new_tail

    print(f"Placed {bone.name} from guide segment {i}.")

try:
    bpy.ops.object.mode_set(mode=old_mode)
except Exception:
    bpy.ops.object.mode_set(mode="OBJECT")

print("Guide mesh bone fitting complete.")