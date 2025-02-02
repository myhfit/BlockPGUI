package bp.ui.scomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.lang.ref.WeakReference;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;
import bp.util.Std;

public class BPCodeLinePanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7256679125506021339L;

	protected WeakReference<JEditorPane> m_textcompref;
	protected WeakReference<JScrollPane> m_scrollcompref;
	protected Color m_bd = UIConfigs.COLOR_WEAKBORDER();
	protected Color m_fg = UIUtil.mix2Plain(UIConfigs.COLOR_TEXTFG(), UIConfigs.COLOR_TEXTBG(), 80);

	public void setup(JEditorPane textcomp, JScrollPane scrollcomp)
	{
		m_textcompref = new WeakReference<JEditorPane>(textcomp);
		m_scrollcompref = new WeakReference<JScrollPane>(scrollcomp);
		setFont(textcomp.getFont());
		setBackground(UIConfigs.COLOR_TEXTBG());
	}

	public void setBackground(Color bg)
	{
		super.setBackground(bg);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		JEditorPane textcomp = m_textcompref.get();
		JScrollPane scrollcomp = m_scrollcompref.get();
		if (textcomp == null || scrollcomp == null)
			return;
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(m_fg);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		Point pt = scrollcomp.getViewport().getViewPosition();
		int start = textcomp.viewToModel(pt);
		int end = textcomp.viewToModel(new Point(pt.x + textcomp.getWidth(), pt.y + scrollcomp.getHeight()));
		Document doc = textcomp.getDocument();
		Element ele = doc.getDefaultRootElement();
		int startline = ele.getElementIndex(start) + 1;
		int endline = ele.getElementIndex(end) + 1;
		FontMetrics fm = g.getFontMetrics();
		int fontheight = fm.getHeight();
		int fontdesc = fm.getDescent();
		int sy = -1;
		try
		{
			Rectangle rect = textcomp.modelToView(start);
			if (rect == null)
				return;
			sy = rect.y - pt.y + fontheight - fontdesc;
		}
		catch (BadLocationException ble)
		{
			Std.err(ble);
		}
		int w = getWidth() - 2;
		int fontwidth = fm.stringWidth("0");
		for (int l = startline, y = sy; l <= endline; y += fontheight, l++)
		{
			int digitnum = getDigitNum(l);
			int offsetx = 0;
			if (digitnum <= 1)
			{
				digitnum = 2;
				offsetx = fontwidth;
			}
			int x = w - digitnum * fontwidth;
			if (x < 0)
			{
				setPreferredSize(new Dimension(digitnum * fontwidth + 3, fontheight));
				invalidate();
			}
			if (l == endline && x >= fontwidth - 2)
			{
				setPreferredSize(new Dimension(digitnum * fontwidth + 3, fontheight));
				invalidate();
			}
			g.drawString(Integer.toString(l), x + offsetx, y);
		}
		g.setColor(m_bd);
		g.drawLine(w, 0, w, getHeight());
	}

	private int getDigitNum(int d)
	{
		if (d < 100000)
		{
			if (d < 100)
			{
				if (d < 10)
					return 1;
				else
					return 2;
			}
			else
			{
				if (d < 1000)
					return 3;
				else
				{
					if (d < 10000)
						return 4;
					else
						return 5;
				}
			}
		}
		else
		{
			if (d < 10000000)
			{
				if (d < 1000000)
					return 6;
				else
					return 7;
			}
			else
			{
				if (d < 100000000)
					return 8;
				else if (d < 1000000000)
					return 9;
				else
					return 10;
			}
		}
	}
}
