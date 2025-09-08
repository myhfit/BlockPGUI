package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;

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
		installfunc.accept("Common", new BPToolGUIDataPipe());
		installfunc.accept("Common", new BPToolGUIParallelEditor());
	}
}