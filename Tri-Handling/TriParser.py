import struct
from pathlib import Path


# ----------------------------
# Binary helpers
# ----------------------------

def read_exact(f, size):
    data = f.read(size)
    if len(data) != size:
        raise EOFError(f"Expected {size} bytes, got {len(data)}")
    return data


def read_u8(f):
    return struct.unpack("<B", read_exact(f, 1))[0]


def read_u16(f):
    return struct.unpack("<H", read_exact(f, 2))[0]


def read_i16(f):
    return struct.unpack("<h", read_exact(f, 2))[0]


def read_f32(f):
    return struct.unpack("<f", read_exact(f, 4))[0]


def read_string_u8(f):
    length = read_u8(f)
    return read_exact(f, length).decode("utf-8", errors="replace")


# ----------------------------
# TRI parsing
# ----------------------------

def read_tri(path):
    path = Path(path)

    with path.open("rb") as f:
        magic = read_exact(f, 4)

        if magic != b"PIRT":
            raise ValueError(f"Not a BodySlide TRIP TRI file. Magic was {magic!r}")

        shapes = []
        shape_count = read_u16(f)

        for _ in range(shape_count):
            shape_name = read_string_u8(f)
            morph_count = read_u16(f)

            morphs = []

            for _ in range(morph_count):
                morph_name = read_string_u8(f)
                multiplier = read_f32(f)
                changed_count = read_u16(f)

                vertices = []

                for _ in range(changed_count):
                    vertex_id = read_u16(f)
                    x = read_i16(f)
                    y = read_i16(f)
                    z = read_i16(f)

                    vertices.append((
                        vertex_id,
                        x * multiplier,
                        y * multiplier,
                        z * multiplier,
                    ))

                morphs.append({
                    "name": morph_name,
                    "multiplier": multiplier,
                    "changed_count": changed_count,
                    "vertices": vertices,
                })

            shapes.append({
                "name": shape_name,
                "morphs": morphs,
            })

        has_uv_section = False

        # Optional UV morph section.
        # For now, we only detect whether anything remains.
        remaining = f.read()
        if len(remaining) >= 2:
            has_uv_section = True

    return shapes, has_uv_section


# ----------------------------
# Sanity reporting
# ----------------------------

def print_sanity_report(shapes, preview_count=5):
    print("\n=== TRI Sanity Report ===")

    for shape in shapes:
        print(f"\nShape: {shape['name']}")
        print(f"  Morphs: {len(shape['morphs'])}")

        for morph in shape["morphs"]:
            verts = morph["vertices"]

            print(f"\n  Morph: {morph['name']}")
            print(f"    Changed verts: {len(verts)}")
            print(f"    Multiplier: {morph['multiplier']}")

            if not verts:
                print("    Vertex IDs: none")
                continue

            ids = [v[0] for v in verts]
            deltas = [(v[1], v[2], v[3]) for v in verts]

            print(f"    Vertex ID range: {min(ids)}–{max(ids)}")

            max_abs_delta = max(
                max(abs(dx), abs(dy), abs(dz))
                for dx, dy, dz in deltas
            )

            print(f"    Max absolute delta: {max_abs_delta:.6f}")

            if max_abs_delta > 100:
                print("    WARNING: very large delta; parser may be misaligned")

            print("    First deltas:")
            for vertex_id, dx, dy, dz in verts[:preview_count]:
                print(
                    f"      vertex {vertex_id}: "
                    f"dx={dx:.6f}, dy={dy:.6f}, dz={dz:.6f}"
                )


# ----------------------------
# OBJ handling
# ----------------------------
def parse_obj_index(token, count):
    if token == "":
        return None
    idx = int(token)
    return idx - 1 if idx > 0 else count + idx


def read_obj_bodyslide_style(path):
    path = Path(path)

    positions = []
    uvs = []
    normals = []

    groups = {}
    current_group = "default"

    def get_group(name):
        if name not in groups:
            groups[name] = {
                "vertices": [],
                "uvs": [],
                "normals": [],
                "faces": [],
                "combo_to_index": {},
            }
        return groups[name]

    get_group(current_group)

    with path.open("r", encoding="utf-8", errors="replace") as f:
        for raw_line in f:
            line = raw_line.strip()

            if not line or line.startswith("#"):
                continue

            parts = line.split()
            tag = parts[0]

            if tag == "v":
                positions.append(tuple(map(float, parts[1:4])))

            elif tag == "vt":
                u = float(parts[1])
                v = float(parts[2])
                uvs.append((u, v))

            elif tag == "vn":
                normals.append(tuple(map(float, parts[1:4])))

            elif tag in {"o", "g"}:
                if len(parts) > 1:
                    current_group = parts[1]
                    get_group(current_group)

            elif tag == "f":
                group = get_group(current_group)

                face_indices = []

                for ref in parts[1:]:
                    bits = ref.split("/")

                    vi = parse_obj_index(bits[0], len(positions))
                    ti = parse_obj_index(bits[1], len(uvs)) if len(bits) > 1 else None
                    ni = parse_obj_index(bits[2], len(normals)) if len(bits) > 2 else None

                    combo = (vi, ti, ni)

                    if combo not in group["combo_to_index"]:
                        new_index = len(group["vertices"])
                        group["combo_to_index"][combo] = new_index

                        group["vertices"].append(positions[vi])

                        if ti is not None:
                            group["uvs"].append(uvs[ti])
                        else:
                            group["uvs"].append(None)

                        if ni is not None:
                            group["normals"].append(normals[ni])
                        else:
                            group["normals"].append(None)

                    face_indices.append(group["combo_to_index"][combo])

                # Triangulate ngon/quad fan-style.
                for i in range(1, len(face_indices) - 1):
                    group["faces"].append((
                        face_indices[0],
                        face_indices[i],
                        face_indices[i + 1],
                    ))

    return groups


# ----------------------------
# Morph application
# ----------------------------

def find_shape(shapes, shape_name):
    for shape in shapes:
        if shape["name"].lower() == shape_name.lower():
            return shape
    raise KeyError(f"Shape not found: {shape_name}")


def find_morph(shape, morph_name):
    for morph in shape["morphs"]:
        if morph["name"].lower() == morph_name.lower():
            return morph
    raise KeyError(f"Morph not found: {morph_name}")


def validate_shape_against_obj(shape, base_vertices):
    required = get_shape_vertex_requirement(shape)

    if required > len(base_vertices):
        raise ValueError(
            f"Shape '{shape['name']}' needs at least {required} vertices, "
            f"but OBJ only has {len(base_vertices)} vertices"
        )

def validate_morph_against_obj(morph, base_vertices):
    """
    Validate that this specific morph does not reference
    vertices outside the target OBJ's vertex array.
    """
    if not morph["vertices"]:
        return

    max_id = max(v[0] for v in morph["vertices"])
    min_id = min(v[0] for v in morph["vertices"])

    if min_id < 0:
        raise ValueError(
            f"Morph '{morph['name']}' has negative vertex ID: {min_id}"
        )

    if max_id >= len(base_vertices):
        raise ValueError(
            f"Morph '{morph['name']}' references vertex {max_id}, "
            f"but OBJ only has {len(base_vertices)} vertices"
        )
        
        
def apply_morph(base_vertices, morph):
    new_vertices = list(base_vertices)

    for vertex_id, dx, dy, dz in morph["vertices"]:
        x, y, z = new_vertices[vertex_id]
        new_vertices[vertex_id] = (x + dx, y + dy, z + dz)

    return new_vertices

            
def get_shape_vertex_requirement(shape):
    max_id = -1

    for morph in shape["morphs"]:
        for vertex_id, dx, dy, dz in morph["vertices"]:
            max_id = max(max_id, vertex_id)

    return max_id + 1 if max_id >= 0 else 0


def print_shape_vertex_requirements(shapes):
    print("\n=== Shape Vertex Requirements ===")

    for shape in shapes:
        required = get_shape_vertex_requirement(shape)

        print(
            f"{shape['name']}: "
            f"needs at least {required} vertices, "
            f"{len(shape['morphs'])} morphs"
        )


def print_obj_tri_match_report(obj_groups, shapes):
    print("\n=== OBJ/TRI Match Report ===")

    for shape in shapes:
        shape_name = shape["name"]
        required = get_shape_vertex_requirement(shape)

        print(f"\nTRI shape: {shape_name}")
        print(f"  TRI requires: {required}")

        if shape_name in obj_groups:
            count = len(obj_groups[shape_name]["vertices"])
            print(f"  OBJ group verts: {count}")

            if count == required:
                print("  MATCH: exact vertex count")
            elif count > required:
                print("  WARNING: OBJ has extra verts")
            else:
                print("  BAD: OBJ has too few verts")
        else:
            print("  BAD: no matching OBJ group")
# ----------------------------
# Main test section
# ----------------------------

if __name__ == "__main__":
    tri_path = r"lingerie.tri"
    base_obj_path = r"Base.obj"
    morph_name = "Waist"

    out_obj_path = rf"{morph_name}.obj"


    shapes, has_uv_section = read_tri(tri_path)

    print(f"Shapes: {len(shapes)}")
    print(f"UV section likely present: {has_uv_section}")
    print_shape_vertex_requirements(shapes)

 #   print_sanity_report(shapes)
