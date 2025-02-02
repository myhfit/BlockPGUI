package bp.ui.scomp;

import java.awt.Font;

import javax.swing.JButton;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5804349287338176999L;
	
	protected Object m_userobj;	

	public void setUserObject(Object obj) 
	{
		m_userobj=obj;
	}
	
	public Object getUserObject()
	{
		return m_userobj;
	}	
	
	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.LISTFONT_SIZE()));
	}
}
