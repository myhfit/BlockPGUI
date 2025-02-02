package bp.ui.scomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.border.EmptyBorder;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPCodeBorder extends EmptyBorder
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6332304501831843202L;

	protected Color m_bd = UIConfigs.COLOR_WEAKBORDER();
	protected Color m_fg = UIUtil.mix2Plain(UIConfigs.COLOR_TEXTFG(), UIConfigs.COLOR_TEXTBG(), 80);
	protected FontMetrics m_fm = null;
	protected int m_lhw = 0;

	protected Component m_comp = null;

	protected int m_linecount;

	public BPCodeBorder(Component comp)
	{
		super(0, 0, 0, 0);
		m_comp = comp;
	}

	public void paintBorder(Component c, Graphics g, int x0, int y0, int w, int h)
	{
		if (m_fm == null)
			return;
		BPTextPane cp = (BPTextPane) c;
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(m_fg);
		Font font = cp.getFont();

		AffineTransform f = g2d.getTransform();
		AffineTransform f2 = AffineTransform.getScaleInstance(1, 1);
		f2.translate((int) f.getTranslateX(), (int) f.getTranslateY());
		g2d.setTransform(f2);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		Font font2 = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * f.getScaleY()));
		g.setFont(font2);
		Rectangle rect = g.getClipBounds();
		int llh = m_fm.getHeight();
		float lh = (float) (llh * f.getScaleY());
		int ascent = m_fm.getAscent();
		int sy = rect.y - (int) (rect.y % lh) + (int) (ascent * f.getScaleY()) - ((int) lh);
		float y = sy;
		int lhw = (int) (m_lhw * f.getScaleY());
		for (; y < (rect.y + rect.height + lh); y += lh)
		{
			g.drawString(fixLineHeaderStr(Integer.toString((int) Math.floor(y / lh) + 1), lhw), 0, (int) y);
		}
		g.setColor(m_bd);
		g.drawLine(left - 1, sy, left - 1, (int) y);
		g2d.setTransform(f);
	}

	private final static String fixLineHeaderStr(String lh, int lhw)
	{
		int l = lh.length();
		String s = "";
		if (l < lhw)
		{
			int c = lhw - l;
			for (int i = 0; i < c; i++)
			{
				s += " ";
			}
		}
		s += lh;
		return s;
	}

	public void setupBorder(FontMetrics fm, int lhw, float scale)
	{
		if (lhw != m_lhw || fm != m_fm)
		{
			m_fm = fm;
			m_lhw = lhw;
			String ts = "";
			for (int i = 0; i < lhw; i++)
			{
				ts += "0";
			}
			Image img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.getGraphics();
			Rectangle2D rect = m_fm.getStringBounds(ts, g);
			int w = (int) (rect.getX() + rect.getWidth());
			g.dispose();
			left = (int) (w * scale) + 1;
			m_comp.revalidate();
			m_comp.repaint();
		}
	}

	public void clicked(MouseEvent e)
	{
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		
	}
}
