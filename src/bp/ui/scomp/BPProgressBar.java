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
			if (UIUtil.checkSameDirection(m_selcolor, UIConfigs.COLOR_TEXTBG()))
			{
				float[] hsb = Color.RGBtoHSB(m_selcolor.getRed(), m_selcolor.getGreen(), m_selcolor.getBlue(), new float[3]);
				hsb[2] = (0.5f - hsb[2]) / 3f + hsb[2];// +=0.2f;
				setForeground(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
			}
			else
			{
				setForeground(UIUtil.mix(UIConfigs.COLOR_TEXTFG(), 128));
			}
		}
		else
		{
			setBackground(UIConfigs.COLOR_TEXTBG());
		}
	}

	public static class BPProgressBarUI extends BasicProgressBarUI
	{
		protected Color m_fg;
		protected Color m_bg;
		protected float m_gf;
		protected float m_gb;
		protected Color m_selcolor;

		public BPProgressBarUI()
		{
			m_fg = UIConfigs.COLOR_TEXTFG();
			m_bg = UIConfigs.COLOR_TEXTBG();
			m_gf = UIUtil.rgbToGray(m_fg);
			m_gb = UIUtil.rgbToGray(m_bg);
		}

		protected Color getSelectionBackground()
		{
			return useFar(progressBar.getBackground(), m_fg, m_bg, m_gf, m_gb);
		}

		protected Color getSelectionForeground()
		{
			return useFar(progressBar.getForeground(), m_fg, m_bg, m_gf, m_gb);
		}

		protected Color useFar(Color c, Color c1, Color c2, float g1, float g2)
		{
			float g = UIUtil.rgbToGray(c);
			return (Math.abs(g - g1) < Math.abs(g - g2)) ? c2 : c1;
		}
	}
}
