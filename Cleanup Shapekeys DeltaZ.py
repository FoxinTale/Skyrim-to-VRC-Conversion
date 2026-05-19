import bpy

# ----------------------------
# CONFIG
# ----------------------------
USE_MEDIAN = True          # median is robust; set False to use mean
MIN_ABS_DY = 1e-6          # only correct keys whose |dy| >= this
MIN_ABS_DZ = 1e-6          # only correct keys whose |dz| >= this

obj = bpy.context.active_object
if not obj or obj.type != "MESH" or not obj.data.shape_keys:
    raise RuntimeError("Select the mesh object that has the shape keys.")

keys = obj.data.shape_keys.key_blocks
basis = keys.get("Basis") or keys[0]
n = len(obj.data.vertices)

def get_center_offset_yz(kb):
    dys = []
    dzs = []
    for i in range(n):
        d = kb.data[i].co - basis.data[i].co
        dys.append(d.y)
        dzs.append(d.z)

    if USE_MEDIAN:
        dys.sort(); dzs.sort()
        mid = n // 2
        if n % 2:
            return dys[mid], dzs[mid]
        else:
            return 0.5*(dys[mid-1]+dys[mid]), 0.5*(dzs[mid-1]+dzs[mid])
    else:
        return sum(dys)/n, sum(dzs)/n

fixed = 0
skipped = 0
largest_y = (None, 0.0)
largest_z = (None, 0.0)

for kb in keys:
    if kb == basis or kb.name == "Basis":
        continue

    dy, dz = get_center_offset_yz(kb)

    if abs(dy) < MIN_ABS_DY and abs(dz) < MIN_ABS_DZ:
        skipped += 1
        continue

    for i in range(n):
        kb.data[i].co.y -= dy
        kb.data[i].co.z -= dz

    fixed += 1
    if abs(dy) > largest_y[1]:
        largest_y = (kb.name, abs(dy))
    if abs(dz) > largest_z[1]:
        largest_z = (kb.name, abs(dz))

print("Uniform-translation cleanup complete (Y+Z).")
print(f"Fixed: {fixed} keys | Skipped: {skipped} keys (below thresholds)")
print(f"Largest |dy| corrected: {largest_y[1]} on key '{largest_y[0]}'")
print(f"Largest |dz| corrected: {largest_z[1]} on key '{largest_z[0]}'")