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
# Main test section
# ----------------------------

if __name__ == "__main__":
    tri_path = r"lingerie.tri"
    base_obj_path = r"Base.obj"
    morph_name = "Waist"

    out_obj_path = rf"{morph_name}.obj"
    shapes, has_uv_section = read_tri(tri_path)
