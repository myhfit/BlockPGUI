package bp.ui.frame;

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bp.config.UIConfigs;
import bp.ui.BPComponent;
import bp.ui.container.BPRootContainer;

@SuppressWarnings("serial")
public abstract class BPFrame extends JFrame implements BPRootContainer<JFrame>
{
	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();

	public BPFrame()
	{
		init();
		setPrefers();
	}

	protected void setPrefers()
	{
		pack();
		if(!isLocationByPlatform())
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
}
