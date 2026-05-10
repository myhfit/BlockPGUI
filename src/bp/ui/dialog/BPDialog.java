package bp.ui.dialog;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.RepaintManager;

import bp.config.UIConfigs;
import bp.ui.BPComponent;
import bp.ui.container.BPRootContainer;
import bp.ui.util.SystemUIUtil;

public abstract class BPDialog extends JDialog implements BPRootContainer<JDialog>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3932609405977077130L;

	protected Map<String, BPComponent<?>> m_compmap = new HashMap<String, BPComponent<?>>();
	protected boolean m_sysinited;

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

	public void setVisible(boolean flag)
	{
		if (!flag || m_sysinited)
			super.setVisible(flag);
		else
		{
			boolean isd = isDisplayable();
			if (!isd)
			{
				m_sysinited = true;
				super.setVisible(true);
			}
			SystemUIUtil.initWindow(this);
			if (isd)
				super.setVisible(flag);
		}
	}

	protected void init()
	{
		if (initWithUndecorated())
			setUndecorated(true);
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

	protected boolean initWithUndecorated()
	{
		return false;
	}

	protected abstract void initUIComponents();

	protected void initUIConfigs()
	{
		boolean b = UIConfigs.DOUBLE_BUFFER();
		if (!b)
			RepaintManager.currentManager(this).setDoubleBufferingEnabled(b);
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

	public static class BPDialogPopupPar extends BPDialog
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 438779220243175807L;

		protected void initUIComponents()
		{
		}

		protected void initUIConfigs()
		{
			((JPanel) getContentPane()).setDoubleBuffered(false);
		}

		protected void setPrefers()
		{
		}

		protected void initDatas()
		{
		}
	}
}