import bpy

# ----------------------------
# CONFIG: edit these lists
# ----------------------------
DIV_VIS = "----- Visemes -----"
DIV_EYE = "----- Eyes -----"
DIV_MOOD = "----- Moods -----"
DIV_BROW = "----- Brows -----"

# VRChat visemes (example)
VISEME_KEYS = [
    "Aah",
    "BigAah",
    "BMP",
    "ChJSh",
    "DST",
    "Eee",
    "Eh",
    "FV",
    "I",
    "K",
    "N",
    "Oh",
    "OohQ",
    "R",
    "Th",
    "W",
]

EYE_KEYS = [
    "BlinkLeft",
    "BlinkRight",
    "LookDown",
    "LookLeft",
    "LookRight",
    "LookUp",
]

MOOD_KEYS = [
    "CombatAnger",
    "CombatShout",
    "DialogueAnger",
    "DialogueDisgusted",
    "DialogueFear",
    "DialogueHappy",
    "DialoguePuzzled",
    "DialogueSad",
    "DialogueSurprise",
    "MoodAnger",
    "MoodDisgusted",
    "MoodFear",
    "MoodHappy",
    "MoodPuzzled",
    "MoodSad",
    "MoodSurprise",
    # add/remove as needed
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
ensure_divider(obj, DIV_VIS)
ensure_divider(obj, DIV_EYE)
ensure_divider(obj, DIV_MOOD)
ensure_divider(obj, DIV_BROW)

# Build ordered plan
visemes = unique_existing(obj, VISEME_KEYS)
eyes = unique_existing(obj, EYE_KEYS)
moods = unique_existing(obj, MOOD_KEYS)

# Names to exclude from "everything else"
divider_set = {norm(DIV_VIS), norm(DIV_EYE), norm(DIV_MOOD), norm(DIV_BROW)}
listed_set = {norm(n) for n in visemes + eyes + moods}
listed_set |= divider_set
listed_set.add(norm("Basis"))

# Collect "everything else"
all_names = [k.name for k in obj.data.shape_keys.key_blocks]
everything_else = [n for n in all_names if norm(n) not in listed_set]

# Now perform moves in one pass from top to bottom
target = 1  # index after Basis

# visemes section
target = move_name_to_index(obj, DIV_VIS, target)
for name in visemes:
    target = move_name_to_index(obj, name, target)

# eyes section
target = move_name_to_index(obj, DIV_EYE, target)
for name in eyes:
    target = move_name_to_index(obj, name, target)

# moods section
target = move_name_to_index(obj, DIV_MOOD, target)
for name in moods:
    target = move_name_to_index(obj, name, target)

# brows/everything-else section
target = move_name_to_index(obj, DIV_BROW, target)
for name in everything_else:
    target = move_name_to_index(obj, name, target)

print("Done: created dividers and reorganized shape keys.")