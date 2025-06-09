package bp.ui.scomp;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;

import bp.config.UIConfigs;
import bp.ui.util.SwingUtil;

public class BPPopupMenuTray extends JPopupMenu implements WindowFocusListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3507238829517933936L;

	protected JDialog m_w;
	protected Object m_lockobj;

	public BPPopupMenuTray()
	{
		m_lockobj = new Object();
		setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
	}

	public void showTray(int x, int y)
	{
		closeTrayDialog();

		createTrayDialog((int) (x / UIConfigs.GC_SCALE()), (int) (y / UIConfigs.GC_SCALE()));
		this.show(m_w, 0, 0);
	}

	protected void createTrayDialog(int x, int y)
	{
		synchronized (m_lockobj)
		{
			JDialog w = new JDialog();
			w.setUndecorated(true);
			w.setSize(1, 1);
			w.setLocation(x, y);
			w.addWindowFocusListener(this);
			w.setVisible(true);
			m_w = w;
		}
	}

	protected void closeTrayDialog()
	{
		JDialog w;
		synchronized (m_lockobj)
		{
			w = m_w;
			m_w = null;
		}
		if (w != null)
		{
			w.removeWindowFocusListener(this);
			w.dispose();
		}
	}

	protected void firePopupMenuWillBecomeInvisible()
	{
		super.firePopupMenuWillBecomeInvisible();
		setInvoker(null);
		closeTrayDialog();

		SwingUtil.refreshDisplay(GraphicsEnvironment.getLocalGraphicsEnvironment());
	}

	public void windowGainedFocus(WindowEvent e)
	{
	}

	public void windowLostFocus(WindowEvent e)
	{
		setVisible(false);
	}
}
