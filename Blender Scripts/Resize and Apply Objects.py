import bpy
from mathutils import Euler

# ----------------------------
# USER VARIABLES
# This script resizes, rotates and applies meshes to a base.
# Skyrim TRI files export at 10x scale and rotated by 90 degrees. 
# For outfits this is good enough, but when you have 50+ items to do this for, scripting becomes the way.
# Faceparts required an extra step to move around...because *reasons*.
# ----------------------------
BASE_OBJ_NAME = "FemaleHead"     # destination mesh (receives shape keys)
COLLECTION_NAME = None          # set to "MyCollection" or None for whole scene
UNIFORM_SCALE = 0.1            # uniform scale for each source mesh
Y_OFFSET = -0.154752 # Leave this alone if you're dealing with faceparts, set it to 0.0 for outfits.
Z_OFFSET = 12.0344 # Leave this alone if you're dealing with faceparts, set it to 0.0 for outfits. 
SET_Z_ABSOLUTE = False          # False: add offset, True: set location.z = Z_OFFSET
SET_Y_ABSOLUTE = False
SKIP_HIDDEN = True

APPLY_ROTATION = True
APPLY_SCALE = True
APPLY_LOCATION = True           # apply location too (recommended if you're moving Z)

# ----------------------------
# Helpers
# ----------------------------
def get_objects_to_process():
    if COLLECTION_NAME:
        col = bpy.data.collections.get(COLLECTION_NAME)
        if not col:
            raise RuntimeError(f"Collection '{COLLECTION_NAME}' not found.")
        objs = list(col.all_objects)
    else:
        objs = list(bpy.context.scene.objects)

    out = []
    for obj in objs:
        if SKIP_HIDDEN and (obj.hide_get() or obj.hide_viewport):
            continue
        out.append(obj)
    return out

def ensure_basis_shapekey(base_obj):
    if base_obj.type != 'MESH':
        raise RuntimeError(f"Base object '{base_obj.name}' is not a mesh.")
    if base_obj.data.shape_keys is None:
        base_obj.shape_key_add(name="Basis", from_mix=False)

def deselect_all():
    for o in bpy.context.selected_objects:
        o.select_set(False)

def apply_transforms(obj):
    if bpy.context.mode != 'OBJECT':
        bpy.ops.object.mode_set(mode='OBJECT')

    deselect_all()
    obj.select_set(True)
    bpy.context.view_layer.objects.active = obj

    bpy.ops.object.transform_apply(
        location=APPLY_LOCATION,
        rotation=APPLY_ROTATION,
        scale=APPLY_SCALE
    )

def join_as_shape(base_obj, src_obj):
    if bpy.context.mode != 'OBJECT':
        bpy.ops.object.mode_set(mode='OBJECT')

    deselect_all()
    base_obj.select_set(True)
    src_obj.select_set(True)
    bpy.context.view_layer.objects.active = base_obj

    bpy.ops.object.join_shapes()

# ----------------------------
# Main
# ----------------------------
base = bpy.data.objects.get(BASE_OBJ_NAME)
if not base:
    raise RuntimeError(f"Base object '{BASE_OBJ_NAME}' not found.")

ensure_basis_shapekey(base)

objs = get_objects_to_process()

skipped = []
processed = 0
failed = []

for obj in objs:
    if obj == base:
        continue
    if obj.type in {"CAMERA", "LIGHT"}:
        continue
    if obj.type != "MESH":
        skipped.append((obj.name, obj.type))
        continue

    try:
        obj.rotation_mode = 'XYZ'
        obj.rotation_euler = Euler((0.0, 0.0, 0.0), 'XYZ')
        obj.scale = (UNIFORM_SCALE, UNIFORM_SCALE, UNIFORM_SCALE)
        
        if SET_Y_ABSOLUTE:
            obj.location.y = Y_OFFSET
        else:
            obj.location.y += Y_OFFSET


        if SET_Z_ABSOLUTE:
            obj.location.z = Z_OFFSET
        else:
            obj.location.z += Z_OFFSET

        apply_transforms(obj)
        join_as_shape(base, obj)

        processed += 1

    except Exception as e:
        failed.append((obj.name, str(e)))

print("---- Join as Shapes batch complete ----")
print(f"Base: {base.name}")
print(f"Processed meshes -> shapekeys: {processed}")
if skipped:
    print(f"Skipped (non-mesh): {len(skipped)} (showing up to 20)")
    for name, typ in skipped[:20]:
        print("  ", name, typ)
if failed:
    print(f"Failed: {len(failed)} (showing up to 20)")
    for name, err in failed[:20]:
        print("  ", name, ":", err)
print("--------------------------------------")