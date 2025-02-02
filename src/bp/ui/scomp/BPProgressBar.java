package bp.ui.scomp;

import java.awt.Color;

import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPProgressBar extends JProgressBar
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5911647026041095223L;

	protected Color m_selcolor;

	public BPProgressBar()
	{
		setUI(new BPProgressBarUI());
		setBorder(new EmptyBorder(0, 0, 0, 0));
		m_selcolor = getForeground();
	}

	public void setSelectedBackgroundColor(Color color)
	{
		m_selcolor = color;
	}

	public void setSelectedBackground(boolean flag)
	{
		if (flag)
		{
			setBackground(m_selcolor);
			setForeground(UIUtil.mix(UIConfigs.COLOR_TEXTFG(), 128));
		}
		else
		{
			setBackground(UIConfigs.COLOR_TEXTBG());
//			setForeground(m_selcolor);
		}
	}

	public static class BPProgressBarUI extends BasicProgressBarUI
	{
		protected Color m_fg;

		public BPProgressBarUI()
		{
			m_fg = UIConfigs.COLOR_TEXTFG();
		}

		protected Color getSelectionBackground()
		{
			return m_fg;
		}
	}
}
