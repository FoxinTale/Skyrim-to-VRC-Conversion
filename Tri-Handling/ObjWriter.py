from pathlib import Path


def write_obj(path, vertices, faces, object_name="Mesh"):
    from pathlib import Path

    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True)

    if path.suffix.lower() == ".nif":
        raise ValueError("Refusing to write OBJ data over a .nif file.")

    with path.open("w", encoding="utf-8", newline="\n") as f:
        f.write(f"o {object_name}\n")

        for x, y, z in vertices:
            f.write(f"v {x:.6f} {y:.6f} {z:.6f}\n")

        for _ in vertices:
            f.write("vt 0.000000 0.000000\n")

        for _ in vertices:
            f.write("vn 0.000000 0.000000 1.000000\n")

        for a, b, c in faces:
            a += 1
            b += 1
            c += 1
            f.write(f"f {a}/{a}/{a} {b}/{b}/{b} {c}/{c}/{c}\n")