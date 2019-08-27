Set oShell = CreateObject ("Wscript.Shell")
Dim strArgs
strArgs = "cmd /c knotbook.bat"
oShell.Run strArgs, 0, false