from tkinter import *
from tkinter.ttk import *

tk = Tk()
tk.title("Knotbook Installer")
tk.geometry("500x300")

progress = Progressbar(tk, orient=HORIZONTAL, length=100, mode='determinate')


def bar():
    import time
    for i in range(0, 102, 2):
        progress["value"] = i
        tk.update()
        time.sleep(0.1)


progress.pack(fill="x", expand=True)
Button(tk, text='Download Stuff', command=bar).pack()
mainloop()
