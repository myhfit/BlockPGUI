package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import bp.config.UIConfigs;
import bp.tool.BPToolGUIDataPipe;
import bp.ui.actions.BPAction;
import bp.ui.util.UIUtil;

public class BPHexPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6820043975224107739L;

	protected BiFunction<Long, Integer, byte[]> m_readcb;
	protected BiConsumer<byte[], Integer> m_previewcb;
	protected byte[] m_bs;
	protected int m_linesize;
	protected String[] m_chs = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	protected final static int XGAP = 2;
	protected final static int YGAP = 2;

	protected int m_chw;
	protected int m_cha;
	protected int m_chh;

	protected long m_pos;

	protected long m_selstart = -1, m_selend = -1, m_selstartm = -1;

	protected int m_lastw;
	protected int m_lasth;

	protected long m_len;

	protected boolean m_isdown;

	protected JScrollBar m_sbar;

	public BPHexPane()
	{
		setFocusable(true);
		m_sbar = new JScrollBar(JScrollBar.VERTICAL);
		m_sbar.addAdjustmentListener(this::onScroll);
		setLayout(new BorderLayout());
		add(m_sbar, BorderLayout.EAST);
		setBackground(UIConfigs.COLOR_TEXTBG());

		addMouseWheelListener(this::onMouseWheel);
		addMouseListener(new UIUtil.BPMouseListener(null, this::onMouseDown, this::onMouseUp, null, this::onMouseLeave));
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		getInputMap().put(ks, "copy");
		getActionMap().put("copy", BPAction.build("").callback(this::onCopy).getAction());

		setLineSize(32);
	}

	public void setLineSize(int linesize)
	{
		m_linesize = linesize;
		updateView();
	}

	public void setFont(Font f)
	{
		super.setFont(f);
		BufferedImage bimg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		FontMetrics fm = bimg.getGraphics().getFontMetrics(f);
		m_chw = fm.charWidth('0');
		m_cha = fm.getAscent();
		m_chh = fm.getHeight();
	}

	public void setup(BiFunction<Long, Integer, byte[]> readcb, long len, BiConsumer<byte[], Integer> previewcb)
	{
		m_readcb = readcb;
		m_len = len;
		m_previewcb = previewcb;
		updateView();
	}

	protected void updateView()
	{
		int w = getWidth();
		int h = getHeight();
		m_lastw = w;
		m_lasth = h;
		int line = (h - YGAP) / m_chh;
		updatePos(m_pos, line * m_linesize, true);
		repaint();
	}

	protected void onScroll(AdjustmentEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			int s = m_sbar.getValue();
			int h = getHeight();
			if (m_len > Integer.MAX_VALUE)
			{

			}
			else
			{
				m_pos = s * m_linesize;
				int line = (h - YGAP) / m_chh;
				updatePos(s * m_linesize, line * m_linesize, false);
				repaint();
			}
		}
	}

	protected void onPage(int delta)
	{
		int h = getHeight();
		int line = (h - YGAP) / m_chh;
		int ori = m_sbar.getValue();
		int v = ori + (line * delta);
		if (v < 0)
			v = 0;
		if (v > m_sbar.getMaximum())
			v = m_sbar.getMaximum();
		if (v != ori)
			m_sbar.setValue(v);
	}

	protected void updatePos(long pos, int size, boolean updatebar)
	{
		if (m_readcb == null)
			return;
		m_bs = m_readcb.apply(pos, size);
		if (updatebar)
		{
			if (m_len > Integer.MAX_VALUE)
			{

			}
			else
			{
				m_sbar.setMinimum(0);
				m_sbar.setValue((int) (pos % m_linesize));
				int line = (int) (m_len / m_linesize + (m_len % m_linesize == 0 ? 0 : 1));
				m_sbar.setMaximum(line);
			}
		}
		if (m_previewcb != null)
			m_previewcb.accept(m_bs, m_linesize);
	}

	protected final static String getHEX(long pos)
	{
		String str = Long.toHexString(pos);
		int l = str.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (16 - l); i++)
		{
			sb.append("0");
		}
		sb.append(str);
		return sb.toString().toUpperCase();
	}

	protected void onCopy(ActionEvent e)
	{
		Clipboard clipboard = getToolkit().getSystemClipboard();
		String text = getSelectedText();
		clipboard.setContents(new StringSelection(text), null);
	}

	private String getSelectedText()
	{
		byte[] bs = m_bs;
		long pos = m_pos;
		StringBuilder sb = new StringBuilder();
		long selstart = m_selstart;
		long selend = m_selend;
		for (int i = 0; i < bs.length; i++, pos++)
		{
			byte b = bs[i];
			byte b1 = (byte) (b & 0xF);
			byte b0 = (byte) (b >> 4 & 0xF);
			if (pos >= m_len)
				break;

			if (selstart <= pos && selend >= pos)
			{
				sb.append(m_chs[b0]);
				sb.append(m_chs[b1]);
			}
			else if (sb.length() > 0)
			{
				break;
			}
		}
		if (sb.length() > 0)
			return sb.toString();
		return null;
	}

	protected void onMouseDown(MouseEvent e)
	{
		int ct = MouseEvent.SHIFT_DOWN_MASK;
		requestFocus();
		int btn = e.getButton();
		if (btn == MouseEvent.BUTTON1)
		{
			if ((e.getModifiersEx() & ct) == ct)
			{
				m_isdown = false;
				long end = getPos(e.getX(), e.getY());
				if (end < m_selstart)
				{
					m_selend = m_selstart;
					m_selstart = end;
				}
				else
				{
					m_selend = end;
				}
				repaint();
			}
			else
			{
				m_isdown = true;
				long pos = getPos(e.getX(), e.getY());
				m_selstart = pos;
				m_selend = pos;
				repaint();
			}
		}
		else if (btn == MouseEvent.BUTTON3)
		{
			showContextMenu(e.getX(), e.getY());
		}
	}

	protected void showContextMenu(int x, int y)
	{
		JPopupMenu pop = new JPopupMenu();
		BPMenuItem mnudp = new BPMenuItem(BPAction.build("Data Pipe...").callback(this::sendToDataPipe).getAction());
		pop.add(mnudp);
		pop.show(this, x, y);
	}

	protected void sendToDataPipe(ActionEvent e)
	{
		byte[] bs = getSelectedBytes();
		BPToolGUIDataPipe tool = new BPToolGUIDataPipe();
		tool.showTool(bs, true);
	}

	protected byte[] getSelectedBytes()
	{
		byte[] rc = null;
		long selstart = m_selstart;
		long selend = m_selend;
		int pos = (int) m_pos;
		if (selstart > -1 && selend > selstart && selstart < Integer.MAX_VALUE && selend < Integer.MAX_VALUE)
		{
			byte[] bs = m_bs;
			int l = (int) selend - (int) selstart;
			rc = new byte[l];
			System.arraycopy(bs, ((int) selstart) - pos, rc, 0, l);
		}
		return rc;
	}

	protected void onMouseUp(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (m_isdown)
			{
				long end = getPos(e.getX(), e.getY());
				if (end < m_selstart)
				{
					m_selend = m_selstart;
					m_selstart = end;
				}
				else
				{
					m_selend = end;
				}
				repaint();
				m_isdown = false;
			}
		}
	}

	protected void onMouseLeave(MouseEvent e)
	{
		m_isdown = false;
	}

	protected long getPos(int x, int y)
	{
		long rc = -1;

		int chw = m_chw;
		int cha = m_cha;
		int chh = m_chh;
		int chp = chh - cha;
		long pos = m_pos;
		int XHEADER = chw * 16;
		int xgap = XGAP + XHEADER + 5;
		int ygap = cha + YGAP;
		if (x < xgap)
		{
			rc = -1;
		}
		else
		{
			int ty = y - ygap + cha;
			int py = ty / chh;
			if (y < ygap + chp)
				py = 0;
			int tx = x - xgap + 2;
			int px = tx / (chw + chw + 4);
			rc = pos + (py * m_linesize) + px;
		}
		return rc;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int w = getWidth();
		int h = getHeight();
		if (w != m_lastw || h != m_lasth)
		{
			updateView();
		}
		g.setColor(UIConfigs.COLOR_TEXTBG());
		g.fillRect(0, 0, getWidth(), getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		int chw = m_chw;
		int cha = m_cha;
		int chh = m_chh;
		int chp = chh - cha;
		byte[] bs = m_bs;
		long pos = m_pos;
		int XHEADER = chw * 16;
		int xgap = XGAP + XHEADER + 5;
		int ygap = cha + YGAP;
		int x = xgap;
		int y = ygap;

		if (bs == null)
			return;

		long selstart = m_selstart, selend = m_selend;

		g.setColor(UIConfigs.COLOR_WEAKBORDER());
		g.drawLine(xgap - 3, 0, xgap - 3, h);
		g.setColor(UIConfigs.COLOR_TEXTFG());
		for (int i = 0; i < bs.length; i++, pos++)
		{
			byte b = bs[i];
			byte b1 = (byte) (b & 0xF);
			byte b0 = (byte) (b >> 4 & 0xF);
			if (pos >= m_len)
				break;
			if (i % m_linesize == 0)
			{
				g.drawString(getHEX(pos), XGAP, y);
			}

			if (selstart <= pos && selend >= pos)
			{
				g.setColor(UIConfigs.COLOR_WEAKBORDER());
				g.fillRect(x - 2, y + chp - chh, chw + chw + 4, chh);
				g.setColor(UIConfigs.COLOR_TEXTFG());
			}
			g.drawString(m_chs[b0], x, y);
			x += chw;
			g.drawString(m_chs[b1], x, y);
			x += (chw + 4);
			if ((i + 1) % m_linesize == 0)
			{
				y += chh;
				x = xgap;
			}
		}
	}

	private void onMouseWheel(MouseWheelEvent e)
	{
		int c = e.getUnitsToScroll();
		int ori = m_sbar.getValue();
		int v = ori + c;
		if (v < 0)
			v = 0;
		if (v > m_sbar.getMaximum())
			v = m_sbar.getMaximum();
		if (v != ori)
			m_sbar.setValue(v);
	}

	private void onKeyDown(KeyEvent e)
	{
		int keycode = e.getKeyCode();
		if (keycode == KeyEvent.VK_PAGE_DOWN)
		{
			onPage(1);
		}
		else if (keycode == KeyEvent.VK_PAGE_UP)
		{
			onPage(-1);
		}

	}
}
