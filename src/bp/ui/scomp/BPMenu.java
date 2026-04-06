package bp.ui.scomp;

import java.awt.Font;
import java.util.function.Supplier;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPMenu extends JMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8950631119210970007L;

	public BPMenu(String text)
	{
		super(text);
		setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
	}

	public BPMenu(Action act)
	{
		super((String) act.getValue(Action.NAME));
		Integer vi = (Integer) act.getValue(Action.MNEMONIC_KEY);
		if (vi != null)
			setMnemonic(vi);
		setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
	}

	protected JMenuItem createActionComponent(Action a)
	{
		JMenuItem rc = super.createActionComponent(a);
		rc.setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
		return rc;
	}

	public static class BPMenuDynamic extends BPMenu implements MenuListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5350712114911557827L;

		protected Supplier<Action[]> m_dynafunc;
		protected boolean m_queried;
		protected boolean m_cc;
		protected boolean m_oow;

		public BPMenuDynamic(String text, Supplier<Action[]> dynafunc)
		{
			super(text);
			m_dynafunc = dynafunc;
			addMenuListener(this);
		}

		public void setOutOfWindow(boolean flag)
		{
			m_oow = flag;
		}

		public void setClearOnClose(boolean flag)
		{
			m_cc = flag;
		}

		public void menuSelected(MenuEvent e)
		{
			if (!m_queried)
			{
				m_queried = true;
				Action[] acts = m_dynafunc.get();
				if (acts != null)
				{
					JComponent[] subs = UIUtil.makeMenuItems(acts, m_oow);
					for (JComponent sub : subs)
						add(sub);
				}
			}
		}

		public void menuDeselected(MenuEvent e)
		{
			if (m_cc)
			{
				m_queried = false;
				removeAll();
			}
		}

		public void menuCanceled(MenuEvent e)
		{
			m_dynafunc = null;
		}
	}
}
