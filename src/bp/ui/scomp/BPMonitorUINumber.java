package bp.ui.scomp;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;
import bp.util.NumberUtil;

public class BPMonitorUINumber extends JComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1481607385045110366L;

	protected List<Number> m_vs = null;
	protected MonitorRenderMode m_rendermode = MonitorRenderMode.TEXT_ONLY;
	protected MonitorFormatMode m_formatmode = null;
	protected volatile String m_text = null;

	public BPMonitorUINumber()
	{
		m_vs = new ArrayList<Number>();
	}

	public void appendValue(Number v)
	{
		m_vs.add(v);
		checkResize(10000, 100);
		Container c = getFocusCycleRootAncestor();
		if (c.isVisible())
			repaint();
	}

	public void setRenderMode(MonitorRenderMode mode)
	{
		m_rendermode = mode;
	}

	public void setFormatMode(MonitorFormatMode mode)
	{
		m_formatmode = mode;
	}

	protected Number getLastValue()
	{
		List<Number> vs = m_vs;
		int l = vs.size();
		if (l > 0)
		{
			return vs.get(l - 1);
		}
		return null;
	}

	protected String getLastText()
	{
		Number v = getLastValue();
		if (v != null)
		{
			return formatValue(v);
		}
		return "";
	}

	protected String formatValue(Number v)
	{
		MonitorFormatMode fm = m_formatmode;
		if (fm == null || v == null)
			return "" + v;
		switch (fm)
		{
			case PERCENT:
				return NumberUtil.formatPercent(v);
			case INTEGER:
			{
				if (v instanceof Double || v instanceof Float)
					return Long.toString(Math.round(v.doubleValue()));
				else
					return Long.toString(v.longValue());
			}
			case FLOAT:
				return Double.toString(v.doubleValue());
		}
		return "";
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));
	}

	public void setText(String text)
	{
		m_text = text;
	}

	protected void checkResize(int limit, int delta)
	{
		List<Number> vs = m_vs;
		int l = vs.size();
		if (l > (limit + delta))
		{
			List<Number> nvs = new ArrayList<Number>(m_vs.subList(l - limit, l));
			m_vs = nvs;
		}
	}

	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		// g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		// RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		int w = getWidth();
		int h = getHeight();

		AffineTransform gtf = g2d.getTransform();
		float fgs = 1f;
		if (gtf != null)
		{
			fgs = (float) gtf.getScaleY();
		}
		if (fgs != 1f)
		{
			gtf.setToScale(1, 1);
			g2d.setTransform(gtf);
		}

		w = Math.round(w * fgs);
		h = Math.round(h * fgs);
		checkResize(w, 10);

		g.setColor(UIConfigs.COLOR_TEXTBG());
		g.fillRect(0, 0, w, h);
		g.setColor(UIConfigs.COLOR_TEXTFG());

		paintDashBoard(g, w, h);
		paintCenterText(g, w, h);
	}

	protected void paintDashBoard(Graphics g, int w, int h)
	{
		MonitorRenderMode rmode = m_rendermode;
		int lx = -1, ly = -1;
		int th = h;
		switch (rmode)
		{
			case STACK_HORIZONTAL:
			{
				List<Number> vs = new ArrayList<Number>(m_vs);
				double vmax = getMax();
				int l = vs.size();
				for (int i = 0; i < l; i++)
				{
					int x = w - 1 - (l - i);
					int y = (int) (th - ((double) th / vmax * vs.get(i).doubleValue()));
					if (x >= 0 && lx != -1 && ly != -1)
						g.drawLine(lx, ly, x, y);
					lx = x;
					ly = y;
				}
				break;
			}
			default:
			{

			}
		}
	}

	protected double getMax()
	{
		return 1;
	}

	protected void paintCenterText(Graphics g, int w, int h)
	{
		String text = m_text;
		if (text == null)
			text = getLastText();
		if (text == null)
			return;
		FontMetrics fm = g.getFontMetrics();
		LineMetrics lm = g.getFontMetrics().getLineMetrics(text, g);
		Rectangle2D rect = fm.getStringBounds(text, g);
		int sw = (int) rect.getWidth();
		int sh = (int) ((int) lm.getHeight() - lm.getDescent());
		int x = (int) Math.round(w / 2 - (sw / 2));
		int y = (int) Math.round(h / 2 + (sh / 2));
		g.drawString(text, x, y);
	}

	public static enum MonitorFormatMode
	{
		PERCENT, INTEGER, FLOAT
	}

	public static enum MonitorRenderMode
	{
		TEXT_ONLY, STACK_HORIZONTAL, STACK_VERTICAL, HORIZONTAL, VERTICAL,
	}
}
