package bp.ui.scomp;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bp.config.UIConfigs;

public class BPMenuItem extends JMenuItem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7840476802533736719L;

	public BPMenuItem(Action a)
	{
		super(a);
		setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
	}

	public BPMenuItem(String text)
	{
		super(text);
		setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
	}

	public static class BPMenuItemInTray extends BPMenuItem
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5442792706504526936L;

		public BPMenuItemInTray(String text)
		{
			super(text);
		}

		public BPMenuItemInTray(Action a)
		{
			super(a);
		}

		protected void fireActionPerformed(ActionEvent e)
		{
			Container par = getParent();
			while (par != null && (!(par instanceof JPopupMenu)))
			{
				par = par.getParent();
			}
			if (par == null)
				return;
			JPopupMenu root = (JPopupMenu) par;
			root.setVisible(false);
			super.fireActionPerformed(e);
		}
	}
}
