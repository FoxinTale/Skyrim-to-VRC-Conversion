import tkinter as tk
from tkinter import filedialog


class MainWindow(tk.Tk):

    def __init__(self):
        super().__init__()
        self.title("TRI Extractor")
        self.geometry("640x480")
        self.build_ui()
         
         
    def build_ui(self):
        # -----------------------------
        # TRI FILE ROW
        # -----------------------------
        tri_frame = tk.Frame(self)
        tri_frame.pack(padx=10, pady=(10, 5))

        tk.Label(tri_frame, text="TRI File:").pack(side="left")

        self.tri_entry = tk.Entry(tri_frame)
        self.tri_entry.pack(side="left", fill="x", expand=True, padx=5)

        tk.Button(
            tri_frame,
            text="Browse...",
            command=self.browse_tri
        ).pack(side="left")

        # -----------------------------
        # NIF FILE ROW
        # -----------------------------
        nif_frame = tk.Frame(self)
        nif_frame.pack(padx=10, pady=(0, 10))

        tk.Label(nif_frame, text="NIF File:").pack(side="left")

        self.nif_entry = tk.Entry(nif_frame)
        self.nif_entry.pack(side="left", fill="x", expand=True, padx=5)

        tk.Button(
            nif_frame,
            text="Browse...",
            command=self.browse_nif
        ).pack(side="left")


        self.log_box = tk.Text(self, height=10)
        self.log_box.pack(fill="both", expand=True, padx=10, pady=10)
        
        # ---------------------------------
    # FILE BROWSERS
    # ---------------------------------

    def browse_tri(self):

        path = filedialog.askopenfilename(
            title="Select TRI File",
            filetypes=[("TRI Files", "*.tri"), ("All Files", "*.*")]
        )

        if path:
            self.tri_entry.delete(0, tk.END)
            self.tri_entry.insert(0, path)

    def browse_nif(self):

        path = filedialog.askopenfilename(
            title="Select NIF File",
            filetypes=[("NIF Files", "*.nif"), ("All Files", "*.*")]
        )

        if path:
            self.nif_entry.delete(0, tk.END)
            self.nif_entry.insert(0, path)   

def log(self, message):
    self.log_box.insert(tk.END, message + "\n")
    self.log_box.see(tk.END)
    
    
if __name__ == "__main__":
    app = MainWindow()
    app.mainloop()