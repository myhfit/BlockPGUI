package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.LogicUtil.WeakRefGoConsumer;

public class BPTabBar extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8826011438919716510L;

	protected List<Tab> m_tabs = new ArrayList<Tab>();

	protected int m_selindex = -1;

	protected Map<String, Tab> m_tabmap = new HashMap<String, Tab>();

	protected Consumer<String> m_switchfunc;
	protected Consumer<String> m_closefunc;

	protected int m_barwidth = 150;
	protected int m_bargap = 5;

	protected int m_barheight = UIConfigs.BAR_HEIGHT_VICON();

	protected JPanel m_tabpnl;
	protected BPToolVIconButton m_morebtn;
	protected WeakReference<Component> m_leftcompref;
	protected WeakReference<Component> m_rightcompref;

	protected boolean m_noclose;
	protected int m_tabborderpos = 0;
	protected boolean m_labelcenter;

	public BPTabBar()
	{
		m_labelcenter = true;
		m_tabpnl = new JPanel();
		BoxLayout layout = new BoxLayout(m_tabpnl, BoxLayout.X_AXIS);
		m_tabpnl.setLayout(layout);
		setLayout(new BorderLayout());
		add(m_tabpnl, BorderLayout.CENTER);
		setBarHeight(m_barheight);
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	public void initMoreButton(Consumer<ActionEvent> callback)
	{
		WeakRefGoConsumer<ActionEvent> cbref = new WeakRefGoConsumer<ActionEvent>(callback);
		m_morebtn = new BPToolVIconButton(BPIconResV.DROPDOWN(), cbref::accept);
		m_morebtn.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		setRightComponent(m_morebtn);
	}

	public void setBarHeight(int barheight)
	{
		m_barheight = barheight;
		m_tabpnl.setPreferredSize(new Dimension(500, (int) Math.round(barheight * UIConfigs.UI_SCALE()) - 1));
		m_tabpnl.setMinimumSize(new Dimension(0, (int) Math.round(barheight * UIConfigs.UI_SCALE()) - 1));
	}

	public void setBarWidth(int barwidth)
	{
		m_barwidth = barwidth;
	}

	public void setNoClose(boolean flag)
	{
		m_noclose = flag;
	}

	public void setTabBorderPos(int flag)
	{
		m_tabborderpos = flag;
	}

	public void setLabelCenter(boolean flag)
	{
		m_labelcenter = flag;
	}

	public void setRightComponent(Component c)
	{
		WeakReference<Component> rightcompref = m_rightcompref;
		if (rightcompref != null)
		{
			Component rightcomp = rightcompref.get();
			if (rightcomp != null)
				remove(rightcomp);
		}
		if (c != null)
			add(c, BorderLayout.EAST);
		m_rightcompref = new WeakReference<Component>(c);
	}

	public void setLeftComponent(Component c)
	{
		WeakReference<Component> leftcompref = m_leftcompref;
		if (leftcompref != null)
		{
			Component leftcomp = leftcompref.get();
			if (leftcomp != null)
				remove(leftcomp);
		}
		if (c != null)
			add(c, BorderLayout.WEST);
		m_leftcompref = new WeakReference<Component>(c);
	}

	public void makeSelectedTabVisible()
	{
		if (m_selindex > -1)
		{
			int w = getWidth();
			if (w == 0)
				return;
			int newidx = (w + m_bargap) / m_barwidth - 1;
			if (newidx < 0)
				newidx = 0;
			if (newidx < m_selindex)
			{
				moveTab(m_selindex, newidx);
				m_selindex = newidx;
			}
		}
	}

	protected void moveTab(int oldidx, int newidx)
	{
		if (oldidx == newidx)
			return;
		Tab tab = m_tabs.get(oldidx);
		if (newidx < oldidx)
		{
			m_tabpnl.remove(oldidx);
			m_tabs.remove(oldidx);
			m_tabpnl.add(tab.pan, newidx);
			m_tabs.add(newidx, tab);
		}
		else
		{
			m_tabpnl.add(tab.pan, newidx);
			m_tabs.add(newidx, tab);
			m_tabpnl.remove(oldidx);
			m_tabs.remove(oldidx);
		}
	}

	public List<Tab> getTabs()
	{
		return new ArrayList<Tab>(m_tabs);
	}

	public void setup(Consumer<String> switchfunc, Consumer<String> closefunc)
	{
		m_switchfunc = switchfunc;
		m_closefunc = closefunc;
	}

	public Tab addTab(String id, Icon icon, String title)
	{
		return addTab(id, icon, title, null);
	}

	public Tab addTab(String id, Component tabcomp, String title)
	{
		return addTab(id, null, title, tabcomp);
	}

	public Tab addTab(String id, Icon icon, String title, Component tabcomp)
	{
		Tab newtab = new Tab(id, icon, title, tabcomp);
		m_tabs.add(newtab);
		m_tabmap.put(id, newtab);
		m_tabpnl.add(newtab.pan);
		makeSelectedTabVisible();
		refreshTabs();
		return newtab;
	}

	public void removeTab(String id)
	{
		if (id == null)
			return;
		Tab tab = m_tabmap.remove(id);
		m_tabs.remove(tab);
		if (m_selindex >= m_tabs.size())
			m_selindex = m_tabs.size() - 1;
		m_tabpnl.remove(tab.pan);
		refreshTabs();
		validate();
		repaint();
	}

	public void switchTab(String id)
	{
		int i = findTabIndex(id);
		if (i != -1)
		{
			if (m_selindex != i)
			{
				m_selindex = i;
				makeSelectedTabVisible();
				refreshTabs();
			}
		}
	}

	public int findTabIndex(String id)
	{
		for (int i = 0; i < m_tabs.size(); i++)
		{
			if (id.equals(m_tabs.get(i).id))
			{
				return i;
			}
		}
		return -1;
	}

	protected void refreshTabs()
	{
		for (int i = 0; i < m_tabs.size(); i++)
		{
			Tab tab = m_tabs.get(i);
			boolean flag = (m_selindex == i);
			if (tab.selected != flag)
			{
				tab.selected = flag;
				tab.pan.setSelectedBorder(flag);
			}
		}
	}

	protected void callCloseTab(String id)
	{
		if (m_closefunc != null)
			m_closefunc.accept(id);
	}

	public void callSwitchTab(String id)
	{
		if (m_switchfunc != null)
			m_switchfunc.accept(id);
	}

	public String getSelectedID()
	{
		String rc = null;
		if (m_selindex > -1)
		{
			rc = m_tabs.get(m_selindex).id;
		}
		return rc;
	}

	public Tab getSelectedTab()
	{
		Tab rc = null;
		if (m_selindex > -1)
		{
			rc = m_tabs.get(m_selindex);
		}
		return rc;
	}

	public void refreshSelectedID()
	{

	}

	public String switchSelectedIndex(int delta)
	{
		m_selindex += delta;
		if (delta < 0 && m_selindex < 0)
			m_selindex = m_tabs.size();
		if (delta > 0 && m_selindex >= m_tabs.size())
			m_selindex = 0;
		if (m_selindex < 0)
			m_selindex = 0;
		if (m_selindex >= m_tabs.size())
			m_selindex = m_tabs.size() - 1;
		refreshTabs();
		if (m_selindex == -1)
			return null;
		return m_selindex >= m_tabs.size() ? null : m_tabs.get(m_selindex).id;
	}

	public Tab findTab(String id)
	{
		return m_tabmap.get(id);
	}

	public String getTitle(String id)
	{
		String rc = null;
		if (id != null)
		{
			Tab tab = findTab(id);
			if (tab != null)
				rc = tab.title;
		}
		return rc;
	}

	public final class Tab
	{
		public String id;
		public Icon icon;
		public String title;
		public TabComp pan;
		public boolean selected;

		public Tab(String id, Icon icon, String title, Component comp)
		{
			this.id = id;
			this.icon = icon;
			this.title = title;
			pan = new TabComp(id, icon, title, comp);
		}
	}

	public class TabComp extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4434598466239091260L;

		protected boolean m_selected;
		protected Color m_bgcolor;
		protected Color m_selcolor;
		protected String m_id;
		protected boolean m_complbl;
		protected JComponent m_comp;

		protected WeakRefGo<BiConsumer<String, String>> m_mnuref = new WeakRefGo<BiConsumer<String, String>>();
		protected Object[][] m_mnus;

		protected BPToolSQButton lbltitle;

		public TabComp(String id, Icon icon, String title, Component comp)
		{
			m_id = id;
			m_complbl = (comp != null);
			m_comp = (JComponent) comp;
			m_selected = true;
			setSelectedBorder(false);
			m_bgcolor = getBackground();
			m_selcolor = UIConfigs.COLOR_TEXTBG();

			lbltitle = new BPToolSQButton(title, () ->
			{
				callSwitchTab(m_id);
			});
			lbltitle.addMouseListener(new UIUtil.BPMouseListener(null, this::onMouseDown, null, null, null));
			lbltitle.setToolTipText(title);
			BPToolVIconButton btnx = new BPToolVIconButton(BPIconResV.CLOSE(), (e) ->
			{
				callCloseTab(m_id);
			});
			lbltitle.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.LISTFONT_SIZE()));
			if (!m_labelcenter)
				lbltitle.setHorizontalAlignment(SwingConstants.LEFT);
			lbltitle.setVerticalAlignment(SwingConstants.CENTER);
			setLayout(new BorderLayout());
			if (comp == null)
				add(lbltitle, BorderLayout.CENTER);
			else
				add(comp, BorderLayout.CENTER);
			int btnh = (int) ((float) m_barheight * 0.75f * UIConfigs.UI_SCALE());
			btnx.setPreferredSize(new Dimension(btnh, btnh));
			btnx.setMinimumSize(new Dimension(btnh, btnh));
			btnx.setMaximumSize(new Dimension(btnh, btnh));
			if (!m_noclose)
				add(btnx, BorderLayout.EAST);

			Dimension d = new Dimension((int) Math.round((double) m_barwidth * UIConfigs.GC_SCALE() * UIConfigs.UI_SCALE()), (int) Math.round((double) Math.floor(m_barheight * UIConfigs.GC_SCALE() * UIConfigs.UI_SCALE())));
			setPreferredSize(d);
			setMaximumSize(d);
			setMinimumSize(d);
		}

		public void setMenu(Object[][] mnus, BiConsumer<String, String> callback)
		{
			m_mnuref = new WeakRefGo<BiConsumer<String, String>>(callback);
			m_mnus = mnus;
		}

		@SuppressWarnings("unchecked")
		protected void onShowMenu(int x, int y)
		{
			Object[][] mnus = m_mnus;
			if (mnus != null)
			{
				JPopupMenu pmnu = new JPopupMenu();
				for (Object[] mnudata : mnus)
				{
					String name = (String) mnudata[0];
					String key = (String) mnudata[1];
					if (mnudata.length > 2)
					{
						Predicate<String> checker = (Predicate<String>) mnudata[2];
						if (checker != null)
							if (!checker.test(m_id))
								continue;
					}
					Action act;
					if (name.equals("-"))
					{
						pmnu.add(new JPopupMenu.Separator());
					}
					else
					{
						act = BPAction.build(name).callback(e -> onMenuAction(key)).getAction();
						BPMenuItem item = new BPMenuItem(act);
						pmnu.add(item);
					}
				}
				pmnu.show(this, x, y);
			}
		}

		protected void onMouseDown(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				onShowMenu(e.getX(), e.getY());
			}
		}

		protected void onMenuAction(String key)
		{
			m_mnuref.exec(cb ->
			{
				cb.accept(m_id, key);
				return null;
			});
		}

		public void setSelectedBorder(boolean flag)
		{
			if (m_selected == flag)
				return;
			m_selected = flag;
			JComponent s = this;
			if (m_complbl)
			{
				s = m_comp;
			}
			if (flag)
			{
				s.setBackground(m_selcolor);
				s.setBorder(new CompoundBorder(new MatteBorder(m_tabborderpos == 0 ? 1 : 0, 0, m_tabborderpos == 1 ? 1 : 0, 1, Color.GRAY), new EmptyBorder(0, m_complbl ? 0 : 4, 0, m_complbl ? 0 : 2)));
			}
			else
			{
				s.setBackground(m_bgcolor);
				s.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, Color.GRAY), new EmptyBorder(0, m_complbl ? 0 : 4, 0, m_complbl ? 0 : 2)));
			}
		}

		public void setTitle(String title)
		{
			lbltitle.setText(title);
		}

		public void setID(String id)
		{
			m_id = id;
		}
	}

	public void setTitle(String id, String title)
	{
		Tab tab = m_tabmap.get(id);
		if (tab != null)
		{
			tab.pan.setTitle(title);
		}
	}

	public void updateTitle(String oldid, String newid, String title)
	{
		Tab tab = m_tabmap.get(oldid);
		if (tab != null)
		{
			tab.pan.setTitle(title);
			tab.pan.setID(newid);
			m_tabmap.remove(oldid);
			m_tabmap.put(newid, tab);
		}
	}
}
