package bp.ui.scomp;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bp.config.UIConfigs;

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

	protected JMenuItem createActionComponent(Action a)
	{
		JMenuItem rc = super.createActionComponent(a);
		rc.setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
		return rc;
	}
}
