package bp.ext;

import bp.BPCore;
import bp.BPCore.BPPlatform;
import bp.ui.frame.BPMainFrameIFC;

public interface BPExtensionLoaderGUISwing extends BPExtensionLoaderGUI<BPMainFrameIFC>
{
	public final static String UITYPE_SWING = "Swing";

	default String getUIType()
	{
		return UITYPE_SWING;
	}

	default boolean checkPlatform()
	{
		return BPCore.getPlatform() == BPPlatform.GUI_SWING;
	}
}
