import bpy

# ----------------------------
# CONFIG: edit these lists
# Sorts the CBBE shapekeys in an outfit. Handles both CBBE and 3BA outfits, 
# Sorts them similar to how they are displayed in bodyslide.
# ----------------------------
DIV_FULLBODY = "----- Full Body -----"
DIV_TORSO = "----- Stature -----"
DIV_ARMS = "----- Arms and Shoulders -----"
DIV_LEGSFEET = "----- Thighs and Legs -----"
DIV_MUSCLES = "----- Pecs -----"
DIV_BUTT = "----- Butt -----"
DIV_NIPS = "----- Nipples -----"
DIV_OTHER = " ----- Other -----"


# All of our shapekeys needed.
# Full Body
FULLBODY_KEYS = [
    "Chubby",
    "Lean",
    "Muscle",
    "SOSLike",
    "Weight 0 to 1",
]

# Stature
TORSO_KEYS = [
    "TorsoSterHeight",
	"TorsoSterDepth",
	"TorsoSterWidth",
	"TorsoBackSize",
	"TorsoBackSlope",
	"TorsoBackShape",
	"TorsoBackCenter",
	"TorsoBackSerratusMid",
	"TorsoBackObliques",
	"TorsoMass",
	"TorsoWidth",
	"TorsoLower",
	"TorsoWaistSize",
	"TorsoWaistHeight",
	"TorsoHip",
	"TorsoFlatAbs",
	"TorsoRibsDefinition",
	"TorsoVLine",
	"TorsoBelly",
	"TorsoBellyChub",
	"TorsoBellyLHandles",
	"TorsoSpine",
]

# Arms
ARM_KEYS = [
	"ArmsTraps",
	"ArmsTrapsMeat",
	"ArmsTrapsPush",
	"ArmsTrapsValleys",
	"ArmsClavicleCurve",
	"ArmsShoulders",
	"ArmsDelts",
	"ArmsDeltsBack",
	"ArmsDeltsUpper",
	"ArmsDeltsLower",
	"ArmsBiceps",
	"ArmsBicepsBack",
	"ArmsSide",
	"ArmsBrachio",
	"ArmsFore",
]


LEGSFEET_KEYS = [
	"LegsSize",
	"LegsThigh",
	"LegsThinner",
	"LegsChubby",
	"LegsGlutes",
	"LegsFemurUpper",
    "LegsFemurLower",
	"LegsFemurSide",
	"LegsFemurBack",
	"LegsKneePit",
	"LegsCalfSize",
	"LegsCalfWidth",
	"LegsCalfUpper",
	"LegsCalfLower",
	"LegsCalfFlatten",
	"LegsShinCrease",
]

MUSCLE_KEYS = [
	"PecsClavicle",
	"PecsSize",
	"PecsMass",
	"PecsSaggy",
	"PecsWidth",
	"PecsFlatten",
	"PecsPosV",
	"PecsPosH",
	"PecsSide",
	"PecsPush",
	"PecsLowerSide",
	"PecsDecrease",
	"PecsCrease",
]

BUTT_KEYS = [
	"ButtBooty",
	"ButtRoundy",
	"ButtSaggy",
	"ButtCleft",
	"ButtSide",
	"ButtCenterPush",
	"ButtCurve",
	"ButtDimpleDeepen",
	"ButtDimpleFatten",
]


NIPPLE_KEYS = [
	"NipsAreola",
	"NipsTips",
	"NipsLength",
	"NipsRound",
	"NipsAngle",
	"NipsPuffy",
	"NipsLower",
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
ensure_divider(obj, DIV_LEGSFEET)
ensure_divider(obj, DIV_MUSCLES)
ensure_divider(obj, DIV_BUTT)
ensure_divider(obj, DIV_NIPS)
ensure_divider(obj, DIV_OTHER)

# Build ordered plan
fullbody = unique_existing(obj, FULLBODY_KEYS)
torso = unique_existing(obj, TORSO_KEYS)
arms = unique_existing(obj, ARM_KEYS)
legsfeet = unique_existing(obj, LEGSFEET_KEYS)
muscles = unique_existing(obj, MUSCLE_KEYS)
butt = unique_existing(obj, BUTT_KEYS)
nipples = unique_existing(obj, NIPPLE_KEYS)

# Names to exclude from "everything else"
divider_set = {
    norm(DIV_FULLBODY), 
    norm(DIV_TORSO),
    norm(DIV_ARMS),
    norm(DIV_LEGSFEET),
    norm(DIV_MUSCLES),
    norm(DIV_BUTT),
    norm(DIV_NIPS),
}

listed_set = {norm(n) for n in fullbody + torso + arms + legsfeet + muscles + butt + nipples}
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
      
# Legs and Feet
target = move_name_to_index(obj, DIV_LEGSFEET, target)
for name in legsfeet:
    target = move_name_to_index(obj, name, target)
    
# Muscles
target = move_name_to_index(obj, DIV_MUSCLES, target)
for name in muscles:
    target = move_name_to_index(obj, name, target)
    
# Butt    
target = move_name_to_index(obj, DIV_BUTT, target)
for name in butt:
    target = move_name_to_index(obj, name, target)
    
# Nipples    
target = move_name_to_index(obj, DIV_NIPS, target)
for name in nipples:
    target = move_name_to_index(obj, name, target)
    
# Anything else not covered.
target = move_name_to_index(obj, DIV_OTHER, target)
for name in everything_else:
    target = move_name_to_index(obj, name, target)

print("Done: created dividers and reorganized shape keys.")