package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPSwitchPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 276835303494086833L;

	protected List<JComponent> m_subs;
	protected List<Object> m_labels;
	protected Color m_bg;

	protected int m_si;

	public BPSwitchPanel()
	{
		m_subs = new ArrayList<JComponent>();
		m_labels = new ArrayList<Object>();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setFocusable(false);
	}

	public void setup(Object[] labels, Function<Object, JComponent> faccb, BiConsumer<Integer, JComponent> initcb)
	{
		m_subs.clear();
		m_labels.clear();
		removeAll();

		for (int i = 0; i < labels.length; i++)
		{
			Object label = labels[i];
			m_labels.add(label);
			JComponent comp;
			if (faccb != null)
			{
				comp = faccb.apply(label);
				if (initcb != null)
					initcb.accept(i, comp);
			}
			else
				comp = makeLabel(i, label, initcb);
			m_subs.add(comp);
			add(comp);
			if (i != labels.length - 1)
				add(Box.createHorizontalStrut(4));
		}
		m_bg = getBackground();
	}

	public void setSelectedIndex(int si)
	{
		m_si = si;
		for (int i = 0; i < m_subs.size(); i++)
		{
			JComponent comp = m_subs.get(i);
			comp.setBorder(i == si ? new MatteBorder(0, 1, 0, 1, UIConfigs.COLOR_TEXTHALF()) : new EmptyBorder(0, 1, 0, 1));
			comp.setBackground(i == si ? UIConfigs.COLOR_WEAKBORDER() : m_bg);
		}
		updateUI();
	}

	protected JComponent makeLabel(int index, Object label, BiConsumer<Integer, JComponent> initcb)
	{
		BPLabel lbl = new BPLabel(ObjUtil.toString(label));
		if (initcb != null)
			initcb.accept(index, lbl);
		lbl.setLabelFont();
		lbl.setBorder(new EmptyBorder(0, 2, 0, 2));
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(null);
		p.add(lbl, BorderLayout.CENTER);
		p.addMouseListener(new UIUtil.BPMouseListener(null, this::onLabelMouseDown, null, null, null));
		return p;
	}

	protected void onLabelMouseDown(MouseEvent e)
	{
		int si = m_subs.indexOf(e.getSource());
		setSelectedIndex(si);
	}
}
