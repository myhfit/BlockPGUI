package bp.ui.frame;

import java.util.Map;

import bp.event.BPEventUI;
import bp.format.BPFormat;
import bp.res.BPResource;
import bp.ui.container.BPTabbedContainer;
import bp.ui.editor.BPEditorFactory;

public interface BPMainFrameIFC
{
	public BPTabbedContainer<?> getBottomTab();

	public BPTabbedContainer<?> getEditors();

	public void refreshShortCuts();

	public void toggleLeftPanel();

	public void toggleBottomPanel();

	public void toggleRightPanel();

	public void enterStandaloneMode();

	public void createEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params);

	public void openEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params);

	public void openResource(BPResource res, BPFormat format, BPEditorFactory fac, boolean isselected, String rconid);

	public static class BPEventUIMainFrame extends BPEventUI
	{
		public final static String EVENTKEY_MAINFRAME = "E_UI_MAINFRAME";

		public final static String MAINFRAME_ENTERSTANDALONEMODE = "ENTER_SA_MODE";

		public BPEventUIMainFrame(String subkey, Object[] datas)
		{
			this.key = EVENTKEY_MAINFRAME;
			this.subkey = subkey;
			this.datas = datas;
		}
	}
}
