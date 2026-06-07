package bp.ui.scomp;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;

import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.ui.actions.BPAction;

public class BPMenuItem extends JMenuItem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7840476802533736719L;

	public BPMenuItem(Action a)
	{
		super(a);
		Boolean istitle = (Boolean) a.getValue(BPAction.IS_TITLE);
		if (Boolean.TRUE.equals(istitle))
			setEnabled(false);
		Integer vi = (Integer) a.getValue(Action.MNEMONIC_KEY);
		if (vi != null)
			setMnemonic(vi);
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
			BPGUICore.inPopup(() -> super.fireActionPerformed(e));
		}
	}
}
