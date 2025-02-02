package bp.ui.container;

import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import bp.ui.BPComponent;

public interface BPContainer<C extends Container> extends BPComponent<C>
{
	default boolean isContainer()
	{
		return true;
	}

	Map<String, BPComponent<?>> getComponentMap();

	@SuppressWarnings("unchecked")
	default <T extends Component> BPComponent<T> getComponentByID(String id)
	{
		return (BPComponent<T>) getComponentMap().get(id);
	}

	Container getRealContainer();

	default void remove(String id)
	{
		if (id == null)
			return;
		Map<String, BPComponent<?>> cm = getComponentMap();
		BPComponent<?> subcomp = cm.get(id);
		if (subcomp.tryClose())
		{
			subcomp.close();
			subcomp.clearResource();
			cm.remove(id);
			removeRealComponent(subcomp);
		}
	}

	default void removeRealComponent(BPComponent<?> subcomp)
	{
		getRealContainer().remove(subcomp.getComponent());
	}

	default void clearResource()
	{
		Map<String, BPComponent<?>> cm = getComponentMap();
		if (cm != null)
		{
			Collection<BPComponent<?>> subcomps = cm.values();
			for (BPComponent<?> subcomp : subcomps)
			{
				if (subcomp.isContainer())
				{
					((BPContainer<?>) subcomp).clearResource();
				}
			}
		}
		clearSubComponents();
		clearRealComponents();
	}

	default void clearRealComponents()
	{
		getRealContainer().removeAll();
	}

	default void clearSubComponents()
	{
		Map<String, BPComponent<?>> cm = getComponentMap();
		if (cm != null)
			cm.clear();
	}

	default void eachContainer(Consumer<BPContainer<?>> cb)
	{
		Map<String, BPComponent<?>> cm = getComponentMap();
		Collection<BPComponent<?>> subcomps = cm.values();
		for (BPComponent<?> subcomp : subcomps)
		{
			if (subcomp.isContainer())
			{
				cb.accept((BPContainer<?>) subcomp);
			}
		}
	}

	default void eachComponent(Consumer<BPComponent<?>> cb)
	{
		Map<String, BPComponent<?>> cm = getComponentMap();
		Collection<BPComponent<?>> subcomps = cm.values();
		for (BPComponent<?> subcomp : subcomps)
		{
			cb.accept(subcomp);
		}
	}
}
