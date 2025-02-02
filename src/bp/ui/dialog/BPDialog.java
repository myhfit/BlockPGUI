package bp.ui.dialog;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;

import bp.config.UIConfigs;
import bp.ui.BPComponent;
import bp.ui.container.BPRootContainer;

public abstract class BPDialog extends JDialog implements BPRootContainer<JDialog>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3932609405977077130L;

	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();

	public BPDialog(Frame owner)
	{
		super(owner);
		init();
		setPrefers();
	}

	public BPDialog(Dialog owner)
	{
		super(owner);
		init();
		setPrefers();
	}

	public BPDialog(Window owner)
	{
		super(owner);
		init();
		setPrefers();
	}

	public BPDialog()
	{
		super();
		init();
		setPrefers();
	}

	protected void setPrefers()
	{
		pack();
		setLocationRelativeTo(getParent());
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
		return BPComponentType.DIALOG;
	}

	public Map<String, BPComponent<?>> getComponentMap()
	{
		return m_compmap;
	}

	public JDialog getComponent()
	{
		return this;
	}

	public void close()
	{
		dispose();
	}
}