from pathlib import Path

from TriParser import read_tri, get_shape_vertex_requirement
from NifParser import read_nif_shapes


def main():
    nif_path = Path(r"outfit.nif")
    tri_path = Path(r"lingerie.tri")

    nif_shapes = read_nif_shapes(nif_path)
    tri_shapes, has_uv = read_tri(tri_path)
    
    report_shape_details(nif_shapes, tri_shapes, get_shape_vertex_requirement)

    nif_by_name = {s["name"]: s for s in nif_shapes}
    tri_by_name = {s["name"]: s for s in tri_shapes}

    print("\n=== Shape Match Report ===")

    for name, nif_shape in nif_by_name.items():
        tri_shape = tri_by_name.get(name)

        if not tri_shape:
            print(f"{name}: no matching TRI shape")
            continue

        required = get_shape_vertex_requirement(tri_shape)
        actual = len(nif_shape["vertices"])

        status = "MATCH" if required == actual else "MISMATCH"

        print(
            f"{name}: "
            f"NIF vertices={actual}, "
            f"TRI requires={required} "
            f"[{status}]"
        )
        
        
def report_shape_details(nif_shapes, tri_shapes, get_shape_vertex_requirement):
    tri_by_name = {
        shape["name"]: shape
        for shape in tri_shapes
    }
    tri_max_id = -1

    print("\n=== Detailed Shape Report ===")

    for nif_shape in nif_shapes:
        name = nif_shape["name"]
        nif_count = len(nif_shape["vertices"])

        print(f"\nShape: {name}")
        print(f"  NIF vertices: {nif_count}")

        partition_infos = nif_shape.get("partition_infos", [])
        if partition_infos:
            print("  NIF partitions:")
            for p in partition_infos:
                print(
                    f"    partition {p['partition_index']}: "
                    f"{p['vertex_count']} verts, "
                    f"stride {p['vertex_stride']}"
                )

        tri_shape = tri_by_name.get(name)

        if not tri_shape:
            print("  TRI: missing")
            continue

        tri_required = get_shape_vertex_requirement(tri_shape)

        print(f"  TRI required vertices: {tri_required}")

        if nif_count == tri_required:
            print("  Status: MATCH")
        else:
            tri_max_id = get_tri_max_vertex_id(tri_shape)

            print("  Status: MISMATCH")
            print(f"  TRI max vertex ID: {tri_max_id}")
            print(f"  TRI required vertices: {tri_max_id + 1}")
            print(f"  NIF max vertex ID: {nif_count - 1}")
            print(f"  NIF vertices: {nif_count}")
            print(f"  Difference: {nif_count - (tri_max_id + 1)}")
            
            
def get_tri_max_vertex_id(tri_shape):
    tri_max_id = -1

    for morph in tri_shape["morphs"]:
        for vertex_id, dx, dy, dz in morph["vertices"]:
            tri_max_id = max(tri_max_id, vertex_id)

    return tri_max_id

if __name__ == "__main__":
    main()