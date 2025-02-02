package bp.ui.scomp;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.JMenuItem;

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
}
