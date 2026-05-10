package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.util.CommonDataUIProcs;

public class BPCommonDataChainPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2523363948106677063L;

	protected BPCommonDataPanel m_root;
	protected List<BPCommonDataPanel> m_subs;
	protected BiConsumer<Object, BPCommonDataPanel> m_selcb;
	protected JPanel m_cp;
	protected JScrollPane m_scroll;

	public BPCommonDataChainPanel()
	{
		m_root = new BPCommonDataPanel();
		m_subs = new ArrayList<BPCommonDataPanel>();
		m_selcb = this::onSelect;

		m_cp = new JPanel();
		m_scroll = new JScrollPane();
		m_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_scroll.setViewportView(m_cp);

		m_root.setActions(null, null);
		preparePanel(m_root);
		setLayout(new BorderLayout());
		m_cp.setLayout(new BoxLayout(m_cp, BoxLayout.X_AXIS));
		m_cp.add(m_root);
		add(m_scroll, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(600, 400));
	}

	protected void preparePanel(BPCommonDataPanel p)
	{
		p.setMinimumSize(new Dimension(150, 400));
		p.setPreferredSize(new Dimension(300, 400));
		p.setActions(m_selcb, null);
	}

	protected BPCommonDataPanel getLastDataPanel()
	{
		return m_subs.size() > 0 ? m_subs.get(m_subs.size() - 1) : m_root;
	}

	public void setMode(int mode)
	{
		m_root.setMode(mode);
	}

	public void setData(Object data)
	{
		m_root.setData(data);
	}

	public void initByData()
	{
		m_root.initByData();
	}

	public void onSelect(Object data, BPCommonDataPanel panel)
	{
		{
			List<BPCommonDataPanel> delps = new ArrayList<BPCommonDataPanel>();
			if (m_root == panel)
			{
				delps.addAll(m_subs);
			}
			else if (m_subs.size() > 0)
			{
				int vi = m_subs.indexOf(panel);
				if (vi < m_subs.size() - 1)
				{
					delps.addAll(m_subs.subList(vi + 1, m_subs.size()));
				}
			}
			if (delps.size() > 0)
			{
				for (BPCommonDataPanel p : delps)
				{
					p.clearResource();
					m_cp.remove(p);
				}
				m_subs.removeAll(delps);
			}
			delps.clear();
		}

		BPCommonDataPanel p = new BPCommonDataPanel();
		p.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		preparePanel(p);
		m_cp.add(p);
		p.setData(data);
		p.setMode(CommonDataUIProcs.testDataMode(data));
		p.initByData();
		m_subs.add(p);
		updateUI();
	}
}
