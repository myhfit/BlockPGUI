package bp.ui.frame;

import java.util.List;
import java.util.Map;

import bp.format.BPFormat;
import bp.res.BPResource;
import bp.ui.BPComponent;
import bp.ui.editor.BPEditorFactory;

public interface BPFrameHostIFC
{
	default boolean isMainFrame()
	{
		return false;
	}

	void createEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params);

	void openEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params);

	void openResource(BPResource res, BPFormat format, BPEditorFactory fac, boolean isselected, String rconid);

	void toggleRightPanel();

	void fullScreen();
	
	void toFront();
	
	void dispose();

	List<BPComponent<?>> getEditorList();

	default void runFrameFunction(String f)
	{
		switch (f.toUpperCase())
		{
			case "TOGGLERIGHTPAN":
				toggleRightPanel();
				break;
			case "FULLSCREEN":
				fullScreen();
				break;
		}
	}
}
