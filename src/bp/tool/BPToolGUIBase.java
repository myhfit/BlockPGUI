package bp.tool;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;

import bp.ui.frame.BPFrame;

public abstract class BPToolGUIBase<C extends BPToolGUIBase.BPToolGUIContext> implements BPToolGUI
{
	public void run()
	{
		showTool();
	}

	protected boolean checkRequirement()
	{
		return true;
	}

	public void showTool(Object... params)
	{
		if (!checkRequirement())
			return;
		BPFrameTool f = buildFrame();
		f.setLayout(new BorderLayout());
		BPToolGUIContext context = createToolContext();
		context.initUI(f, params);
		context.initDatas(params);
		f.setContext(context);
		setFramePrefers(f);
		f.setVisible(true);
	}

	public Component createToolGroup(Object... params)
	{
		Container rc = createToolGroupInst();
		BPToolGUIContext context = createToolContext();
		context.initUI(rc, params);
		context.initDatas(params);
		return rc;
	}

	protected Container createToolGroupInst()
	{
		JPanel rc = new JPanel();
		rc.setLayout(new BorderLayout());
		return rc;
	}

	protected abstract C createToolContext();

	protected BPFrameTool buildFrame()
	{
		BPFrameTool f = new BPFrameTool();
		f.setTitle("BlockP Tool - " + getSubTitle());
		return f;
	}

	protected String getSubTitle()
	{
		return getName();
	}

	protected void setFramePrefers(BPFrame f)
	{
		f.setPreferredSize(new Dimension(800, 600));
		f.pack();
		if (!f.isLocationByPlatform())
			f.setLocationRelativeTo(null);
	}

	protected static interface BPToolGUIContext
	{
		void initUI(Container toolgroup, Object... params);

		void initDatas(Object... params);

		default void clearResource()
		{

		}
	}

	protected static class BPFrameTool extends BPFrame implements WindowListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5281557063189501609L;

		protected BPToolGUIContext m_context = null;

		protected void initUIComponents()
		{
			addWindowListener(this);
		}

		public void setContext(BPToolGUIContext context)
		{
			m_context = context;
		}

		protected void initDatas()
		{
		}

		public void windowOpened(WindowEvent e)
		{
		}

		public void windowClosing(WindowEvent e)
		{
		}

		public void windowClosed(WindowEvent e)
		{
			m_context.clearResource();
			m_context = null;
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
}
