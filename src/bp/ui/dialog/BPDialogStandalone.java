package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;

import bp.tool.BPToolGUI;
import bp.tool.BPToolGUI.BPToolGUIContainer;
import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.scomp.BPWindowBorder;
import bp.ui.util.UIUtil;

public class BPDialogStandalone extends BPDialogSimple implements WindowListener, BPToolGUIContainer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8686107660927702479L;

	public final static int STMODE_NORMAL = 0;
	public final static int STMODE_UNDECO = 1;
	public final static int STMODE_MINITITLE = 2;

	protected Runnable m_closecb;

	public void setCloseCallback(Runnable cb)
	{
		m_closecb = cb;
	}

	public final static BPDialogStandalone showTool(BPToolGUI tool, int undeco, Object[] params, int cmdbarmode, Function<Integer, Boolean> cmdcallback)
	{
		BPDialogStandalone dlg = null;
		switch (undeco)
		{
			case STMODE_UNDECO:
			case STMODE_MINITITLE:
				dlg = new BPDialogStandaloneUndecorated();
				if (undeco == STMODE_MINITITLE)
					((BPDialogStandaloneUndecorated) dlg).addCaptionBar();
				break;
			default:
				dlg = new BPDialogStandalone();
				break;
		}
		dlg.setCommandCallback(cmdcallback);
		dlg.setCommandBarMode(cmdbarmode);
		Component c = tool.createToolGroup(dlg, params);
		dlg.getContentPane().add(c, BorderLayout.CENTER);
		return dlg;
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
		if (m_closecb != null)
			m_closecb.run();
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	@SuppressWarnings("serial")
	public static class BPDialogStandaloneUndecorated extends BPDialogStandalone implements MouseMotionListener
	{
		protected BPLabel m_lbltitle;
		protected int m_originx;
		protected int m_originy;
		protected int m_originwx;
		protected int m_originwy;
		protected boolean m_isdown;

		protected void initUI()
		{
			// if (initWithUndecorated())
			// {
			BPWindowBorder border = new BPWindowBorder(1, 1, 1, 1);
			border.setup(this);
			// getContentPane().add(makeCaptionBar(), BorderLayout.NORTH);
			// }
			super.initUI();
		}

		protected void initUIComponents()
		{
			super.initUIComponents();
			// if (initWithUndecorated())
			// {
			// getContentPane().add(makeCaptionBar(), BorderLayout.NORTH);
			// }
			addWindowListener(this);
		}

		public void addCaptionBar()
		{
			getContentPane().add(makeCaptionBar(), BorderLayout.NORTH);
		}

		protected JComponent makeCaptionBar()
		{
			JPanel pc = new JPanel();
			{
				pc.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				m_lbltitle = new BPLabel();
				m_lbltitle.setLabelFont();
				pc.addMouseListener(new UIUtil.BPMouseListener(null, this::onTitleDown, this::onTitleUp, null, null));
				pc.addMouseMotionListener(this);
				pc.setLayout(new BorderLayout());
				pc.add(m_lbltitle, BorderLayout.WEST);
				JPanel pr = new JPanel();
				{
					pr.setCursor(Cursor.getDefaultCursor());
					pr.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
					BPToolVIconButton btnclose = new BPToolVIconButton(BPAction.build("close").vIcon(BPIconResV.KILL()).callback(e -> dispose()).getAction());
					if (isAlwaysOnTopSupported())
					{
						BPToolVIconButton btntop = new BPToolVIconButton(BPAction.build("topmost").vIcon(BPIconResV.TOUP()).callback(this::setTopMost).getAction());
						pr.add(btntop);
					}
					pr.add(btnclose);
				}
				pc.add(pr, BorderLayout.EAST);
			}
			return pc;
		}

		protected void setTopMost(ActionEvent e)
		{
			if (isAlwaysOnTopSupported())
				setAlwaysOnTop(!isAlwaysOnTop());
		}

		public void setTitle(String title)
		{
			super.setTitle(title);
			if (m_lbltitle != null)
				m_lbltitle.setText(title);
		}

		protected void onTitleDown(MouseEvent e)
		{
			m_originx = e.getXOnScreen();
			m_originy = e.getYOnScreen();
			Point pt = getLocation();
			m_originwx = pt.x;
			m_originwy = pt.y;
			m_isdown = true;
		}

		protected void onTitleUp(MouseEvent e)
		{
			m_isdown = false;
		}

		protected boolean initWithUndecorated()
		{
			return true;
		}

		public void mouseDragged(MouseEvent e)
		{
			if (!m_isdown)
				return;
			int x = m_originwx + e.getXOnScreen() - m_originx;
			int y = m_originwy + e.getYOnScreen() - m_originy;
			setLocation(x, y);
		}

		public void mouseMoved(MouseEvent e)
		{
		}
	}
}
