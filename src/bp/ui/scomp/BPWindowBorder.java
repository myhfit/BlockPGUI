package bp.ui.scomp;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.border.EmptyBorder;

public class BPWindowBorder extends EmptyBorder implements MouseMotionListener, MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3340878019870005621L;

	// private Window m_win;

	public BPWindowBorder(int top, int left, int bottom, int right)
	{
		super(top, left, bottom, right);
	}

	public void setup(Window win)
	{
		JPanel rp = ((JPanel) ((RootPaneContainer) win).getContentPane());
		rp.setOpaque(false);
		rp.setBorder(this);
	}

	public void mouseDragged(MouseEvent e)
	{
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
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

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		g.drawRect(x, y, width - 1, height - 1);
	}
}
