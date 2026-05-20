import struct
from pathlib import Path


class NifError(Exception):
    pass


class UnsupportedNifError(NifError):
    pass


def read_exact(f, size, label):
    data = f.read(size)
    if len(data) != size:
        raise EOFError(
            f"While reading {label}: expected {size} bytes, got {len(data)}. "
            f"Offset: 0x{f.tell():X}"
        )
    return data


def read_u8(f, label="u8"):
    return struct.unpack("<B", read_exact(f, 1, label))[0]


def read_u16(f, label="u16"):
    return struct.unpack("<H", read_exact(f, 2, label))[0]


def read_u32(f, label="u32"):
    return struct.unpack("<I", read_exact(f, 4, label))[0]


def read_i32(f, label="i32"):
    return struct.unpack("<i", read_exact(f, 4, label))[0]


def read_string_u32(f, label="string"):
    length = read_u32(f, f"{label} length")
    data = read_exact(f, length, label)
    return data.decode("utf-8", errors="replace")

def read_i32_at(path, offset):
    with Path(path).open("rb") as f:
        f.seek(offset)
        return read_i32(f, "block ref")

def read_bool_u8(f, label):
    return read_u8(f, label) != 0


def read_triangle(f, label):
    a = read_u16(f, f"{label} a")
    b = read_u16(f, f"{label} b")
    c = read_u16(f, f"{label} c")
    return (a, b, c)


def read_skin_partition_mesh(path, partition_block):
    vertices = []
    faces = []
    partition_infos = []

    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")

        data_size = read_u32(f, "vertex data size")
        vertex_size = read_u32(f, "vertex size")
        vertex_desc = read_exact(f, 8, "vertex desc")

        if vertex_size not in {32, 40}:
            raise ValueError(f"Unexpected vertex size: {vertex_size}")

        vertex_count = data_size // vertex_size
        vertex_buffer_start = f.tell()

#        raw_vertices = []
#        final_vertices = [None] * vertex_count
#        raw_vertex_cursor = 0
        
        for i in range(vertex_count):
            base = vertex_buffer_start + i * vertex_size
            f.seek(base + 8)

            x = struct.unpack("<f", read_exact(f, 4, f"vertex {i} x"))[0]
            y = struct.unpack("<f", read_exact(f, 4, f"vertex {i} y"))[0]
            z = struct.unpack("<f", read_exact(f, 4, f"vertex {i} z"))[0]

            vertices.append((x, y, z))
#            raw_vertices.append((x, y, z))
        f.seek(vertex_buffer_start + data_size)

        for p in range(num_partitions):
            num_vertices = read_u16(f, f"partition {p} num vertices")
            num_triangles = read_u16(f, f"partition {p} num triangles")
            num_bones = read_u16(f, f"partition {p} num bones")
            num_strips = read_u16(f, f"partition {p} num strips")
            num_weights_per_vertex = read_u16(f, f"partition {p} weights per vertex")

            bones = [
                read_u16(f, f"partition {p} bone {i}")
                for i in range(num_bones)
            ]

            has_vertex_map = read_bool_u8(f, f"partition {p} has vertex map")
            vertex_map = []
            if has_vertex_map:
                vertex_map = [
                    read_u16(f, f"partition {p} vertex map {i}")
                    for i in range(num_vertices)
                ]
                
            if vertex_map:
                local_vertices = [vertices[global_index] for global_index in vertex_map]
            else:
                local_vertices = vertices

            has_vertex_weights = read_bool_u8(f, f"partition {p} has vertex weights")
            if has_vertex_weights:
                read_exact(
                    f,
                    num_vertices * 16,
                    f"partition {p} vertex weights",
                )

            strip_lengths = [
                read_u16(f, f"partition {p} strip length {i}")
                for i in range(num_strips)
            ]
            
            has_faces = read_bool_u8(f, f"partition {p} has faces")
            partition_faces = []

            # mapped triangles/strips section
            if has_faces:
                if num_strips > 0:
                    for strip_index, strip_len in enumerate(strip_lengths):
                        read_exact(
                            f,
                            strip_len * 2,
                            f"partition {p} strip {strip_index}",
                        )
                else:
                    for i in range(num_triangles):
                        partition_faces.append(
                        read_triangle(f, f"partition {p} mapped triangle {i}")
                    )
                '''
                if num_strips > 0:
                    for strip_index, strip_len in enumerate(strip_lengths):
                        read_exact(
                            f,
                            strip_len * 2,
                            f"partition {p} strip {strip_index}",
                        )
                else:
                    for i in range(num_triangles):
                        read_triangle(
                            f,
                            f"partition {p} mapped triangle {i}"
                        )
'''
            has_bone_indices = read_bool_u8(f, f"partition {p} has bone indices")
            if has_bone_indices:
                read_exact(
                    f,
                    num_vertices * 4,
                    f"partition {p} bone indices",
                )

            lod_level = read_u8(f, f"partition {p} lod level")
            global_vb = read_bool_u8(f, f"partition {p} global vb")

            partition_vertex_desc = read_exact(
                f,
                8,
                f"partition {p} vertex desc",
            )

            # Consume trueTriangles only for alignment.
            true_faces = []

            for i in range(num_triangles):
                true_faces.append(
                    read_triangle(f, f"partition {p} true triangle {i}")
                )

            if partition_faces:
                faces.extend(partition_faces)
            else:
                faces.extend(true_faces)

#            local_vertices = raw_vertices[
#                raw_vertex_cursor: raw_vertex_cursor + num_vertices
#            ]

#            raw_vertex_cursor += num_vertices

#            if vertex_map:
#                for local_index, global_index in enumerate(vertex_map):
#                    final_vertices[global_index] = local_vertices[local_index]
#            else:
#                start = raw_vertex_cursor - num_vertices
#                for local_index, vertex in enumerate(local_vertices):
#                    final_vertices[start + local_index] = vertex
        
            partition_infos.append({
                "partition_index": p,
                "vertex_count": num_vertices,
                "triangle_count": num_triangles,
                "bone_count": num_bones,
                "strip_count": num_strips,
                "weights_per_vertex": num_weights_per_vertex,
                "has_vertex_map": has_vertex_map,
                "has_vertex_weights": has_vertex_weights,
                "has_faces": has_faces,
                "has_bone_indices": has_bone_indices,
                "face_count": len(partition_faces),
            })
          
          
    return{
        "vertices": local_vertices,
        "faces": partition_faces,
        "partition_infos": partition_infos,
        "vertex_size": vertex_size,
        "vertex_count": len(local_vertices),
        "face_count": len(partition_faces),
    } 
        
#    return {
#        "vertices": vertices,
#        "faces": faces,
#        "partition_infos": partition_infos,
#        "vertex_size": vertex_size,
#        "vertex_count": len(vertices),
#        "face_count": len(faces),
#    }
 

def validate_faces(vertices, faces, shape_name):
    max_vert = len(vertices) - 1
    bad = []

    for i, (a, b, c) in enumerate(faces):
        if a > max_vert or b > max_vert or c > max_vert:
            bad.append((i, (a, b, c)))

    print(f"\nFace validation for {shape_name}:")
    print(f"  vertices: {len(vertices)}")
    print(f"  faces: {len(faces)}")
    print(f"  max face index: {max(max(face) for face in faces) if faces else 'none'}")
    print(f"  bad faces: {len(bad)}")

    if bad:
        print("  first bad faces:")
        for item in bad[:10]:
            print(f"    {item}")

            
def read_nif_shapes(path):
    header = read_nif_header(path)

    shapes = []

    for tri_block in header["tri_shapes"]:
        name = get_bstrishape_name(
            path,
            tri_block,
            header["strings"]
        )

        skin_block, skin_offset = find_ref_by_candidate_offsets(
            path,
            tri_block,
            header["blocks"],
            "BSDismemberSkinInstance",
            offsets=[0x58, 0x5C, 0x60, 0x54, 0x64],
        )

        if not skin_block:
            raise ValueError(f"Could not find skin instance for shape {name}")

        partition_block = get_valid_ref_at(
            path,
            skin_block,
            0x04,
            header["blocks"],
            "NiSkinPartition",
        )

        if not partition_block:
            raise ValueError(f"Could not find NiSkinPartition for shape {name}")


        mesh = read_skin_partition_mesh(path, partition_block)

        vertices = mesh["vertices"]
        faces = mesh["faces"]
        mesh = read_skin_partition_mesh(path, partition_block)

        shapes.append({
            "name": name,
            "block_index": tri_block["index"],
            "skin_block_index": skin_block["index"],
            "partition_block_index": partition_block["index"],
            "vertices": mesh["vertices"],
            "faces": mesh["faces"],
            "partition_infos": mesh["partition_infos"],
        })

    return shapes
	
def read_nif_header(path):
    path = Path(path)

    with path.open("rb") as f:
        header_line = f.readline().decode("ascii", errors="replace").strip()

        version_raw = read_u32(f, "version")
        endian = read_u8(f, "endian")
        user_version = read_u32(f, "user version")

        num_blocks = read_u32(f, "block count")
        user_version_2 = read_u32(f, "user version 2")

        export_info_count = read_u16(f, "export info count")

        export_info = []
        for i in range(export_info_count):
            length = read_u8(f, f"export info {i} length")
            text = read_exact(f, length, f"export info {i}").decode(
                "utf-8",
                errors="replace",
            )
            export_info.append(text)

        post_export_field = read_u16(f, "post-export field")

        block_type_count = read_u16(f, "block type count")

        block_types = []
        for i in range(block_type_count):
            name = read_string_u32(f, f"block type {i}")
            block_types.append(name)

        block_type_indices = []
        for i in range(num_blocks):
            block_type_indices.append(read_u16(f, f"block {i} type index"))

        block_sizes = []
        for i in range(num_blocks):
            block_sizes.append(read_u32(f, f"block {i} size"))

        string_count = read_u32(f, "string count")
        max_string_length = read_u32(f, "max string length")

        strings = []
        for i in range(string_count):
            strings.append(read_string_u32(f, f"string {i}"))

        group_count = read_u32(f, "group count")

        groups = []
        for i in range(group_count):
            groups.append(read_u32(f, f"group {i}"))

        block_offsets = []
        offset = f.tell()

        for size in block_sizes:
            block_offsets.append(offset)
            offset += size

        blocks = []

        for i in range(num_blocks):
            type_index = block_type_indices[i]
            type_name = block_types[type_index]

            blocks.append({
                "index": i,
                "type_index": type_index,
                "type_name": type_name,
                "size": block_sizes[i],
                "offset": block_offsets[i],
            })

        tri_shapes = [
            block for block in blocks
            if block["type_name"] == "BSTriShape"
        ]

        return {
            "path": path,
            "header_line": header_line,
            "version": version_raw,
            "endian": endian,
            "user_version": user_version,
            "user_version_2": user_version_2,
            "num_blocks": num_blocks,
            "export_info": export_info,
            "post_export_field": post_export_field,
            "block_types": block_types,
            "block_type_indices": block_type_indices,
            "block_sizes": block_sizes,
            "strings": strings,
            "max_string_length": max_string_length,
            "group_count": group_count,
            "groups": groups,
            "blocks": blocks,
            "tri_shapes": tri_shapes,
        }
    
    

def get_block_ref_at(path, block, rel_offset, blocks):
    ref = read_i32_at(path, block["offset"] + rel_offset)

    if ref < 0:
        return None

    if ref >= len(blocks):
        raise ValueError(
            f"Bad block ref {ref} in block {block['index']} at +0x{rel_offset:X}"
        )

    return blocks[ref]
    
    
def get_valid_ref_at(path, block, rel_offset, blocks, expected_type):
    if rel_offset + 4 > block["size"]:
        return None

    ref = read_i32_at(path, block["offset"] + rel_offset)

    if ref < 0 or ref >= len(blocks):
        return None

    target = blocks[ref]

    if target["type_name"] != expected_type:
        return None

    return target
 

def find_ref_by_candidate_offsets(path, block, blocks, expected_type, offsets):
    for rel_offset in offsets:
        target = get_valid_ref_at(path, block, rel_offset, blocks, expected_type)
        if target:
            return target, rel_offset

    return None, None

    
def decode_version(v):
    return (
        (v >> 24) & 0xFF,
        (v >> 16) & 0xFF,
        (v >> 8) & 0xFF,
        v & 0xFF,
    )


def probe_after_vertex_buffer(path, partition_block, byte_count=128):
    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")
        vertex_data_size = read_u32(f, "vertex data size")
        vertex_stride = read_u32(f, "vertex stride")

        vertex_buffer_start = f.tell()
        after_vertex_buffer = vertex_buffer_start + vertex_data_size

        f.seek(after_vertex_buffer)
        data = f.read(min(byte_count, partition_block["size"] - (after_vertex_buffer - partition_block["offset"])))

#    print(
#        f"\nAfter vertex buffer for NiSkinPartition block {partition_block['index']}:"
#    )
#    print(f"  num partitions: {num_partitions}")
#    print(f"  vertex data size: {vertex_data_size}")
#    print(f"  vertex stride: {vertex_stride}")
#    print(f"  vertex buffer start: 0x{vertex_buffer_start:X}")
#    print(f"  after vertex buffer: 0x{after_vertex_buffer:X}")

#    print("\n  Word decode:")
    for i in range(0, len(data) - 3, 4):
        chunk = data[i:i + 4]
        u32 = struct.unpack("<I", chunk)[0]
        i32 = struct.unpack("<i", chunk)[0]
        f32 = struct.unpack("<f", chunk)[0]

#        print(
#            f"  +0x{i:02X}: "
#            f"u32={u32:<10} "
#            f"i32={i32:<10} "
#            f"f32={f32:g}"
#        )

#    print("\n  As u16 pairs/triples:")
    for i in range(0, min(len(data), 96) - 5, 6):
        a, b, c = struct.unpack("<HHH", data[i:i + 6])
#        print(f"  +0x{i:02X}: ({a}, {b}, {c})")

def probe_vertex_record_layout(path, partition_block, record_count=3):
    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")
        data_size = read_u32(f, "vertex data size")
        vertex_size = read_u32(f, "vertex size")
        vertex_desc = read_exact(f, 8, "vertex desc")

        start = f.tell()

        print(f"vertex_size={vertex_size}")

        for i in range(record_count):
            base = start + i * vertex_size
            print(f"\nVertex record {i}:")

            for off in range(0, vertex_size - 3, 4):
                f.seek(base + off)
                value = struct.unpack("<f", read_exact(f, 4, "float probe"))[0]
                print(f"  +0x{off:02X}: {value}")

def read_header_probe(path):
    header = read_nif_header(path)

    print("Header line:", header["header_line"])
    print("Version:", decode_version(header["version"]))
    print("Endian:", header["endian"])
    print("User version:", header["user_version"])
    print("User version 2:", header["user_version_2"])
    print("Block count:", header["num_blocks"])

    print("\nBSTriShape blocks:")

    for tri_block in header["tri_shapes"]:
        name = get_bstrishape_name(
            path,
            tri_block,
            header["strings"],
        )

#        print(
#            f"\nBSTriShape block {tri_block['index']}: {name} "
#            f"size={tri_block['size']} "
#            f"offset=0x{tri_block['offset']:X}"
#        )
        


        skin_block, skin_offset = find_ref_by_candidate_offsets(
            path,
            tri_block,
            header["blocks"],
            "BSDismemberSkinInstance",
            offsets=[0x58, 0x5C, 0x60, 0x54, 0x64],
        )

        if not skin_block:
            print("  No valid BSDismemberSkinInstance found")
            continue

        skin_data_block = get_valid_ref_at(
            path,
            skin_block,
            0x00,
            header["blocks"],
            "NiSkinData",
        )

        partition_block = get_valid_ref_at(
            path,
            skin_block,
            0x04,
            header["blocks"],
            "NiSkinPartition",
        )

#        print(
#            f"  Skin ref +0x{skin_offset:X} "
#            f"→ SkinInstance {skin_block['index']}"
#        )

        if skin_data_block:
            print(f"  NiSkinData: {skin_data_block['index']}")

        if not partition_block:
            print("  No valid NiSkinPartition found")
            continue

#        print(f"  NiSkinPartition: {partition_block['index']}")


        vertices, partition_infos = read_skin_partition_vertices(
            path,
            partition_block,
        )

#        print(f"  Read vertices: {len(vertices)}")

#        for info in partition_infos:
#            print(
#                f"    partition {info['partition_index']}: "
#                f"{info['vertex_count']} verts, "
#                f"stride {info['vertex_stride']}, "
#                f"reported partitions {info.get('reported_num_partitions')}"
#            )

#        probe_after_vertex_buffer(
#            path,
#            partition_block,
#            byte_count=160,
#        )
        
#        probe_after_vertex_buffer_u16(
#            path,
#            partition_block
#        )
        
#        probe_triangle_candidates(
#            path,
#            partition_block,
#            max_offsets=512,
#            preview_faces=8,
#       )

        probe_post_vertex_words(path, partition_block)
        
def probe_post_vertex_words(path, partition_block, byte_count=256):
    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")
        vertex_data_size = read_u32(f, "vertex data size")
        vertex_stride = read_u32(f, "vertex stride")

        vertex_buffer_start = f.tell()
        after_vertex_buffer = vertex_buffer_start + vertex_data_size

        f.seek(after_vertex_buffer)
        data = f.read(byte_count)

    print("\nPost-vertex u32/u16 probe:")

    for rel in range(0, len(data) - 4, 4):
        u32 = struct.unpack("<I", data[rel:rel + 4])[0]
        lo, hi = struct.unpack("<HH", data[rel:rel + 4])

#        print(
#            f"+0x{rel:04X}: "
#            f"u32={u32:<10} "
#            f"u16=({lo}, {hi})"
#        )      

def probe_triangle_candidates(path, partition_block, max_offsets=256, preview_faces=8):
    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")
        vertex_data_size = read_u32(f, "vertex data size")
        vertex_stride = read_u32(f, "vertex stride")

        vertex_count = vertex_data_size // vertex_stride
        vertex_buffer_start = f.tell()
        after_vertex_buffer = vertex_buffer_start + vertex_data_size

#        print("\nTriangle candidate scan:")
#        print(f"  vertex count: {vertex_count}")
#        print(f"  scan start: 0x{after_vertex_buffer:X}")

        for rel in range(0, max_offsets, 2):
            f.seek(after_vertex_buffer + rel)

            try:
                triples = []
                valid = 0

                for _ in range(preview_faces):
                    a = read_u16(f, "tri a")
                    b = read_u16(f, "tri b")
                    c = read_u16(f, "tri c")

                    triples.append((a, b, c))

                    if a < vertex_count and b < vertex_count and c < vertex_count:
                        valid += 1

                if valid == preview_faces:
                    print(f"\nPossible triangle list at +0x{rel:X}:")
                    for tri in triples:
                        print(f"  {tri}")

            except EOFError:
                break
            
            
            
def peek_block(path, block, byte_count=120):
    path = Path(path)

    with path.open("rb") as f:
        f.seek(block["offset"])
        data = f.read(min(byte_count, block["size"]))

#    print(
#        f"\nBlock {block['index']} "
#        f"({block['type_name']}), "
#        f"size={block['size']}, "
#        f"offset=0x{block['offset']:X}"
#    )
#    print(data.hex(" "))
#    print(data.decode("ascii", errors="replace"))
    
    
def decode_block_words(path, block):
    path = Path(path)

    with path.open("rb") as f:
        f.seek(block["offset"])
        data = f.read(min(32, block["size"]))

#    print(
#        f"\nBlock {block['index']} "
#        f"({block['type_name']}) word decode:"
#    )

    for i in range(0, len(data), 4):
        chunk = data[i:i+4]
        if len(chunk) < 4:
            break

        u = struct.unpack("<I", chunk)[0]
        s = struct.unpack("<i", chunk)[0]
        fl = struct.unpack("<f", chunk)[0]

#       print(
#           f"+0x{i:02X}: "
#           f"u32={u:<12} "
#           f"i32={s:<12} "
#           f"f32={fl}"
#       )

        
def scan_block_refs(path, block, max_block_index):
    path = Path(path)
    refs = []

    with path.open("rb") as f:
        f.seek(block["offset"])
        data = f.read(block["size"])

    for offset in range(0, len(data) - 3):
        value = struct.unpack("<i", data[offset:offset + 4])[0]

        if 0 <= value <= max_block_index:
            refs.append((offset, value))

    return refs


def find_first_ref_of_type(path, source_block, blocks, target_type):
    refs = scan_block_refs(path, source_block, len(blocks) - 1)

    for ref_offset, ref in refs:
        if blocks[ref]["type_name"] == target_type:
            return {
                "offset": ref_offset,
                "block": blocks[ref],
            }

    return None   
 
def peek_words(path, block, byte_count=96):
    with Path(path).open("rb") as f:
        f.seek(block["offset"])
        data = f.read(min(byte_count, block["size"]))

#    print(
#        f"\nBlock {block['index']} ({block['type_name']}), "
#        f"size={block['size']}, offset=0x{block['offset']:X}"
#    )

    for i in range(0, len(data) - 3, 4):
        chunk = data[i:i+4]
        u32 = struct.unpack("<I", chunk)[0]
        i32 = struct.unpack("<i", chunk)[0]
        f32 = struct.unpack("<f", chunk)[0]

#        print(
#            f"+0x{i:02X}: "
#            f"u32={u32:<10} "
#            f"i32={i32:<10} "
#            f"f32={f32:g}"
#        )
        
        
def probe_skin_partition_vertices(path, partition_block, count=5):
    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")
        vertex_data_size = read_u32(f, "vertex data size")
        vertex_stride = read_u32(f, "vertex stride")

        print("num partitions:", num_partitions)
        print("vertex data size:", vertex_data_size)
        print("vertex stride:", vertex_stride)

        if vertex_stride == 0:
            raise ValueError("Vertex stride is zero")

        vertex_count = vertex_data_size // vertex_stride
        print("derived vertex count:", vertex_count)

        vertex_buffer_start = f.tell()

        for i in range(min(count, vertex_count)):
            base = vertex_buffer_start + i * vertex_stride

            # Try position at +8 inside each vertex record.
            f.seek(base + 8)
            x = struct.unpack("<f", f.read(4))[0]
            y = struct.unpack("<f", f.read(4))[0]
            z = struct.unpack("<f", f.read(4))[0]

#            print(f"vertex {i}: {x:.6f}, {y:.6f}, {z:.6f}")
            
VALID_VERTEX_STRIDES = {32, 40}

def read_skin_partition_vertices(path, partition_block):
    all_vertices = []
    partition_infos = []

    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")

        vertex_data_size = read_u32(f, "vertex data size")
        vertex_stride = read_u32(f, "vertex stride")

        if vertex_stride not in VALID_VERTEX_STRIDES:
            raise ValueError(
                f"Unexpected vertex stride {vertex_stride} "
                f"in NiSkinPartition block {partition_block['index']}"
            )

        if vertex_data_size % vertex_stride != 0:
            raise ValueError(
                f"Vertex data size {vertex_data_size} is not divisible by stride {vertex_stride}"
            )

        vertex_count = vertex_data_size // vertex_stride
        vertex_buffer_start = f.tell()

        vertices = []

        for i in range(vertex_count):
            base = vertex_buffer_start + i * vertex_stride
            f.seek(base + 8)

            x = struct.unpack("<f", read_exact(f, 4, f"vertex {i} x"))[0]
            y = struct.unpack("<f", read_exact(f, 4, f"vertex {i} y"))[0]
            z = struct.unpack("<f", read_exact(f, 4, f"vertex {i} z"))[0]

            vertices.append((x, y, z))

        partition_infos.append({
            "partition_index": 0,
            "reported_num_partitions": num_partitions,
            "vertex_data_size": vertex_data_size,
            "vertex_stride": vertex_stride,
            "vertex_count": vertex_count,
            "vertices": vertices,
        })

        all_vertices.extend(vertices)

    return all_vertices, partition_infos

def get_shape_vertex_requirement(shape):
    max_id = -1

    for morph in shape["morphs"]:
        for vertex_id, dx, dy, dz in morph["vertices"]:
            max_id = max(max_id, vertex_id)

    return max_id + 1 if max_id >= 0 else 0
    
    
    
def probe_after_vertex_buffer_u16(path, partition_block, count=80):
    with Path(path).open("rb") as f:
        f.seek(partition_block["offset"])

        num_partitions = read_u32(f, "num partitions")
        vertex_data_size = read_u32(f, "vertex data size")
        vertex_stride = read_u32(f, "vertex stride")

        vertex_buffer_start = f.tell()
        after_vertex_buffer = vertex_buffer_start + vertex_data_size

        f.seek(after_vertex_buffer)

        values = []
        for i in range(count):
            values.append(read_u16(f, f"post-vertex u16 {i}"))

    print("\nPost-vertex u16 stream:")
    for i in range(0, len(values), 16):
        chunk = values[i:i + 16]
        print(f"{i:04d}: " + " ".join(f"{v:5d}" for v in chunk))

        
def probe_shape_name_indices(path, tri_shapes, strings):
    print("\n=== BSTriShape possible name indices ===")

    for block in tri_shapes:
        with Path(path).open("rb") as f:
            f.seek(block["offset"])
            data = f.read(block["size"])

        print(f"\nBSTriShape block {block['index']}:")

        for offset in range(0, len(data) - 3, 4):
            value = struct.unpack("<i", data[offset:offset + 4])[0]

            if 0 <= value < len(strings):
                s = strings[value]
                print(f"  +0x{offset:02X}: string[{value}] = {s}")
                
                
def get_bstrishape_name(path, block, strings):
    name_index = read_i32_at(path, block["offset"] + 0x00)

    if name_index < 0 or name_index >= len(strings):
        raise ValueError(
            f"BSTriShape block {block['index']} has invalid name index {name_index}"
        )

    return strings[name_index]

def report_nif_tri_shape_matches(nif_shapes, trip_shapes):
    trip_by_name = {
        shape["name"]: shape
        for shape in trip_shapes
    }

    print("\n=== NIF/TRI Shape Match Report ===")

    for nif_shape in nif_shapes:
        name = nif_shape["name"]
        nif_vert_count = len(nif_shape["vertices"])

        print(f"\nNIF shape: {name}")
        print(f"  NIF vertices: {nif_vert_count}")

        trip_shape = trip_by_name.get(name)

        if not trip_shape:
            print("  TRI match: MISSING")
            continue

        required = get_shape_vertex_requirement(trip_shape)

        print(f"  TRI required vertices: {required}")

        if required == nif_vert_count:
            print("  MATCH")
        else:
            print("  MISMATCH")

            
if __name__ == "__main__":
    nif_path = r"outfit.nif"
    read_header_probe(nif_path)