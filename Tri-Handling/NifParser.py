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

        vertices, partition_infos = read_skin_partition_vertices(
            path,
            partition_block,
        )

        shapes.append({
            "name": name,
            "block_index": tri_block["index"],
            "skin_block_index": skin_block["index"],
            "partition_block_index": partition_block["index"],
            "vertices": vertices,
            "partition_infos": partition_infos,
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


def read_header_probe(path):
    
    header = read_nif_header(path)

    print("Header line:", header["header_line"])
    print("Version:", decode_version(header["version"]))
    print("Endian:", header["endian"])
    print("User version:", header["user_version"])
    print("User version 2:", header["user_version_2"])

    print("\nBSTriShape blocks:")
    for block in header["tri_shapes"]:
        name = get_bstrishape_name(
            path,
            block,
            header["strings"]
        )

        print(
            f"  block {block['index']}: "
            f"{name} "
            f"(offset=0x{block['offset']:X})"
        )
   
            
            
            
            
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