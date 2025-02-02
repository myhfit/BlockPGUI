package bp.ui.scomp;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;

public class BPRouteBar extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8270418754456278825L;

	protected List<String> m_ids = new ArrayList<String>();
	protected List<BPLabel> m_items = new ArrayList<BPLabel>();
	protected List<BPLabel> m_sps = new ArrayList<BPLabel>();

	protected WeakRefGo<BiConsumer<String, Boolean>> m_switchcbref;

	public BPRouteBar()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		m_switchcbref = new WeakRefGo<BiConsumer<String, Boolean>>();
	}

	public void setSwitchCallback(BiConsumer<String, Boolean> switchcb)
	{
		m_switchcbref.setTarget(switchcb);
	}

	public void addItem(String id, String title)
	{
		m_ids.add(id);
		BPLabel lbl = new BPLabel(title);
		lbl.setMonoFont();
		lbl.setBorder(new EmptyBorder(3, 3, 3, 3));
		lbl.setFloatLabel();
		lbl.addMouseListener(new UIUtil.BPMouseListener(null, this::onItemDown, null, null, null));
		if (m_items.size() > 0)
		{
			BPLabel lblsp = new BPLabel(">");
			lblsp.setMonoFont();
			m_sps.add(lblsp);
			add(lblsp);
		}
		m_items.add(lbl);
		add(lbl);
	}

	public void removeItem(String id)
	{
		int index = m_ids.indexOf(id);
		if (index > -1)
		{
			m_ids.remove(index);
			BPLabel lbl = m_items.remove(index);
			remove(lbl);
			if (index > 0)
				remove(m_sps.remove(index - 1));
		}
	}

	protected void onItemDown(MouseEvent e)
	{
		BPLabel l = (BPLabel) e.getSource();
		int i = m_items.indexOf(l);
		if (i > -1)
		{
			m_switchcbref.run(t -> t.accept(m_ids.get(i), false));
		}
	}
}
