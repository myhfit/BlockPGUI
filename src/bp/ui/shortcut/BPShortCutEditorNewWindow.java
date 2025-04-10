package bp.ui.shortcut;

import bp.ui.util.CommonUIOperations;
import bp.util.TextUtil;

public class BPShortCutEditorNewWindow extends BPShortCutEditor
{
	public final static String SCKEY_EDITORNW = "Editor(window)";

	public boolean run()
	{
		String editorname = TextUtil.eds(getParam(SC_KEY_EDITORNAME));
		String filename = TextUtil.eds(getParam(SC_KEY_FILENAME));
		String format = TextUtil.eds(getParam(SC_KEY_FORMAT));

		if (editorname != null)
		{
			CommonUIOperations.openFileNewWindow(filename, format, editorname, null);
			return true;
		}
		return false;
	}

	public String getShortCutKey()
	{
		return SCKEY_EDITORNW;
	}
}
