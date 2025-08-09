package bp.ui.scomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import bp.config.UIConfigs;
import bp.ui.res.icon.BPVIcon;
import bp.ui.util.UIUtil.ActionRunnable;

public class BPToolVIconButton extends JComponent implements MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8662713082517570937L;

	protected Consumer<ActionEvent> m_act;
	protected String m_actcmd;

	protected BPVIcon m_icon;

	protected Color m_bbg;

	protected boolean m_issel;

	public BPToolVIconButton(Action action)
	{
		this(action, null);
	}

	public BPToolVIconButton(Action action, JComponent accparent)
	{
		this((BPVIcon) action.getValue("VICON"), (e) ->
		{
			action.actionPerformed(e);
		});
		m_actcmd = (String) action.getValue(Action.ACTION_COMMAND_KEY);
		String tooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);
		action.addPropertyChangeListener(this::onPropChanged);
		if (tooltip != null)
			setToolTipText(tooltip);
		m_act = new ActionRunnable(action);
		KeyStroke ks = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
		if (ks != null)
		{
			if (accparent != null)
				accparent.registerKeyboardAction(action, (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			else
				registerKeyboardAction(action, (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		setButtonSize(UIConfigs.BUTTON_SIZE());
	}

	public BPToolVIconButton(BPVIcon icon, Consumer<ActionEvent> act)
	{
		super();
		m_icon = icon;
		m_act = act;
		addMouseListener(this);
		setButtonSize(UIConfigs.BUTTON_SIZE());
	}

	public void setButtonSize(int size)
	{
		int btnsize = size;
		setPreferredSize(new Dimension(btnsize, btnsize));
		setMaximumSize(new Dimension(btnsize, btnsize));
		setMinimumSize(new Dimension(btnsize, btnsize));
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		m_act.accept(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, EventQueue.getMostRecentEventTime(), 0));
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void setSelected(boolean issel)
	{
		m_issel = issel;
		repaint();
	}

	public boolean isSelected()
	{
		return m_issel;
	}

	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = ((Graphics2D) g);
		AffineTransform f = g2d.getTransform();
		float scale = 1f;
		if (f != null)
			scale = (float) f.getScaleY();
		if (scale != 1f)
		{
			AffineTransform f2 = AffineTransform.getScaleInstance(1, 1);
			f2.translate(f.getTranslateX(), f.getTranslateY());
			g2d.setTransform(f2);
			int h = (int) Math.floor((float) getHeight() * f.getScaleY());
			int w = (int) Math.floor((float) getWidth() * f.getScaleX());
			int y0 = (h - w) / 2;
			m_icon.draw(g, 0, y0, w - 1, w - 1, m_issel);
			if (!isEnabled())
			{
				m_icon.drawDisable(g, 0, y0, w - 1, w - 1);
			}
			g2d.setTransform(f);
		}
		else
		{
			int h = getHeight();
			int w = getWidth();
			int s = Math.min(h, w);
			int x0 = (w > h ? ((w - h) / 2) : 0);
			int y0 = (w < h ? ((h - w) / 2) : 0);
			m_icon.draw(g, x0, y0, s, s, m_issel);
			if (!isEnabled())
			{
				m_icon.drawDisable(g, x0, y0, s, s);
			}
		}
	}

	private void onPropChanged(PropertyChangeEvent e)
	{
		String pname = e.getPropertyName();
		if (Action.SELECTED_KEY.equals(pname))
		{
			Boolean v = (Boolean) e.getNewValue();
			setSelected((v == null) ? false : v);
		}
		if ("enabled".equals(pname))
		{
			Boolean v = (Boolean) e.getNewValue();
			setEnabled((v == null) ? false : v);
		}
	}
}