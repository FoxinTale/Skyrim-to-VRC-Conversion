import tkinter as tk
from tkinter import filedialog
from tkinter import ttk
from Extractor import extract_tri
import sys
from TextRedirector import TextRedirector


class MainWindow(tk.Tk):

    def __init__(self):
        super().__init__()
        self.option_add("*Button.bd", 2)
        self.option_add("*Button.relief", "raised")
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


        # -----------------------------
        # OUTPUT FOLDER ROW
        # -----------------------------
        output_frame = tk.Frame(self)
        output_frame.pack(padx=10, pady=(0, 10))

        tk.Label(output_frame, text="Output Folder:").pack(side="left")

        self.output_entry = tk.Entry(output_frame)
        self.output_entry.pack(side="left", fill="x", expand=True, padx=5)

        tk.Button(
            output_frame,
            text="Browse...",
            command=self.browse_output
        ).pack(side="left")
        
        # -----------------------------
        # EXTRACT BUTTON
        # -----------------------------
        extract_button = tk.Button(
            self,
            text="Extract!",
            height=2,
            command=self.extract
        )

        extract_button.pack(
            padx=10,
            pady=20,
            fill="x"
        )


        self.status_label = tk.Label(
            self,
            text="Ready",
            bg="#3c3f41",
            fg="white"
        )
        self.status_label.pack(padx=10, pady=(10, 0), fill="x")

        self.progress_bar = ttk.Progressbar(
            self,
            orient="horizontal",
            mode="determinate"
        )

        self.progress_bar.pack(padx=10, pady=10, fill="x")
        self.log_box = tk.Text(self, height=10)
        self.log_box.pack(fill="both", expand=True, padx=10, pady=10)
        
        
        sys.stdout = TextRedirector(self.log_box)
        sys.stderr = TextRedirector(self.log_box)
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
            
    def browse_output(self):

        path = filedialog.askdirectory(
            title="Select Output Folder"
        )

        if path:
            self.output_entry.delete(0, tk.END)
            self.output_entry.insert(0, path)
            
    def extract(self):
        tri_path = self.tri_entry.get()
        nif_path = self.nif_entry.get()
        output_path = self.output_entry.get()
        self.progress_bar["value"] = 0
        self.status_label.config(text="Starting...")

        
        print("TRI:", tri_path)
        print("NIF:", nif_path)
        print("OUTPUT:", output_path)
        extract_tri(nif_path, tri_path, output_path, progress_callback=self.update_progress)
        self.status_label.config(text="Done.")

    def update_progress(self, completed, total, shape_name, morph_name):
        self.progress_bar["maximum"] = total
        self.progress_bar["value"] = completed

        self.status_label.config(
            text=f"Exporting {shape_name} / {morph_name} ({completed}/{total})"
        )

        self.progress_bar.update_idletasks()

def log(self, message):
    self.log_box.insert(tk.END, message + "\n")
    self.log_box.see(tk.END)
    
    
if __name__ == "__main__":
    app = MainWindow()
    app.mainloop()