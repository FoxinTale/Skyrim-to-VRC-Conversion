import bpy

# ----------------------------
# CONFIG
# Prints a list of vertex groups to make copy/pasting into the adjustment script easier.
# ----------------------------
START_BONE = "Tail01"          # bone name to start from
MAX_BONES = 50                # safety cap

# If a bone has multiple children, how to choose the "next" bone:
# "CLOSEST_TO_TAIL" is usually best for chains.
CHILD_PICK_MODE = "CLOSEST_TO_TAIL"   # or "FIRST"

# ----------------------------
# Main
# ----------------------------
arm = bpy.context.active_object
if not arm or arm.type != "ARMATURE":
    raise RuntimeError("Select the armature as the active object.")

bones = arm.data.bones  # rest bones (works in Object/Pose mode)

start = bones.get(START_BONE)
if not start:
    raise RuntimeError(f"Bone '{START_BONE}' not found.")

chain = [start.name]
cur = start

for _ in range(MAX_BONES - 1):
    kids = list(cur.children)
    if not kids:
        break

    if CHILD_PICK_MODE == "CLOSEST_TO_TAIL":
        # Use head_local/tail_local for distance in armature space
        kids.sort(key=lambda c: (c.head_local - cur.tail_local).length)
        nxt = kids[0]
    else:
        nxt = kids[0]

    chain.append(nxt.name)
    cur = nxt

# Print in copy/paste-friendly format
print("----- Bone Chain -----")
print(f"Start: {chain[0]}")
print("CHAIN = [")
for name in chain:
    print(f'    "{name}",')
print("]")
print("----------------------")