package bp.ui.container;

import java.awt.Component;
import java.awt.Container;

import javax.swing.Icon;

import bp.ui.BPComponent;

public interface BPTabbedContainer<C extends Container> extends BPContainer<C>
{
	void addBPTab(String id, Icon icon, String title, BPComponent<?> c);

	void addBPTab(String id, Component tabcomp, String title, BPComponent<?> c);

	void addBPTab(String id, Component tabcomp, String title, BPComponent<?> c, boolean needswitch);

	void closeBPTab(String id);

	void switchTab(String id);

	default void closeAllTabs()
	{
		clearResource();
	}
}
