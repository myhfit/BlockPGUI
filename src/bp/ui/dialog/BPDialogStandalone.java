package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Function;

import bp.tool.BPToolGUI;
import bp.tool.BPToolGUI.BPToolGUIContainer;

public class BPDialogStandalone extends BPDialogSimple implements WindowListener, BPToolGUIContainer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8686107660927702479L;

	protected Runnable m_closecb;

	protected void initUIComponents()
	{
		super.initUIComponents();
		addWindowListener(this);
	}

	public void setCloseCallback(Runnable cb)
	{
		m_closecb = cb;
	}

	public final static BPDialogStandalone showTool(BPToolGUI tool, Object[] params, int cmdbarmode, Function<Integer, Boolean> cmdcallback)
	{
		BPDialogStandalone dlg = new BPDialogStandalone();
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
}
