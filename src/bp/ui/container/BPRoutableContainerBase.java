package bp.ui.container;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPRouteBar;
import bp.ui.scomp.BPToolVIconButton;

public class BPRoutableContainerBase extends JPanel implements BPRoutableContainer<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3562837066236276967L;

	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();
	protected BPRouteBar m_routebar;

	protected List<String> m_ids;
	protected int m_selindex = -1;
	protected JPanel m_con;
	protected volatile String m_id;

	protected BiConsumer<String, Boolean> m_switchcb;

	public BPRoutableContainerBase()
	{
		m_switchcb = this::routeTo;
		m_ids = new LinkedList<String>();

		setLayout(new BorderLayout());

		m_routebar = new BPRouteBar();
		m_con = new JPanel();

		JPanel toppan = new JPanel();
		toppan.setLayout(new BorderLayout());

		JPanel tb = new JPanel();
		initToolBar(tb);

		toppan.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));

		toppan.add(tb, BorderLayout.WEST);
		toppan.add(m_routebar, BorderLayout.CENTER);
		add(toppan, BorderLayout.NORTH);
		add(m_con, BorderLayout.CENTER);

		m_con.setLayout(new BorderLayout());

		m_routebar.setSwitchCallback(m_switchcb);
	}

	protected void initToolBar(JPanel tb)
	{
		tb.setLayout(new BoxLayout(tb, BoxLayout.X_AXIS));
		tb.setBorder(new EmptyBorder(0, 2, 0, 4));
		tb.add(new BPToolVIconButton(BPAction.build("back").callback(this::onBack).vIcon(BPIconResV.TOUP()).getAction()));
	}

	protected void onBack(ActionEvent e)
	{
		int idx = m_selindex - 1;
		if (idx < 0)
			idx = 0;
		routeTo(m_ids.get(idx), true);
	}

	public Map<String, BPComponent<?>> getComponentMap()
	{
		return m_compmap;
	}

	public Container getRealContainer()
	{
		return m_con;
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.PANEL;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void closeTo(String id)
	{
	}

	public void addRoute(String id, String title, BPComponent<?> comp)
	{
		Map<String, BPComponent<?>> compmap = m_compmap;
		List<String> ids = m_ids;
		int index = m_selindex;
		if (index < ids.size() - 1)
		{
			for (int i = ids.size() - 1; i > index; i--)
			{
				String delid = ids.remove(i);
				BPComponent<?> oldcomp = compmap.get(delid);
				if (oldcomp != null)
					oldcomp.clearResource();
				compmap.remove(delid);
				m_routebar.removeItem(delid);
			}
		}

		if (compmap.containsKey(id))
		{
		}
		else
		{
			compmap.put(id, comp);
			m_routebar.addItem(id, title);
			ids.add(id);
			routeTo(id, false);
		}
	}

	public void routeTo(String id, boolean needdel)
	{
		Map<String, BPComponent<?>> compmap = m_compmap;
		int index = m_ids.indexOf(id);
		if (index > -1 && index != m_selindex)
		{
			m_selindex = index;
			m_con.removeAll();
			m_con.add(compmap.get(id).getComponent(), BorderLayout.CENTER);
			routeSwitched(id);
			validate();
			repaint();

			if (needdel && index < m_ids.size() - 1)
			{
				for (int i = m_ids.size() - 1; i > index; i--)
				{
					String delid = m_ids.remove(i);
					BPComponent<?> comp = compmap.get(delid);
					if (comp != null)
						comp.clearResource();
					compmap.remove(delid);
					m_routebar.removeItem(delid);
				}
			}
		}
	}

	protected void routeSwitched(String id)
	{

	}

	public void closeCurrent()
	{
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public BPComponent<?> getCurrent()
	{
		return m_compmap.get(m_ids.get(m_selindex));
	}

	public String getID()
	{
		return m_id;
	}
}
