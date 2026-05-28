import bpy

# ----------------------------
# CONFIG
# ----------------------------
MODE = "BONES"     # "BONES" or "VERTEX_GROUPS"
ONLY_SELECTED = True

# For BONES:
ARMATURE_NAME = None  # None = use active object if it's an armature

# For VERTEX_GROUPS:
# If ONLY_SELECTED=False, it will scan all mesh objects in the scene.
# If ONLY_SELECTED=True, it will scan selected mesh objects.

# This is built to handle both males and females.

RENAME_MAP = {
    "NPC Pelvis [Pelv]": "Hips",
    "NPC Thigh [Thg].L": "Left Upper Leg",
    "NPC L FrontThigh": "Left Thigh Front",
    "NPC L RearThigh": "Left Rear Thigh",
    "NPC Calf [Clf].L": "Left Lower Leg",
    "NPC RearCalf [rClf].L": "Left Rear Calf",
    "NPC Foot [ft ].L": "Left Foot",
    "NPC Toe0 [Toe].L": "Left Toes",
    
    "NPC Thigh [Thg].R": "Right Upper Leg",
    "NPC R FrontThigh": "Right Thigh Front",
    "NPC R RearThigh": "Right Rear Thigh",
    "NPC Calf [Clf].R": "Right Lower Leg",
    "NPC RearCalf [rClf].R": "Right Rear Calf",
    "NPC Foot [ft ].R": "Right Foot",
    "NPC Toe0 [Toe].R": "Right Toes",
    
    "NPC R Butt": "Right Butt",
    "NPC L Butt": "Left Butt",
    
    "NPC Spine [Spn0]": "Spine",
    "NPC Spine1 [Spn1]": "Chest",
    "NPC Belly": "Belly",
    "NPC Spine2 [Spn2]": "Upper Chest",
    "NPC Neck [Neck]": "Neck",
    "NPC Head [Head]": "Head",
    
    "L Breast01": "Left Breast.001",
    "L Breast02": "Left Breast.002",
    "L Breast03": "Left Breast.003",
    
    "R Breast01": "Right Breast.001",
    "R Breast02": "Right Breast.002",
    "R Breast03": "Right Breast.003",
    
    "NPC R Breast": "Breast.R",
    "NPC L Breast": "Breast.L",
    
    "NPC Clavicle [Clv].L": "Left Shoulder",
    "NPC UpperArm [Uar].L": "Left Upper Arm",
    "NPC Forearm [Lar].L": "Left Lower Arm",
    "NPC UpperarmTwist1 [Ut1].L": "Left Upper Arm Twist 1",
    "NPC UpperarmTwist2 [Ut2].L": "Left Upper Arm Twist 2",
    "NPC ForearmTwist1 [Lt1].L": "Left Lower Arm Twist 1",
    "NPC ForearmTwist2 [Lt2].L": "Left Lower Arm Twist 2",
    "NPC Hand [Hnd].L": "Left Hand",
    
    "NPC Clavicle [Clv].R": "Right Shoulder",
    "NPC UpperArm [Uar].R": "Right Upper Arm",
    "NPC Forearm [Lar].R": "Right Lower Arm",
    "NPC UpperarmTwist1 [Ut1].R": "Right Upper Arm Twist 1",
    "NPC UpperarmTwist2 [Ut2].R": "Right Upper Arm Twist 2",
    "NPC ForearmTwist1 [Lt1].R": "Right Lower Arm Twist 1",
    "NPC ForearmTwist2 [Lt2].R": "Right Lower Arm Twist 2",
    "NPC Hand [Hnd].R": "Right Hand",
    
    "NPC L Pussy02": "Left Pussy",
    "NPC R Pussy02": "Right Pussy",
    "Clitoral1": "Clitorus",
    "VaginaB1": "Rear Vagina",
    "VaginaDeep1": "Vagina Deep",
    
    "NPC RT Anus2": "Right Upper Anus",
    "NPC RB Anus2": "Right Lower Anus",
    "NPC LT Anus2": "Left Upper Anus",
    "NPC LB Anus2": "Left Lower Anus",
    "NPC Anus Deep2": "Deep Anus",
    
    "NPC GenitalsBase [GenBase]": "Genitals Root",
    "NPC Genitals01 [Gen01]": "Genitals.001",
    
    "NPC Finger00 [F00].R": "Right Thumb.001",
    "NPC Finger01 [F01].R": "Right Thumb.002",
    "NPC Finger02 [F02].R": "Right Thumb.003",
    "NPC Finger10 [F10].R": "Right Index.001",
    "NPC Finger11 [F11].R": "Right Index.002",
    "NPC Finger12 [F12].R": "Right Index.003", 
    "NPC Finger20 [F20].R": "Right Middle.001",
    "NPC Finger21 [F21].R": "Right Middle.002",
    "NPC Finger22 [F22].R": "Right Middle.003",   
    "NPC Finger30 [F30].R": "Right Ring.001",
    "NPC Finger31 [F31].R": "Right Ring.002",
    "NPC Finger32 [F32].R": "Right Ring.003",
    "NPC Finger40 [F40].R": "Right Little.001",
    "NPC Finger41 [F41].R": "Right Little.002",
    "NPC Finger42 [F42].R": "Right Little.003",
    
    "NPC Finger00 [F00].L": "Left Thumb.001",
    "NPC Finger01 [F01].L": "Left Thumb.002",
    "NPC Finger02 [F02].L": "Left Thumb.003",
    "NPC Finger10 [F10].L": "Left Index.001",
    "NPC Finger11 [F11].L": "Left Index.002",
    "NPC Finger12 [F12].L": "Left Index.003", 
    "NPC Finger20 [F20].L": "Left Middle.001",
    "NPC Finger21 [F21].L": "Left Middle.002",
    "NPC Finger22 [F22].L": "Left Middle.003",   
    "NPC Finger30 [F30].L": "Left Ring.001",
    "NPC Finger31 [F31].L": "Left Ring.002",
    "NPC Finger32 [F32].L": "Left Ring.003",
    "NPC Finger40 [F40].L": "Left Little.001",
    "NPC Finger41 [F41].L": "Left Little.002",
    "NPC Finger42 [F42].L": "Left Little.003",
    
    # add more...
}

# What to do if the destination name already exists:
# "SKIP" = don't rename, "OVERWRITE" = merge by renaming anyway (Blender will auto-suffix .001),
# "MERGE" = merge weights (vertex groups only), then delete old
ON_CONFLICT = "MERGE"  # "SKIP" | "OVERWRITE" | "MERGE"

# ----------------------------
# Helpers
# ----------------------------
def rename_bones(arm_obj: bpy.types.Object):
    arm = arm_obj.data
    count = 0
    skipped = []
    conflicts = []

    for old, new in RENAME_MAP.items():
        b = arm.bones.get(old)
        if not b:
            skipped.append(old)
            continue

        if arm.bones.get(new) and new != old:
            conflicts.append((old, new))
            if ON_CONFLICT == "SKIP":
                continue
            # OVERWRITE: Blender will auto-suffix if needed; but bones must be unique, so we skip here.
            # Best practice: SKIP and fix manually for bones.
            continue

        b.name = new
        count += 1

    print(f"Renamed {count} bone(s). Missing: {len(skipped)}. Conflicts: {len(conflicts)}")
    if skipped:
        print("Missing bones (first 20):", skipped[:20])
    if conflicts:
        print("Conflicts (first 20):", conflicts[:20])


def merge_vertex_groups(obj: bpy.types.Object, src_name: str, dst_name: str):
    """Merge src group weights into dst group, then remove src group."""
    src = obj.vertex_groups.get(src_name)
    dst = obj.vertex_groups.get(dst_name)
    if not src:
        return False

    # If dst doesn't exist, just rename
    if not dst:
        src.name = dst_name
        return True

    # Merge: add weights from src into dst (max() style)
    # Build a quick lookup of src weights by vertex
    src_weights = {}
    for v in obj.data.vertices:
        for g in v.groups:
            if g.group == src.index:
                src_weights[v.index] = g.weight
                break

    # Apply weights to dst
    for vidx, w in src_weights.items():
        try:
            dst.add([vidx], w, 'MAX')
        except RuntimeError:
            # Can happen on invalid indices; usually safe to ignore
            pass

    # Remove source group
    obj.vertex_groups.remove(src)
    return True


def rename_vertex_groups(objs):
    renamed = 0
    missing = 0
    conflicts = 0

    for obj in objs:
        if obj.type != "MESH":
            continue

        for old, new in RENAME_MAP.items():
            vg = obj.vertex_groups.get(old)
            if not vg:
                missing += 1
                continue

            exists = obj.vertex_groups.get(new) is not None and new != old
            if exists:
                conflicts += 1
                if ON_CONFLICT == "SKIP":
                    continue
                if ON_CONFLICT == "MERGE":
                    if merge_vertex_groups(obj, old, new):
                        renamed += 1
                    continue
                # OVERWRITE: just rename; Blender will auto-suffix .001 if needed
                vg.name = new
                renamed += 1
                continue

            vg.name = new
            renamed += 1

    print(f"Renamed/merged {renamed} vertex group(s). Missing hits: {missing}. Conflicts: {conflicts}")


# ----------------------------
# Main selection
# ----------------------------
if MODE == "BONES":
    if ARMATURE_NAME:
        arm_obj = bpy.data.objects.get(ARMATURE_NAME)
        if not arm_obj:
            raise RuntimeError(f"Armature '{ARMATURE_NAME}' not found.")
    else:
        arm_obj = bpy.context.active_object
        if not arm_obj or arm_obj.type != "ARMATURE":
            raise RuntimeError("Set ARMATURE_NAME or select an armature as the active object.")

    rename_bones(arm_obj)

elif MODE == "VERTEX_GROUPS":
    if ONLY_SELECTED:
        objs = list(bpy.context.selected_objects)
    else:
        objs = list(bpy.context.scene.objects)

    rename_vertex_groups(objs)

else:
    raise RuntimeError("MODE must be 'BONES' or 'VERTEX_GROUPS'.")