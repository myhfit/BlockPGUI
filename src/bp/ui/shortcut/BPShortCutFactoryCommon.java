package bp.ui.shortcut;

import java.util.function.BiConsumer;

public class BPShortCutFactoryCommon implements BPShortCutFactory
{
	public void register(BiConsumer<String, BPShortCutFactory> regfunc)
	{
		regfunc.accept("Editor", this);
		regfunc.accept("Console", this);
	}

	public BPShortCut createShortCut(String key)
	{
		BPShortCut rc = null;
		switch (key)
		{
			case "Editor":
			{
				rc = new BPShortCutEditor();
				break;
			}
			case "Console":
			{
				rc = new BPShortCutConsole();
				break;
			}
		}
		return rc;
	}
}
