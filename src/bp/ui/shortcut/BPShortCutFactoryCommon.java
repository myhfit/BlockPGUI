package bp.ui.shortcut;

import java.util.function.BiConsumer;

public class BPShortCutFactoryCommon implements BPShortCutFactory
{
	public void register(BiConsumer<String, BPShortCutFactory> regfunc)
	{
		regfunc.accept(BPShortCutEditor.SCKEY_EDITOR, this);
		regfunc.accept(BPShortCutEditorNewWindow.SCKEY_EDITORNW, this);
		regfunc.accept(BPShortCutConsole.SCKEY_CONSOLE, this);
		regfunc.accept(BPShortCutSimpleRun.SCKEY_SIMPLERUN, this);
		regfunc.accept(BPShortCutResource.SCKEY_RES, this);
	}

	public BPShortCut createShortCut(String key)
	{
		switch (key)
		{
			case BPShortCutEditor.SCKEY_EDITOR:
				return new BPShortCutEditor();
			case BPShortCutEditorNewWindow.SCKEY_EDITORNW:
				return new BPShortCutEditorNewWindow();
			case BPShortCutConsole.SCKEY_CONSOLE:
				return new BPShortCutConsole();
			case BPShortCutSimpleRun.SCKEY_SIMPLERUN:
				return new BPShortCutSimpleRun();
			case BPShortCutResource.SCKEY_RES:
				return new BPShortCutResource();
		}
		return null;
	}

	public boolean canExpand(String key)
	{
		if (BPShortCutResource.SCKEY_RES.equals(key))
			return true;
		return false;
	}
}
