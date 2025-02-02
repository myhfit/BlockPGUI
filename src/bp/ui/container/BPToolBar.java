package bp.ui.container;

import java.awt.Container;

import javax.swing.Action;

public interface BPToolBar<C extends Container> extends BPContainer<C>
{
	default BPComponentType getComponentType()
	{
		return BPComponentType.TOOLBAR;
	}
	
	void setActions(Action[] actions);
}
