import bpy

# ----------------------------
# CONFIG: edit these lists
# ----------------------------
DIV_FULLBODY = "----- Full Body -----"
DIV_TORSO = "----- Torso -----"
DIV_ARMS = "----- Arms -----"
DIV_HIPS = "----- Hips -----"
DIV_LEGSFEET = "----- Legs and Feet -----"
DIV_MUSCLES = "----- Muscles -----"
DIV_BELLY = "----- Belly -----"
DIV_BREASTS = "----- Breasts -----"
DIV_BUTT = "----- Butt -----"
DIV_NIPS = "----- Nipples -----"
DIV_BITS = "----- Vaginal and Anal -----" # Why are people so thirsty
DIV_OTHER = " ----- Other -----"


# All of our shapekeys needed.
FULLBODY_KEYS = [
    "7B Lower",
    "7B Upper",
    "AnkleSize",
    "NeckSeam",
    "OldBaseShape",
    "VanillaSSEHi",
    "VanillaSSELo",
    "WristSize",
]

TORSO_KEYS = [
    "BackArch",
    "Back",
    "BackValley_v2",
    "BackWing_v2",
    "BackValley",
    "BackWing",
    "ChestDepth",
    "ChestWidth",
    "ChubbyWaist",
    "Clavicle_v2",
    "NavelEven",
    "RibsProminance",
    "RibsMore_v2",
    "Torso",
    "SternumDepth",
    "SternumHeight",
    "WaistHeight",
    "WideWaistLine",
    "Waist",
]

ARM_KEYS = [
    "ArmpitShape_v2",
    "ChubbyArms",
    "ForearmSize",
    "ShoulderSmooth",
    "ShoulderTweak",
    "ShoulderWidth",
    "Arms",
]

HIP_KEYS = [
    "HipCarved",
    "HipBone",
    "HipForward",
    "HipNarrow_v2",
    "Hips",
    "UNPHip_v2",
    "HipUpperWidth",
]

LEGSFEET_KEYS = [
    "7BLeg_v2",
    "CalfFBThicc_v2",
    "CalfSize",
    "CalfSmooth",
    "ChubbyLegs",
    "KneeHeight",
    "KneeShape",
    "KneeTogether_v2",
    "LegSpread_v2",
    "ThighFBThicc_v2",
    "ThighInsideThicc_v2",
    "ThighOutsideThicc_v2",
    "SlimThighs",
    "LegShapeClassic",
    "Thighs",
    "LegsThin",
]

MUSCLE_KEYS = [
    "MuscleAbs",
    "MuscleArms",
    "MuscleBack_v2",
    "MuscleButt",
    "MuscleLegs",
    "MuscleMoreAbs_v2",
    "MuscleMoreArms_v2",
    "MuscleMoreLegs_v2",
    "MusclePecs",
]

BELLY_KEYS = [
    "BigBelly",
    "BellyFrontDownFat_v2",
    "BellyFrontUpFat_v2",
    "BellySideDownFat_v2",
    "BellySideUpFat_v2",
    "PregnancyBelly",
    "Belly",
    "TummyTuck",
    "BellyUnder_v2",
]

BREAST_KEYS = [
    "BreastCenter",
    "BreastCenterBig",
    "BreastCleavage",
    "BreastsConverage_v2",
    "BreastsFantasy",
    "BreastFlatness",
    "BreastsGone",
    "BreastGravity2",
    "BreastHeight",
    "DoubleMelon",
    "BreastFlatness2",
    "BreastPerkiness",
    "BreastsPressed_v2",
    "BreastsTogether",
    "PushUp",
    "BreastSideShape",
    "BreastsNewSH",
    "BreastsNewSHSymmetry",
    "Breasts",
    "BreastsSmall",
    "BreastsSmall2",
    "BreastTopSlope",
    "BreastUnderDepth",
    "BreastWidth",
]

BUTT_KEYS = [
    "AppleCheeks",
    "BigButt",
    "ChubbyButt",
    "ButtClassic",
    "ButtCrack",
    "ButtDimples",
    "Groin",
    "CrotchBack",
    "ButtNarrow_v2",
    "ButtPressed_v2",
    "RoundAss",
    "ButtSaggy_v2",
    "ButtShape2",
    "Butt",
    "ButtSmall",
    "ButtUnderFold",
]

NIPPLE_KEYS = [
    "AreolaPull_v2",
    "AreolaSize",
    "NippleBump_v2",
    "NippleCrease_v2",
    "NippleCrumpled_v2",
    "NippleManga", # This is 'Defined'? ?????
    "NippleDip",
    "NippleDistance",
    "NipBGone",
    "NippleInvert_v2",
    "NippleLength",
    "NipplePuffy_v2",
    "NipplePerkManga", # More Puff?????
    "NipplePerkiness",
    "NippleDown",
    "NippleUp",
    "NippleShy_v2",
    "NippleSize",
    "NippleSquash1_v2",
    "NippleSquash2_v2",
    "NippleThicc_v2",
    "NippleTip",
    "NippleTube_v2",
    "NippleTipManga", # Somehow, this is twisty nips.
]

# *sigh*
BITS_KEYS = [
    "AnalPosition_v2",
    "AnalLoose_v2",
    "AnalTexPos_v2",
    "AnalTexPosRe_v2",
    "ClitSwell_v2",
    "Clit",
    "CBPC", # Crotch Puffy
    "Cutepuffyness",
    "CrotchGap",
    "VaginaHole", # Hole Size
    "Innieoutie",
    "LabiaBulgogi_v2",
    "LabiaCrumpled_v2",
    "Labiaprotrude",
    "Labiaprotrude2",
    "Labiaspread",
    "Labiapuffyness",
    "LabiaMorePuffyness_v2",
    "LabiaNeat_v2",
    "Labiaprotrudeback",
    "LabiaTightUp",
    "Vaginasize",
]


CASE_INSENSITIVE = True

# ----------------------------
# Helpers
# ----------------------------
def norm(s: str) -> str:
    return s.casefold() if CASE_INSENSITIVE else s

def ensure_divider(obj, name: str):
    """Create a dummy shape key divider if it doesn't exist."""
    kb = obj.data.shape_keys.key_blocks
    if any(norm(k.name) == norm(name) for k in kb):
        return
    obj.shape_key_add(name=name, from_mix=False)

def index_map(obj):
    kb = obj.data.shape_keys.key_blocks
    return {norm(k.name): i for i, k in enumerate(kb)}

def move_key_to_index(obj, from_index, to_index):
    obj.active_shape_key_index = from_index
    while obj.active_shape_key_index > to_index:
        bpy.ops.object.shape_key_move(type='UP')

def move_name_to_index(obj, name: str, target_index: int) -> int:
    """Move key (if exists) to target_index. Returns next target_index."""
    m = index_map(obj)
    idx = m.get(norm(name))
    if idx is None:
        return target_index
    if idx != target_index:
        move_key_to_index(obj, idx, target_index)
    return target_index + 1

def unique_existing(obj, names):
    """Return list of names in 'names' that exist on obj, de-duped while preserving order."""
    kb = obj.data.shape_keys.key_blocks
    existing = {norm(k.name) for k in kb}
    out = []
    seen = set()
    for n in names:
        nn = norm(n)
        if nn in existing and nn not in seen:
            out.append(n)
            seen.add(nn)
    return out

# ----------------------------
# Main
# ----------------------------
obj = bpy.context.active_object
if not obj or obj.type != "MESH" or not obj.data.shape_keys:
    raise RuntimeError("Select a mesh object with shape keys as the active object.")

if bpy.context.mode != 'OBJECT':
    bpy.ops.object.mode_set(mode='OBJECT')

# Ensure Basis is at index 0 (Blender normally does this)
kb = obj.data.shape_keys.key_blocks
if norm(kb[0].name) != norm("Basis"):
    # If someone renamed Basis, we still treat index 0 as basis-equivalent.
    pass

# Create divider keys (if needed)
ensure_divider(obj, DIV_FULLBODY)
ensure_divider(obj, DIV_TORSO)
ensure_divider(obj, DIV_ARMS)
ensure_divider(obj, DIV_HIPS)
ensure_divider(obj, DIV_LEGSFEET)
ensure_divider(obj, DIV_MUSCLES)
ensure_divider(obj, DIV_BELLY)
ensure_divider(obj, DIV_BREASTS)
ensure_divider(obj, DIV_BUTT)
ensure_divider(obj, DIV_NIPS)
ensure_divider(obj, DIV_BITS)

# Build ordered plan
fullbody = unique_existing(obj, FULLBODY_KEYS)
torso = unique_existing(obj, TORSO_KEYS)
arms = unique_existing(obj, ARM_KEYS)
hips = unique_existing(obj, HIP_KEYS)
legsfeet = unique_existing(obj, LEGSFEET_KEYS)
muscles = unique_existing(obj, MUSCLE_KEYS)
belly = unique_existing(obj, BELLY_KEYS)
breasts = unique_existing(obj, BREAST_KEYS)
butt = unique_existing(obj, BUTT_KEYS)
nipples = unique_existing(obj, NIPPLE_KEYS)
bits = unique_existing(obj, BITS_KEYS)

# Names to exclude from "everything else"
divider_set = {
    norm(DIV_FULLBODY), 
    norm(DIV_TORSO),
    norm(DIV_ARMS),
    norm(DIV_HIPS),
    norm(DIV_LEGSFEET),
    norm(DIV_MUSCLES),
    norm(DIV_BELLY),
    norm(DIV_BREASTS),
    norm(DIV_BUTT),
    norm(DIV_NIPS),
    norm(DIV_BITS),
}

listed_set = {norm(n) for n in fullbody + torso + arms + hips + legsfeet + muscles + breasts + butt + nipples + bits}
listed_set |= divider_set
listed_set.add(norm("Basis"))

# Collect "everything else"
all_names = [k.name for k in obj.data.shape_keys.key_blocks]
everything_else = [n for n in all_names if norm(n) not in listed_set]

# Now perform moves in one pass from top to bottom
target = 1  # index after Basis

# Full Body
target = move_name_to_index(obj, DIV_FULLBODY, target)
for name in fullbody:
    target = move_name_to_index(obj, name, target)

# Torso
target = move_name_to_index(obj, DIV_TORSO, target)
for name in torso:
    target = move_name_to_index(obj, name, target)

# Arms
target = move_name_to_index(obj, DIV_ARMS, target)
for name in arms:
    target = move_name_to_index(obj, name, target)

# Hips
target = move_name_to_index(obj, DIV_HIPS, target)
for name in hips:
    target = move_name_to_index(obj, name, target)
      
# Legs and Feet
target = move_name_to_index(obj, DIV_LEGSFEET, target)
for name in legsfeet:
    target = move_name_to_index(obj, name, target)
    
# Muscles
target = move_name_to_index(obj, DIV_MUSCLES, target)
for name in muscles:
    target = move_name_to_index(obj, name, target)
    
# Belly
target = move_name_to_index(obj, DIV_BELLY, target)
for name in belly:
    target = move_name_to_index(obj, name, target)
    
# Breasts
target = move_name_to_index(obj, DIV_BREASTS, target)
for name in breasts:
    target = move_name_to_index(obj, name, target)

# Butt    
target = move_name_to_index(obj, DIV_BUTT, target)
for name in butt:
    target = move_name_to_index(obj, name, target)
    
# Nipples    
target = move_name_to_index(obj, DIV_NIPS, target)
for name in nipples:
    target = move_name_to_index(obj, name, target)
    
# The female parts.
target = move_name_to_index(obj, DIV_BITS, target)
for name in bits:
    target = move_name_to_index(obj, name, target)

print("Done: created dividers and reorganized shape keys.")