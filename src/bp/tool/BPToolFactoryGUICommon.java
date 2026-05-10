package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;
import bp.ui.actions.BPActionConstCommon;

public class BPToolFactoryGUICommon implements BPToolFactory
{
	public String getName()
	{
		return "GUI Common";
	}

	public boolean canRunAt(BPPlatform platform)
	{
		return platform == BPPlatform.GUI_SWING;
	}

	public void install(BiConsumer<String, BPTool> installfunc, BPPlatform platform)
	{
		String packname = BPActionConstCommon.TXT_COMMON.text();
		installfunc.accept(packname, new BPToolGUIDataPipe());
		installfunc.accept(packname, new BPToolGUIParallelEditor());
		installfunc.accept(packname, new BPToolGUIUnits());
	}
}