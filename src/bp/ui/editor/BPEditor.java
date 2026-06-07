package bp.ui.editor;

import java.awt.Component;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;

import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.res.BPResource;
import bp.ui.BPComponent;
import bp.ui.editor.controller.BPEditorController;
import bp.ui.editor.controller.BPEditorEventController;

public interface BPEditor<C extends Component> extends BPComponent<C>
{
	void focusEditor();

	String getEditorInfo();

	default void activeEditor()
	{

	}

	void save();

	void reloadData();

	boolean needSave();

	default String[] getExts()
	{
		return null;
	}

	void setNeedSave(boolean needsave);

	void setID(String id);

	String getID();

	void setChannelID(int channelid);

	int getChannelID();

	void setOnDynamicInfo(Consumer<String> info);

	default BPDataContainer createDataContainer(BPResource res)
	{
		BPDataContainer con = new BPDataContainerBase();
		con.bind(res);
		return con;
	}

	default void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
	}

	default boolean needActiveOnStart()
	{
		return false;
	}

	default Action[] getEditMenuActions()
	{
		return null;
	}

	default Action[] getActBarActions()
	{
		return BPEditorActionManager.getBarActions(this);
	}

	default Action[] getSeparatorActions()
	{
		return null;
	}

	default void toggleLeftPanel()
	{
		toggleLeftPanel(null);
	}

	default void toggleLeftPanel(Boolean v)
	{
	}

	default void toggleRightPanel()
	{
		toggleRightPanel(null);
	}
	
	default void toggleRightPanel(Boolean v)
	{
	}

	default void toggleBottomPanel()
	{
		toggleBottomPanel(null);
	}

	default void toggleBottomPanel(Boolean v)
	{
	}

	default String getEditorName()
	{
		return null;
	}

	default void installEditorEventHandler(Consumer<BPEditorEvent> handler)
	{
		BPEditorEventController c = getEditorEventController();
		if (c != null)
			c.installHandler(handler);
	}

	BPEditorController getEditorController();

	default BPEditorEventController getEditorEventController()
	{
		BPEditorController c = getEditorController();
		return c == null ? null : c.eventcontroller;
	}

	public static class BPEditorEvent
	{
		public final static String ACT_SELECT = "SELECT";
		public final static String ACT_OPEN = "OPEN";
		public final static String ACT_DELETE = "DELETE";
		
		public String action;
		public BPEditor<?> editor;
		public Object data;
		public Object[] params;

		public BPEditorEvent(String action, BPEditor<?> editor, Object data, Object... params)
		{
			this.action = action;
			this.editor = editor;
			this.data = data;
			this.params = params;
		}
	}
}
