from pathlib import Path

from TriParser import read_tri
from NifParser import read_nif_shapes
from ObjWriter import write_obj

def main():
    nif_path = Path(r"torso_1.nif")
    tri_path = Path(r"torso.tri")


    nif_shapes = read_nif_shapes(nif_path)
    tri_shapes, has_uv = read_tri(tri_path)
 
    tri_by_name = {
        shape["name"]: shape
        for shape in tri_shapes
    }

    tri_by_name = {shape["name"]: shape for shape in tri_shapes}

    out_root = Path(r"morph_exports")
    out_root.mkdir(parents=True, exist_ok=True)

    for nif_shape in nif_shapes:
        shape_name = nif_shape["name"]
        tri_shape = tri_by_name.get(shape_name)

        if not tri_shape:
            print(f"Skipping {shape_name}: no matching TRI shape")
            continue

        shape_dir = out_root / shape_name
        shape_dir.mkdir(parents=True, exist_ok=True)

        # Base shape, no prefix.
        write_obj(
            shape_dir / "base.obj",
            nif_shape["vertices"],
            nif_shape["faces"],
            object_name=shape_name,
        )

        for morph in tri_shape["morphs"]:
            morphed_vertices = apply_morph(
                nif_shape["vertices"],
                morph,
            )

            out_path = shape_dir / f"{morph['name']}.obj"

            write_obj(
                out_path,
                morphed_vertices,
                nif_shape["faces"],
                object_name=morph["name"],
            )

            print(f"Wrote {out_path}")

      
def apply_morph(vertices, morph):
    morphed = list(vertices)

    for vertex_id, dx, dy, dz in morph["vertices"]:
        x, y, z = morphed[vertex_id]
        morphed[vertex_id] = (x + dx, y + dy, z + dz)

    return morphed

if __name__ == "__main__":
    main()