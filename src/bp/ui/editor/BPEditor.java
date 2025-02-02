package bp.ui.editor;

import java.awt.Component;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;

import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.res.BPResource;
import bp.ui.BPComponent;

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

	default void toggleRightPanel()
	{
	}

	default String getEditorName()
	{
		return null;
	}
}
