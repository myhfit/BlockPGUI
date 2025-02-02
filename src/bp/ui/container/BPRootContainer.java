package bp.ui.container;

import java.awt.Container;

public interface BPRootContainer<C extends Container> extends BPContainer<C>
{
	default boolean isRootContainer()
	{
		return true;
	}
}
