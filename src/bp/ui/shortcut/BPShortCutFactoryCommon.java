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
	}

	public BPShortCut createShortCut(String key)
	{
		BPShortCut rc = null;
		switch (key)
		{
			case BPShortCutEditor.SCKEY_EDITOR:
			{
				rc = new BPShortCutEditor();
				break;
			}
			case BPShortCutEditorNewWindow.SCKEY_EDITORNW:
			{
				rc = new BPShortCutEditorNewWindow();
				break;
			}
			case BPShortCutConsole.SCKEY_CONSOLE:
			{
				rc = new BPShortCutConsole();
				break;
			}
			case BPShortCutSimpleRun.SCKEY_SIMPLERUN:
			{
				rc = new BPShortCutSimpleRun();
				break;
			}
		}
		return rc;
	}
}
