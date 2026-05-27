from pathlib import Path

from TriParser import read_tri, get_shape_vertex_requirement
from NifParser import read_nif_shapes, validate_faces, read_vertices_with_position_offset, read_nif_header
from ObjWriter import write_obj

def main():
    nif_path = Path(r"torso_1.nif")
    tri_path = Path(r"torso.tri")
#    out_path = r"debug_vertices.obj"
    
#    out_dir = Path(r"test")
#    out_dir.mkdir(parents=True, exist_ok=True)
#    morph_name = "Waist"


    nif_shapes = read_nif_shapes(nif_path)
    tri_shapes, has_uv = read_tri(tri_path)
    print_tri_morphs_by_shape(tri_shapes)
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
#    header = read_nif_header(nif_path)
#    blocks = header["blocks"]

    
#    report_shape_details(nif_shapes, tri_shapes, get_shape_vertex_requirement)

#    nif_by_name = {s["name"]: s for s in nif_shapes}
#    tri_by_name = {s["name"]: s for s in tri_shapes}

#    print("\n=== Shape Match Report ===")


#    first_shape = next(s for s in nif_shapes if s["name"] == "bra1")
#    partition_block = blocks[first_shape["partition_block_index"]]
    
#    probe_vertex_position_offsets(
#        nif_path,
#        partition_block,
#        vertex_index=0,
#    )
#    bad_index = 666  # replace with Blender-selected vertex index

#    print("\nBad vertex comparison:")
#    print("  global vertex:", first_shape["vertices"][bad_index])
#    print("  obj vertex:", first_shape["obj_vertices"][bad_index])
    
#    probe_vertex_position_offsets(
#        nif_path,
#        partition_block,
#        vertex_index=bad_index,
#    )

#    print("Export debug:")
#    print("  global vertices:", len(first_shape["vertices"]))
#    print("  obj vertices:", len(first_shape["obj_vertices"]))
#    print("  faces:", len(first_shape["faces"]))

#    print("  first global verts:")
#    for v in first_shape["vertices"][:5]:
#        print("   ", v)

#    print("  first obj verts:")
#    for v in first_shape["obj_vertices"][:5]:
#        print("   ", v)

#    print("  first faces:")
#    for f in first_shape["faces"][:10]:
#        print("   ", f)

#    blender_verts = read_obj_vertices(r"Bra1_Proper.obj")

#    compare_vertices(
#        [nif_to_blender_obj_space(v) for v in first_shape["obj_vertices"]],
#        blender_verts,
#    )

#
#    print("MAIN CHECK")
#    for idx in [25, 26, 27, 28, 34, 35]:
#        print(idx, first_shape["obj_vertices"][idx])

#    write_obj(
#        out_path,
#        first_shape["vertices"],
#        first_shape["faces"],
#        object_name=first_shape["name"],
#    )
    
#    diff_obj_vertices(
#        r"debug_vertices.obj",
#        r"Bra1_Proper.obj",
#    )
    
#    for name, nif_shape in nif_by_name.items():
#        tri_shape = tri_by_name.get(name)

#        if not tri_shape:
#            print(f"{name}: no matching TRI shape")
#            continue

#        required = get_shape_vertex_requirement(tri_shape)
#        actual = len(nif_shape["vertices"])

#        status = "MATCH" if required == actual else "MISMATCH"

#        print(
#            f"{name}: "
#            f"NIF vertices={actual}, "
#            f"TRI requires={required} "
#            f"[{status}]"
#        )
        
#        for shape in nif_shapes:
#            validate_faces(
#                shape["vertices"],
#                shape["faces"],
#                shape["name"]
#            )
#            print(
#                f"{shape['name']}: "
#                f"{len(shape['vertices'])} verts, "
#                f"{len(shape['faces'])} faces"
#            )
            
            
#        first_shape = nif_shapes[0]
        
#        print("Exporting:")
#        print("  global/TRI vertices:", len(first_shape["vertices"]))
#        print("  OBJ/render vertices:", len(first_shape["obj_vertices"]))
#        print("  faces:", len(first_shape["faces"]))
#        print("  max face:", max(max(f) for f in first_shape["faces"]))


        

                
#        first_shape = nif_shapes[0]

#        write_vertices_only_obj(
#            out_path,
#            first_shape["vertices"],
#            object_name=first_shape["name"],
#        )

#        print(f"Wrote debug OBJ: {out_path}")


def print_tri_morphs_by_shape(tri_shapes):
    print("\n=== TRI Morphs By Shape ===")

    for shape in tri_shapes:
        print(f"\nShape: {shape['name']}")
        print(f"  Morph count: {len(shape['morphs'])}")

        for morph in shape["morphs"]:
            print(
                f"    {morph['name']} "
                f"({len(morph['vertices'])} changed verts)"
            )       
        
def report_shape_details(nif_shapes, tri_shapes, get_shape_vertex_requirement):
    tri_by_name = {
        shape["name"]: shape
        for shape in tri_shapes
    }
    tri_max_id = -1

#    print("\n=== Detailed Shape Report ===")

    for nif_shape in nif_shapes:
        name = nif_shape["name"]
        nif_count = len(nif_shape["vertices"])

#        print(f"\nShape: {name}")
#        print(f"  NIF vertices: {nif_count}")

        partition_infos = nif_shape.get("partition_infos", [])
#        if partition_infos:
#            print("  NIF partitions:")
#            for p in partition_infos:
#                print(
#                    f"    partition {p['partition_index']}: "
#                    f"{p['vertex_count']} verts, "
#                    f"{p['triangle_count']} triangles, "
#                    f"{p['bone_count']} bones"
#                )

        tri_shape = tri_by_name.get(name)

        if not tri_shape:
            print("  TRI: missing")
            continue

        tri_required = get_shape_vertex_requirement(tri_shape)

#        print(f"  TRI required vertices: {tri_required}")

#        if nif_count == tri_required:
#            print("  Status: MATCH")
#        else:
#            tri_max_id = get_tri_max_vertex_id(tri_shape)

#            print("  Status: MISMATCH")
#            print(f"  TRI max vertex ID: {tri_max_id}")
#            print(f"  TRI required vertices: {tri_max_id + 1}")
#            print(f"  NIF max vertex ID: {nif_count - 1}")
#            print(f"  NIF vertices: {nif_count}")
#            print(f"  Difference: {nif_count - (tri_max_id + 1)}")
            
            
def get_tri_max_vertex_id(tri_shape):
    tri_max_id = -1

    for morph in tri_shape["morphs"]:
        for vertex_id, dx, dy, dz in morph["vertices"]:
            tri_max_id = max(tri_max_id, vertex_id)

    return tri_max_id


def apply_morph(vertices, morph):
    morphed = list(vertices)

    for vertex_id, dx, dy, dz in morph["vertices"]:
        x, y, z = morphed[vertex_id]
        morphed[vertex_id] = (x + dx, y + dy, z + dz)

    return morphed

def read_obj_vertices(path):
    verts = []

    with open(path, "r", encoding="utf-8", errors="replace") as f:
        for line in f:
            if line.startswith("v "):
                _, x, y, z = line.split()[:4]
                verts.append((float(x), float(y), float(z)))

    return verts


def compare_vertices(ours, blender, limit=20, tolerance=0.0001):
    mismatches = 0

    for i, (a, b) in enumerate(zip(ours, blender)):
        dx = abs(a[0] - b[0])
        dy = abs(a[1] - b[1])
        dz = abs(a[2] - b[2])

        if dx > tolerance or dy > tolerance or dz > tolerance:
            print(f"Mismatch at vertex {i}:")
            print(f"  ours:    {a}")
            print(f"  blender: {b}")
            mismatches += 1

            if mismatches >= limit:
                break

    print(f"Compared {min(len(ours), len(blender))} vertices")
    print(f"Mismatches shown: {mismatches}")
    
def nif_to_blender_obj_space(v):
    x, y, z = v
    return (x * 0.1, z * 0.1, -y * 0.1)


def diff_obj_vertices(our_obj_path, blender_obj_path, tolerance=0.0001, limit=50):
    our_verts_raw = read_obj_vertices(our_obj_path)
    blender_verts = read_obj_vertices(blender_obj_path)

    our_verts = [
        nif_to_blender_obj_space(v)
        for v in our_verts_raw
    ]

    print("\n=== OBJ Vertex Diff ===")
    print(f"Our verts: {len(our_verts)}")
    print(f"Blender verts: {len(blender_verts)}")

    mismatch_count = 0
    max_error = 0.0
    max_error_index = None

    for i, (ours, blender) in enumerate(zip(our_verts, blender_verts)):
        dx = abs(ours[0] - blender[0])
        dy = abs(ours[1] - blender[1])
        dz = abs(ours[2] - blender[2])

        error = max(dx, dy, dz)

        if error > max_error:
            max_error = error
            max_error_index = i

        if error > tolerance:
            mismatch_count += 1

            if mismatch_count <= limit:
                print(f"\nMismatch {mismatch_count} at vertex {i}:")
                print(f"  ours:    {ours}")
                print(f"  blender: {blender}")
                print(f"  delta:   ({dx:.6f}, {dy:.6f}, {dz:.6f})")

    if len(our_verts) != len(blender_verts):
        print(
            f"\nVertex count mismatch: "
            f"ours={len(our_verts)}, blender={len(blender_verts)}"
        )

    print(f"\nTotal mismatches: {mismatch_count}")
    print(f"Max error: {max_error:.6f} at vertex {max_error_index}")
    
    
    
if __name__ == "__main__":
    main()