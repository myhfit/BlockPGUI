package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.scomp.BPTabBar.Tab;

public class BPTabbedPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8248564114829829753L;
	protected BPTabBar m_tabbar;
	protected JPanel m_con;

	protected Map<String, Component> m_cps = new HashMap<String, Component>();

	protected Consumer<String> m_closefunc = (id) ->
	{
		removeTab(id);
	};
	protected Consumer<String> m_switchfunc = (id) ->
	{
		switchTab(id);
	};

	public BPTabbedPanel()
	{
		setLayout(new BorderLayout());
		m_tabbar = new BPTabBar();
		m_tabbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_tabbar.setup(m_switchfunc, m_closefunc);

		m_con = new JPanel();
		add(m_tabbar, BorderLayout.NORTH);
		add(m_con, BorderLayout.CENTER);

		m_con.setLayout(new BorderLayout());
	}

	public void addTab(String id, String str, Icon icon, Component comp)
	{
		addTab(id, str, icon, null, comp);
	}

	public void addTab(String id, String str, Component tabcomp, Component comp)
	{
		addTab(id, str, null, tabcomp, comp);
	}

	public void addTab(String id, String str, Icon icon, Component tabcomp, Component comp)
	{
		m_tabbar.setVisible(true);
		Tab tab = m_tabbar.addTab(id, icon, str, tabcomp);
		if (tab != null)
		{
			initTab(tab);
		}
		m_cps.put(id, comp);
		repaint();
	}

	public void switchTab(String id)
	{
		m_tabbar.switchTab(id);
		m_con.removeAll();
		m_con.add(m_cps.get(id), BorderLayout.CENTER);
		tabSwitched(id);
		validate();
		repaint();
	}

	public void switchTab(int delta)
	{
		String selid = m_tabbar.switchSelectedIndex(delta);
		if (selid != null)
		{
			m_con.removeAll();
			m_con.add(m_cps.get(selid), BorderLayout.CENTER);
			tabSwitched(selid);
		}
		repaint();
	}

	protected void tabSwitched(String id)
	{

	}

	public void removeTab(String id)
	{
		if (id == null)
			return;
		if (!checkRemoveTab(id))
			return;
		doRemoveTab(id);
	}

	protected void doRemoveTab(String id)
	{
		m_tabbar.removeTab(id);
		m_cps.remove(id);
		tabRemoved(id);
		String selid = m_tabbar.getSelectedID();
		if (selid != null)
		{
			switchTab(selid);
		}
		else
		{
			m_con.removeAll();
			repaint();
		}
	}

	protected void tabRemoved(String id)
	{

	}

	public String findID(Component comp)
	{
		String id = null;
		for (Entry<String, Component> entry : m_cps.entrySet())
		{
			if (entry.getValue() == comp)
			{
				id = entry.getKey();
				break;
			}
		}
		return id;
	}

	public String getTitle(String id)
	{
		return m_tabbar.getTitle(id);
	}

	protected boolean checkRemoveTab(String id)
	{
		return true;
	}

	public void removeCurrentTab()
	{
		String selid = m_tabbar.getSelectedID();
		removeTab(selid);
	}

	public BPTabBar getTabBar()
	{
		return m_tabbar;
	}

	protected void initTab(Tab tab)
	{

	}
}
