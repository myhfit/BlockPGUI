package bp.ui.scomp;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil.ActionRunnable;

public class BPToolSQButton extends JLabel implements MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8662713082517570937L;

	protected Runnable m_act;

	public BPToolSQButton(Action action)
	{
		this((Icon) action.getValue(Action.SMALL_ICON), () ->
		{
			action.actionPerformed(null);
		});
		String tooltip = (String) action.getValue(Action.SHORT_DESCRIPTION);
		if (tooltip != null)
			setToolTipText(tooltip);
		setEnabled(action.isEnabled());
		m_act = new ActionRunnable(action);
	}

	public BPToolSQButton(Icon icon, Runnable act)
	{
		super(icon);
		m_act = act;
		addMouseListener(this);
	}

	public BPToolSQButton(String text, Action act)
	{
		this(text, () ->
		{
			act.actionPerformed(null);
		});
		setEnabled(act.isEnabled());
	}

	public BPToolSQButton(String text, Runnable act)
	{
		super(text);
		setHorizontalAlignment(SwingConstants.CENTER);
		m_act = act;
		Font f = getFont();
		setFont(new Font(UIConfigs.LABEL_FONT_NAME(), f.getStyle(), (int) Math.floor(UIConfigs.TEXTFIELDFONT_SIZE())));
		addMouseListener(this);
	}

	public void paint(Graphics g)
	{
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		super.paint(g);
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			if (isEnabled())
				m_act.run();
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
}
