package bp.ui.frame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import bp.config.UIConfigs;
import bp.ui.BPComponent;
import bp.ui.container.BPRootContainer;

@SuppressWarnings("serial")
public abstract class BPFrame extends JFrame implements BPRootContainer<JFrame>
{
	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();

	protected OriginWindowState m_fullscreendata = null;

	public BPFrame()
	{
		init();
		setPrefers();
	}

	protected void setPrefers()
	{
		pack();
		if (!isLocationByPlatform())
			setLocationRelativeTo(null);
	}

	protected void init()
	{
		initUI();
		initDatas();
	}

	protected void initUI()
	{
		clearResource();
		initBPEvents();

		initUIConfigs();
		initUIComponents();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	protected abstract void initUIComponents();

	protected void initUIConfigs()
	{
		((JPanel) getContentPane()).setDoubleBuffered(UIConfigs.DOUBLE_BUFFER());
	}

	protected void initBPEvents()
	{

	}

	public Container getRealContainer()
	{
		return getContentPane();
	}

	protected abstract void initDatas();

	public BPComponentType getComponentType()
	{
		return BPComponentType.FRAME;
	}

	public Map<String, BPComponent<?>> getComponentMap()
	{
		return m_compmap;
	}

	public JFrame getComponent()
	{
		return this;
	}

	public void fullScreen()
	{
		if (m_fullscreendata != null)
		{
			m_fullscreendata.restore(this);
			setVisible(false);
			dispose();
			setUndecorated(m_fullscreendata.originundecorated);
			setVisible(true);
			validate();
			m_fullscreendata = null;
		}
		else
		{
			m_fullscreendata = new OriginWindowState(this);
			setExtendedState(MAXIMIZED_BOTH);
			getJMenuBar().setPreferredSize(new Dimension(0, 0));
			setVisible(false);
			dispose();
			setUndecorated(true);
			setVisible(true);
		}
	}

	protected final static class OriginWindowState
	{
		public int originextendedstate;
		public Dimension lastsize;
		public Point lastpos;
		public Dimension lastmenusize;
		public boolean originundecorated;

		public OriginWindowState(JFrame mf)
		{
			originextendedstate = mf.getExtendedState();
			lastsize = mf.getSize();
			lastpos = mf.getLocation();
			{
				JMenuBar mb = mf.getJMenuBar();
				if (mb != null)
					lastmenusize = mb.getPreferredSize();
			}
			originundecorated = mf.isUndecorated();
		}

		public void restore(JFrame mf)
		{
			mf.setExtendedState(originextendedstate);
			mf.setSize(lastsize);
			mf.setLocation(lastpos);
			if (lastmenusize != null)
			{
				JMenuBar mb = mf.getJMenuBar();
				if (mb != null)
					mb.setPreferredSize(lastmenusize);
			}
		}
	}
}
