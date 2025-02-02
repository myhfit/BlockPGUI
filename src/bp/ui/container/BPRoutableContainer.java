package bp.ui.container;

import java.awt.Container;

import bp.ui.BPComponent;

public interface BPRoutableContainer<C extends Container> extends BPContainer<C>
{
	void closeTo(String id);

	void addRoute(String id, String title, BPComponent<?> comp);

	void routeTo(String id, boolean needdel);

	void closeCurrent();

	default boolean isRoutable()
	{
		return true;
	}

	BPComponent<?> getCurrent();

	String getID();
}
