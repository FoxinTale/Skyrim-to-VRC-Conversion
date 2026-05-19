import bpy

# ----------------------------
# CONFIG
# ----------------------------
MAP = {
    "Aah":   "vrc.v_aa",
    "BMP":   "vrc.v_pp",
    "ChJSh": "vrc.v_ch",
    "DST":   ["vrc.v_dd", "vrc.v_ss"],  # duplicate DST into DD + SS
    "Eh":    "vrc.v_e",
    "FV":    "vrc.v_ff",
    "I":     "vrc.v_ih",
    "K":     "vrc.v_kk",
    "N":     "vrc.v_nn",
    "Oh":    "vrc.v_oh",
    "OohQ":  "vrc.v_ou",
    "R":     "vrc.v_rr",
    "Th":    "vrc.v_th",
}

CASE_INSENSITIVE = True
SKIP_EXISTING_TARGETS = True
CREATE_SIL_IF_MISSING = True
SIL_NAME = "vrc.sil"

# ----------------------------
# Helpers
# ----------------------------
def norm(s: str) -> str:
    return s.casefold() if CASE_INSENSITIVE else s

def find_key_block(key_blocks, name: str):
    nn = norm(name)
    for kb in key_blocks:
        if norm(kb.name) == nn:
            return kb
    return None

def key_name_exists(key_blocks, name: str) -> bool:
    return find_key_block(key_blocks, name) is not None

def duplicate_keyblock(obj, src_kb, new_name: str):
    """Create a new shapekey on obj by copying vertex coords from src_kb."""
    new_kb = obj.shape_key_add(name=new_name, from_mix=False)
    for i, v in enumerate(new_kb.data):
        v.co = src_kb.data[i].co
    return new_kb

# ----------------------------
# Main
# ----------------------------
obj = bpy.context.active_object
if not obj or obj.type != "MESH" or not obj.data.shape_keys:
    raise RuntimeError("Select the mesh (with Skyrim phoneme shape keys) as the active object.")

if bpy.context.mode != 'OBJECT':
    bpy.ops.object.mode_set(mode='OBJECT')

kb = obj.data.shape_keys.key_blocks
basis = find_key_block(kb, "Basis") or kb[0]

# Create a blank vrc.sil (copy Basis) if missing
if CREATE_SIL_IF_MISSING and not key_name_exists(kb, SIL_NAME):
    sil = obj.shape_key_add(name=SIL_NAME, from_mix=False)
    for i, v in enumerate(sil.data):
        v.co = basis.data[i].co
    print(f"Created blank '{SIL_NAME}' from Basis.")
    kb = obj.data.shape_keys.key_blocks  # refresh

renamed = 0
created = 0
missing = []
skipped = []

for skyrim_name, target in MAP.items():
    src = find_key_block(kb, skyrim_name)
    if not src:
        missing.append(skyrim_name)
        continue

    # Single target: rename
    if isinstance(target, str):
        if SKIP_EXISTING_TARGETS and key_name_exists(kb, target) and norm(src.name) != norm(target):
            skipped.append((skyrim_name, target, "target exists"))
            continue
        src.name = target
        renamed += 1
        kb = obj.data.shape_keys.key_blocks
        continue

    # Multiple targets: rename to first, duplicate for the rest
    if isinstance(target, (list, tuple)) and target:
        first = target[0]

        if SKIP_EXISTING_TARGETS and key_name_exists(kb, first) and norm(src.name) != norm(first):
            skipped.append((skyrim_name, first, "target exists"))
        else:
            src.name = first
            renamed += 1

        kb = obj.data.shape_keys.key_blocks
        src_now = find_key_block(kb, first) or src

        for extra in target[1:]:
            if SKIP_EXISTING_TARGETS and key_name_exists(kb, extra):
                skipped.append((skyrim_name, extra, "target exists"))
                continue
            duplicate_keyblock(obj, src_now, extra)
            created += 1

# Report
print("---- Skyrim -> VRC viseme rename/dup complete ----")
print(f"Renamed: {renamed}")
print(f"Duplicated/Created: {created}")
if missing:
    print(f"Missing source keys: {len(missing)} (first 20): {missing[:20]}")
if skipped:
    print(f"Skipped targets: {len(skipped)} (first 20): {skipped[:20]}")
print("-------------------------------------------------")