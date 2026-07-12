package bp.ui.frame;

import java.util.function.Function;

import javax.swing.Action;

import bp.event.BPEventUI;
import bp.ui.container.BPTabbedContainer;
import bp.ui.editor.BPEditor;

public interface BPMainFrameIFC extends BPFrameHostIFC
{
	public BPTabbedContainer<?> getBottomTab();

	public BPTabbedContainer<?> getEditors();

	public void refreshShortCuts();

	public void toggleLeftPanel();

	public void toggleBottomPanel();

	public void toggleVisible();

	public void enterStandaloneMode();

	public boolean isVisible();

	public <T> T useCurrentEditor(Function<? extends BPEditor<?>, T> seg);

	default void registerMenu(String key, String title, Action[] actions)
	{
	}

	default boolean isMainFrame()
	{
		return true;
	}

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
