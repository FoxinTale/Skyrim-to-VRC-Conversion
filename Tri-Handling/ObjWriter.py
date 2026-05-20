from pathlib import Path


def write_obj(path, vertices, faces, object_name="Mesh"):
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True)

    with path.open("w", encoding="utf-8", newline="\n") as f:
        f.write(f"o {object_name}\n")

        for x, y, z in vertices:
            f.write(f"v {x:.6f} {y:.6f} {z:.6f}\n")

        for a, b, c in faces:
            # OBJ uses 1-based indices
            f.write(f"f {a + 1} {b + 1} {c + 1}\n")