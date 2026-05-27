import bpy

# ----------------------------
# CONFIG
# ----------------------------
CASE_INSENSITIVE = True
ONLY_ACTIVE_OBJECT = True   # False = rename on all selected meshes
SKIP_IF_TARGET_EXISTS = True  # don't overwrite if the target name already exists

RENAME_MAP = {
    # Full Body
    "VanillaSSEHi": "SSE High",
    "VanillaSSELo": "SSE Low",
    "NeckSeam": "Neck Seam",
    "OldBaseShape": "Old Shape",
    "AnkleSize": "Ankle Size",
    "WristSize": "Wrist Size",
    
    # Torso
    "BackArch": "Back Arch",
    "Back": "Back Size",
    "BackValley_v2": "Back Valley",
    "BackWing_v2": "Back Wing",
    "ChestDepth": "Chest Depth",
    "ChestWidth": "Chest Width",
    "ChubbyWaist": "Chubby Waist",
    "Clavicle_v2": "Clavicle",
    "NavelEven": "Navel Even",
    "RibsMore_v2": "Ribs",
    "RibsProminance": "Protruded Ribs",
    "Torso": "Torso Size",
    "SternumDepth": "Sternum Depth",
    "SternumHeight": "Sternum Height",
    "WaistHeight": "Waist Height",
    "WideWaistLine": "Waistline",
    "Waist": "Waist Size",
    
    # Arms
    "ArmpitShape_v2": "Armpit",
    "ChubbyArms": "Chubby Arms",
    "ForearmSize": "Forearm Size",
    "ShoulderSmooth": "Smoother Shoulders",
    "ShoulderTweak": "Shoulder Tweak",
    "ShoulderWidth": "Shoulder Width",
    "Arms": "Arm Size",
    
    # Hips
    "HipCarved": "Carved",
    "HipBone": "Hip Bone",
    "HipForward": "Hips Forward",
    "HipNarrow_v2": "Narrow Hips",
    "Hips": "Hip Size",
    "UNPHip_v2": "UNP Hips",
    "HipUpperWidth": "Hips Upper Width",
    
    # Legs
    "7BLeg_v2": "7B Legs",
    "CalfFBThicc_v2": "Calf Front Back",
    "CalfSize": "Calf Size",
    "CalfSmooth": "Calf Smooth",
    "ChubbyLegs": "Chubby Legs",
    "KneeHeight": "Knee Height",
    "KneeShape": "Knee Shape",
    "KneeTogether_v2": "Knees Together",
    "LegSpread_v2": "Leg Spread",
    "ThighFBThicc_v2": "Thigh Front Back",
    "ThighInsideThicc_v2": "Thighs Inside",
    "ThighOutsideThicc_v2": "Thighs Outside",
    "SlimThighs": "Slim Thighs",
    "LegShapeClassic": "Leg Shape Classic",
    "LegsThin": "Slim Legs",
    
    # Muscles
    "MuscleAbs": "Abs",
    "MuscleArms": "Muscular Arms",
    "MuscleBack_v2": "Muscular Back",
    "MuscleButt": "Muscular Butt",
    "MuscleLegs": "Muscular Legs",
    "MuscleMoreAbs_v2": "More Abs",
    "MuscleMoreArms_v2": "More Arm Muscles",
    "MuscleMoreLegs_v2": "More Muscular Legs",
    "MusclePecs": "Pecs",
    
    # Belly
    "BigBelly": "Bigger Belly",
    "BellyFrontDownFat_v2": "Front Down Fat",
    "BellyFrontUpFat_v2": "Front Up Fat",
    "BellySideDownFat_v2": "Side Down Fat",
    "BellySideUpFat_v2": "Side Up Fat",
    "PregnancyBelly": "Gregnant",
    "Belly": "Belly Size",
    "TummyTuck": "Belly Tuck",
    "BellyUnder_v2": "Belly Under",
    
    # Breasts
    "BreastCenter": "Centre",
    "BreastCenterBig": "Centre Big",
    "BreastCleavage": "Cleavage",
    "BreastsConverage_v2": "Breasts Together",
    "BreastsFantasy": "Fantasy",
    "BreastFlatness": "Flatness",
    "BreastsGone": "Gone",
    "BreastGravity2": "Gravity",
    "BreastHeight": "Breast Height",
    "DoubleMelon": "Melons",
    "BreastFlatness2": "More Flatness",
    "BreastPerkiness": "Perkiness",
    "BreastsPressed_v2": "Breasts Pressed",
    "BreastsTogether": "Pushed Together",
    "PushUp": "Push Up",
    "BreastSideShape": "Side Shape",
    "BreastsNewSH": "Silly Huge",
    "BreastsNewSHSymmetry": "Silly Huge Symmetry",
    "Breasts": "Breasts Size",
    "BreastsSmall": "Smaller 1",
    "BreastsSmall2": "Smaller 2",
    "BreastTopSlope": "Top Slope",
    "BreastUnderDepth": "Breast Under Depth",
    "BreastWidth": "Breast Width",
    
    # Butt
    "AppleCheeks": "Apple",
    "BigButt": "Bigger Butt",
    "ChubbyButt": "Chubby Butt",
    "ButtClassic": "Classic Butt",
    "ButtCrack": "Crack",
    "ButtDimples": "Dimples",
#    "Groin"
    "CrotchBack": "Crotch Back", # This is called "Move Crotch" in Bodyslide. Why?
    "ButtNarrow_v2": "Narrower Butt",
    "ButtPressed_v2": "Butt Pressed",
    "RoundAss": "Round",
    "ButtSaggy_v2": "Saggy Butt",
    "ButtShape2": "Butt Shape",
    "Butt": "Butt Size",
    "ButtSmall": "Smaller Butt",
    "ButtUnderFold": "Under Fold",
 
    # add more...
}

# ----------------------------
# Helpers
# ----------------------------
def norm(s: str) -> str:
    return s.casefold() if CASE_INSENSITIVE else s

def find_keyblock(key_blocks, name: str):
    nn = norm(name)
    for kb in key_blocks:
        if norm(kb.name) == nn:
            return kb
    return None

def exists_name(key_blocks, name: str) -> bool:
    return find_keyblock(key_blocks, name) is not None

def rename_on_object(obj: bpy.types.Object):
    if obj.type != "MESH" or not obj.data.shape_keys:
        return (0, 0, [])

    kb = obj.data.shape_keys.key_blocks
    renamed = 0
    skipped = 0
    missing = []

    for old, new in RENAME_MAP.items():
        src = find_keyblock(kb, old)
        if not src:
            missing.append(old)
            continue

        if SKIP_IF_TARGET_EXISTS and exists_name(kb, new) and norm(src.name) != norm(new):
            skipped += 1
            continue

        src.name = new
        renamed += 1

        # refresh (names changed)
        kb = obj.data.shape_keys.key_blocks

    return (renamed, skipped, missing)

# ----------------------------
# Main
# ----------------------------
objs = [bpy.context.active_object] if ONLY_ACTIVE_OBJECT else list(bpy.context.selected_objects)
objs = [o for o in objs if o is not None]

total_renamed = 0
total_skipped = 0
all_missing = []

for obj in objs:
    r, s, m = rename_on_object(obj)
    total_renamed += r
    total_skipped += s
    all_missing.extend((obj.name, x) for x in m)

print("---- Shape key rename complete ----")
print(f"Objects processed: {len(objs)}")
print(f"Renamed: {total_renamed}")
print(f"Skipped (target exists): {total_skipped}")
if all_missing:
    print(f"Missing entries: {len(all_missing)} (showing up to 30)")
    for obj_name, old in all_missing[:30]:
        print(f"  {obj_name}: {old}")
print("-----------------------------------")