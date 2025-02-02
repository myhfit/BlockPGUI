package bp.ext;

import bp.ui.frame.BPMainFrameIFC;

public interface BPExtensionLoaderGUISwing extends BPExtensionLoaderGUI<BPMainFrameIFC>
{
	public final static String UITYPE_SWING = "Swing";

	default String getUIType()
	{
		return UITYPE_SWING;
	}
}
