package bp.ui.container;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import bp.ui.BPComponent;
import bp.ui.scomp.BPTabbedPanel;

public class BPTabbedContainerBase extends BPTabbedPanel implements BPTabbedContainer<BPTabbedPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8788598230893791841L;

	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();

	public void addBPTab(String id, Icon icon, String title, BPComponent<?> c)
	{
		super.addTab(id, title, icon, c.getComponent());
		m_compmap.put(id, c);
		switchTab(id);
	}

	public void addBPTab(String id, Component tabcomp, String title, BPComponent<?> c)
	{
		addBPTab(id, tabcomp, title, c, true);
	}

	public void addBPTab(String id, Component tabcomp, String title, BPComponent<?> c, boolean needswitch)
	{
		addTab(id, title, tabcomp, c.getComponent());
		m_compmap.put(id, c);
		if (needswitch)
		{
			switchTab(id);
		}
	}

	public void closeBPTab(String id)
	{
		removeTab(id);
	}

	public Map<String, BPComponent<?>> getComponentMap()
	{
		return m_compmap;
	}

	public Container getRealContainer()
	{
		return this;
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.TABS;
	}

	public BPTabbedPanel getComponent()
	{
		return this;
	}

	public boolean checkRemoveTab(String id)
	{
		BPComponent<?> comp = m_compmap.get(id);
		if (comp != null)
		{
			if (!comp.tryClose())
			{
				return false;
			}
		}
		return super.checkRemoveTab(id);
	}

	public void removeTab(String id)
	{
		remove(id);
	}

	public void removeRealComponent(BPComponent<?> subcomp)
	{
		doRemoveTab(findID(subcomp.getComponent()));
	}

	public void closeAllTabs()
	{
		List<String> ids = new ArrayList<String>(m_compmap.keySet());
		for (String id : ids)
		{
			closeBPTab(id);
		}
		clearResource();
	}

	public void closeOther(String oid)
	{
		List<String> ids = new ArrayList<String>(m_compmap.keySet());
		for (String id : ids)
		{
			if (!id.equals(oid))
				closeBPTab(id);
		}
	}
}
