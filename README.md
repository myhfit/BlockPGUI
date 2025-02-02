# Introduce
BlockPGUI is GUI main project for BlockP Projects. 

# Main class
## bp.BPGUILauncher
GUI Launcher has a path selector and extension selector.   
Extensions will be load under a independent ClassLoader(All extensions in this classloader).  
Dependency jars in extension's MANIFEST.MF will be load in same ClassLoader.  
If You want to load other jars, you can add classpath for other jars in start arguments, these jars will be load in AppClassLoader.
Start Settings will be saved in .bpenvcfgs in work dir.  
You can hide Launcher UI with set show_launcher=>false in .bpenvcfgs

## bp.BPGUIMain
Need to set full classpath and workspace path.  
Extensions will **not** be load under independent ClassLoader.  
Start Command:  
> java -cp bp.jar;bpgui.jar[...other class paths] bp.BPGUIMain [workspace path] [args...]
