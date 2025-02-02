package bp.ui;

import java.awt.Component;

public interface BPComponent<C extends Component>
{
	BPComponentType getComponentType();

	C getComponent();

	default boolean isContainer()
	{
		return false;
	}

	default boolean isRootContainer()
	{
		return false;
	}

	public enum BPComponentType
	{
		FRAME, DIALOG, SELECT, TEXTEDITOR, CODEEDITOR, TREE, TOOLBAR, TABS, FORM, CUSTOMCOMP, IMAGEEDITOR, TABLE, PANEL
	}

	default boolean tryClose()
	{
		return true;
	}

	default void close()
	{

	}

	default void clearResource()
	{

	}
	
	default boolean isRoutable()
	{
		return false;
	}
}
