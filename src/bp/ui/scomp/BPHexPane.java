package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil;
import bp.util.TextUtil;

public class BPHexPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6820043975224107739L;

	protected BiFunction<Long, Integer, byte[]> m_readcb;
	protected BiConsumer<byte[], Integer> m_previewcb;
	protected BiConsumer<Long, Long> m_poscb;

	protected byte[] m_bs;
	protected int m_linesize;
	protected String[] m_chs = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	protected final static int XGAP = 2;
	protected final static int YGAP = 1;

	protected int m_chw;
	protected int m_cha;
	protected int m_chh;

	protected long m_pos;

	protected long m_selstart = -1, m_selend = -1, m_selpos = -1;
	protected long m_rselstart = -1, m_rselend = -1;

	protected int m_lastw;
	protected int m_lasth;

	protected long m_len;

	protected boolean m_isdown;
	protected int[] m_downpt;
	protected boolean m_fullscroll;

	protected JScrollBar m_sbar;

	protected Action[] m_actions;

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
		addMouseMotionListener(new UIUtil.BPMouseMotionListener(this::onMouseDrag, null));
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
		getInputMap().put(KeyStroke.getKeyStroke("control L"), "locate");
		getActionMap().put("locate", BPAction.build("locate").callback(this::onShowLocate).getAction());

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		getInputMap().put(ks, "copy");
		getActionMap().put("copy", BPAction.build("").callback(this::onCopy).getAction());
		setContextActions(null);

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

	public void setFullScroll(boolean flag)
	{
		m_fullscroll = flag;
	}

	public void setup(BiFunction<Long, Integer, byte[]> readcb, long len, BiConsumer<byte[], Integer> previewcb, BiConsumer<Long, Long> poscb)
	{
		m_readcb = readcb;
		m_len = len;
		m_previewcb = previewcb;
		m_poscb = poscb;
		updateView();
	}

	public void onShowLocate(ActionEvent e)
	{
		String addr = UIStd.input("", "Dec or 0x+Hex", "Input address");
		long l = -1;
		if (addr.startsWith("0x"))
		{
			StringBuilder sb = new StringBuilder();
			for (char c : addr.toCharArray())
			{
				if (c == ' ' || (c > '0' && c < '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))
				{
					UIStd.info("Address not correct");
					return;
				}
				sb.append(c);
			}
			l = Long.parseLong(sb.toString(), 16);
		}
		else
		{
			l = Long.parseLong(addr.trim(), 10);
		}
		if (l >= 0)
		{
			m_pos = l;
			setSelection(l, l);
			updateView();
		}
	}

	public void ensurePosition(long pos)
	{
		if (pos < m_pos)
			m_pos = pos - pos % m_linesize;
		else if (pos > m_pos + (m_linesize * 20))
		{
			long d = pos % m_linesize;
			if (d != 0)
				pos += (m_linesize - d);
			int h = getHeight();
			int line = (h / m_chh) - 1;
			pos = pos - (line * m_linesize);
			if (pos > 0)
				m_pos = pos;
		}
	}

	public void updateView()
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
		if (m_fullscroll || !e.getValueIsAdjusting())
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

	public JScrollBar getScrollBar()
	{
		return m_sbar;
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

	protected void onMoveCursor(int delta, boolean isshift)
	{
		if (!isshift)
		{
			long ss = m_selpos + delta;

			if ((ss >= m_len || ss < 0))
				return;
			m_selstart = ss;
			m_selend = ss;
			m_selpos = ss;

			if (m_selstart > -1 && m_selstart < m_len && m_selend > -1 && m_selend < m_len)
			{
				m_rselstart = m_selstart;
				m_rselend = m_selend;
			}
			else
			{
				if (m_selstart == m_selend)
				{
					m_rselstart = -1;
					m_rselend = -1;
				}
				else if ((m_selstart >= m_len && m_selend >= m_len) || (m_selstart < 0 && m_selend < 0))
				{
					m_rselstart = -1;
					m_rselend = -1;
				}
				else
				{
					m_rselstart = fixPos(m_selstart);
					m_rselend = fixPos(m_selend);
				}
			}
			if (m_rselstart > m_rselend)
			{
				long t = m_rselend;
				m_rselend = m_rselstart;
				m_rselstart = t;
			}
			ensurePosition(ss);
			updateView();
			LogicUtil.IFVU(m_poscb, cb -> cb.accept(m_rselstart, m_rselend));
		}
		else
		{
			m_selpos = m_selpos + delta;
			if (m_selpos > m_len)
				m_selpos = m_len - 1;
			if (m_selpos < 0)
				m_selpos = 0;
			m_selend = m_selpos;

			if (m_selstart > -1 && m_selstart < m_len && m_selend > -1 && m_selend < m_len)
			{
				m_rselstart = m_selstart;
				m_rselend = m_selend;
			}
			else
			{
				if (m_selstart == m_selend)
				{
					m_rselstart = -1;
					m_rselend = -1;
				}
				else if ((m_selstart >= m_len && m_selend >= m_len) || (m_selstart < 0 && m_selend < 0))
				{
					m_rselstart = -1;
					m_rselend = -1;
				}
				else
				{
					m_rselstart = fixPos(m_selstart);
					m_rselend = fixPos(m_selend);
				}
			}
			if (m_rselstart > m_rselend)
			{
				long t = m_rselend;
				m_rselend = m_rselstart;
				m_rselstart = t;
			}
			ensurePosition(m_selpos);
			updateView();
			LogicUtil.IFVU(m_poscb, cb -> cb.accept(m_rselstart, m_rselend));
		}
	}

	public void updateLen(long len)
	{
		m_len = len;
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
				int line = (int) (m_len / m_linesize + (m_len % m_linesize == 0 ? 0 : 1));
				m_sbar.setMaximum(line);
				m_sbar.setValue((int) (pos / m_linesize));
			}
		}
		if (m_previewcb != null)
			m_previewcb.accept(m_bs, m_linesize);
	}

	public final static String getHEXStr(long pos)
	{
		String str = Long.toHexString(pos);
		int l = str.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (16 - l); i++)
		{
			sb.append("0");
		}
		sb.append(str);
		sb.insert(8, " ");
		return sb.toString().toUpperCase();
	}

	protected void onCopy(ActionEvent e)
	{
		Clipboard clipboard = getToolkit().getSystemClipboard();
		String text = getSelectedHex();
		clipboard.setContents(new StringSelection(text), null);
	}

	protected void onCopyText(ActionEvent e)
	{
		Clipboard clipboard = getToolkit().getSystemClipboard();
		byte[] bs = getSelectedBytes();
		String text = TextUtil.toString(bs, "utf-8");
		clipboard.setContents(new StringSelection(text), null);
	}

	public long[] getSelection()
	{
		return new long[] { m_rselstart, m_rselend };
	}

	public void setSelection(long start, long end)
	{
		m_selstart = start;
		m_selend = end;
		m_selpos = end;

		if (m_selstart > -1 && m_selstart < m_len && m_selend > -1 && m_selend < m_len)
		{
			m_rselstart = m_selstart;
			m_rselend = m_selend;
		}
		else
		{
			if (start == end)
			{
				m_rselstart = -1;
				m_rselend = -1;
			}
			else
			{
				m_rselstart = fixPos(m_rselstart);
				m_rselend = fixPos(m_rselend);
			}
		}
		LogicUtil.IFVU(m_poscb, cb -> cb.accept(m_rselstart, m_rselend));
	}

	protected long fixPos(long pos)
	{
		if (pos < 0)
			pos = 0;
		if (pos > m_len)
			pos = m_len - 1;
		return pos;
	}

	public void setSelectionEnd(long end)
	{
		m_selend = end;
		m_selpos = end;

		if (m_selstart > -1 && m_selstart < m_len && m_selend > -1 && m_selend < m_len)
		{
			m_rselstart = m_selstart;
			m_rselend = m_selend;
		}
		else
		{
			if (m_selstart == m_selend)
			{
				m_rselstart = -1;
				m_rselend = -1;
			}
			else if ((m_selstart >= m_len && m_selend >= m_len) || (m_selstart < 0 && m_selend < 0))
			{
				m_rselstart = -1;
				m_rselend = -1;
			}
			else
			{
				m_rselstart = fixPos(m_selstart);
				m_rselend = fixPos(m_selend);
			}
		}
		if (m_rselstart > m_rselend)
		{
			long t = m_rselend;
			m_rselend = m_rselstart;
			m_rselstart = t;
		}
		LogicUtil.IFVU(m_poscb, cb -> cb.accept(m_rselstart, m_rselend));
	}

	private String getSelectedHex()
	{
		byte[] bs = getSelectedBytes();
		int pos = 0;
		int len = bs.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bs.length; i++, pos++)
		{
			byte b = bs[i];
			byte b1 = (byte) (b & 0xF);
			byte b0 = (byte) (b >> 4 & 0xF);
			if (pos >= len)
				break;

			sb.append(m_chs[b0]);
			sb.append(m_chs[b1]);
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
				long end = getPos(e.getX(), e.getY(), 2);
				setSelectionEnd(end);
				repaint();
			}
			else
			{
				m_isdown = true;
				m_downpt = new int[] { e.getX(), e.getY() };
				long pos = getPos(e.getX(), e.getY(), 2);
				setSelection(pos, pos);
				repaint();
			}
		}
		else if (btn == MouseEvent.BUTTON3)
		{
			showContextMenu(e.getX(), e.getY());
		}
	}

	public void setContextActions(List<Action> actions)
	{
		Action actcopy = BPActionHelpers.getAction(BPActionConstCommon.RAWET_CTX_MNUCOPYHEX, this::onCopy);
		Action actcopytext = BPActionHelpers.getAction(BPActionConstCommon.RAWET_CTX_MNUCOPYTEXT, this::onCopyText);
		List<Action> acts = new ArrayList<Action>();
		acts.add(actcopy);
		acts.add(actcopytext);
		acts.add(BPAction.separator());
		if (actions != null)
			acts.addAll(actions);
		m_actions = acts.toArray(new Action[acts.size()]);
	}

	protected void showContextMenu(int x, int y)
	{
		if (m_actions != null && m_actions.length > 0)
		{
			JPopupMenu pop = new JPopupMenu();
			JComponent[] comps = UIUtil.makeMenuItems(m_actions);
			for (JComponent comp : comps)
			{
				pop.add(comp);
			}
			pop.show(this, x, y);
		}
	}

	public byte[] getSelectedBytes()
	{
		byte[] rc = null;
		long selstart = m_rselstart;
		long selend = m_rselend;
		rc = getBytes(selstart, selend);
		return rc;
	}

	public byte[] getBytes(long start, long end)
	{
		byte[] rc = null;
		if (start > -1 && end >= start && start < Integer.MAX_VALUE && end < Integer.MAX_VALUE)
		{
			int l = (int) end - (int) start + 1;
			if (l <= 0)
			{
				rc = null;
			}
			else
			{
				rc = m_readcb.apply(start, l);
			}
		}
		return rc;
	}

	protected boolean checkSamePt(int x, int y)
	{
		if (m_downpt == null)
			return false;
		int r = Math.abs(m_downpt[0] - x) + Math.abs(m_downpt[1] - y);
		if (r > 4)
			return false;
		return true;
	}

	protected void onMouseDrag(MouseEvent e)
	{
		if (m_isdown)
		{
			if (!checkSamePt(e.getX(), e.getY()))
			{
				long end = getPos(e.getX(), e.getY(), 1);
				setSelectionEnd(end);
				repaint(1);
			}
		}
	}

	protected void onMouseUp(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (m_isdown && !checkSamePt(e.getX(), e.getY()))
			{
				long end = getPos(e.getX(), e.getY(), 2);
				setSelectionEnd(end);
				repaint();
				m_isdown = false;
				m_downpt = null;
			}
		}
	}

	protected void onMouseLeave(MouseEvent e)
	{
		m_isdown = false;
		m_downpt = null;
	}

	protected long getPos(int x, int y, int fixoutofbound)
	{
		long rc = -1;

		int chw = m_chw;
		int cha = m_cha;
		int chh = m_chh;
		int chp = chh - cha;
		long pos = m_pos;
		int XHEADER = chw * 17;
		int YHEADER = chh;
		int xgap = XGAP + XHEADER + 5;
		int ygap = cha + YGAP + YHEADER;

		int midgap = m_linesize > 16 ? 33 * chw : -1;
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
			int px = (tx - (midgap > 0 ? (tx > midgap ? chw : 0) : 0)) / (chw + chw + 4);
			if (fixoutofbound == 1)
			{
				if (px >= m_linesize)
					px = m_linesize - 1;
				rc = pos + (py * m_linesize) + px;
			}
			else if (fixoutofbound == 2)
			{
				if (px >= m_linesize)
				{
					long trc = pos + (py * m_linesize) + m_linesize - 1;
					if (trc == m_len - 1)
						rc = trc + 1;
					else
						rc = trc;
				}
				else
				{
					rc = pos + (py * m_linesize) + px;
				}
			}
			else
			{
				rc = pos + (py * m_linesize) + px;
			}
		}
		if (rc > m_len && fixoutofbound == 1)
			rc = m_len - 1;
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
		Color hf = UIConfigs.COLOR_TEXTHALF();
		Color fg = UIConfigs.COLOR_TEXTFG();
		Color bg = UIConfigs.COLOR_TEXTBG();
		Color bc = UIConfigs.COLOR_WEAKBORDER();
		Color h3f = UIConfigs.COLOR_TEXT3QUARTER();

		Font f = getFont();
		Font f2 = f.deriveFont(Font.BOLD);

		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		int chw = m_chw;
		int cha = m_cha;
		int chh = m_chh;
		int chp = chh - cha;
		byte[] bs = m_bs;
		long pos = m_pos;
		int XHEADER = chw * 17;
		int YHEADER = chh;
		int xgap = XGAP + XHEADER + 5;
		int ygap = cha + YGAP + YHEADER;
		int x = xgap;
		int y = ygap;
		String[] chs = m_chs;
		int linesize = m_linesize;
		long len = m_len;
		boolean isinsel = false;

		if (bs == null)
			return;

		long selstart = m_rselstart, selend = m_rselend;

		g.setColor(bc);
		g.drawLine(xgap - 3, 0, xgap - 3, h);
		g.drawLine(0, YGAP + YHEADER - 1, w, YGAP + YHEADER - 1);

		{
			byte selstartx = (byte) (selstart % (long) linesize);
			byte selendx = (byte) (selend % (long) linesize);
			int selb = 0;
			if (selend - selstart >= linesize)
				selb = -1;
			else if (selendx < selstartx)
				selb = 1;

			g.setColor(hf);
			int hx = x;
			int hy = cha + YGAP;
			if (selb == -1)
			{
				g.setFont(f2);
				g.setColor(fg);
			}
			boolean issel;
			for (byte b = 0; b < linesize; b++)
			{
				byte b1 = (byte) (b & 0xF);
				byte b0 = (byte) (b >> 4 & 0xF);
				issel = false;
				if (selb == 0 && b >= selstartx && b <= selendx)
				{
					issel = true;
				}
				else if (selb == 1 && (b >= selstartx || b <= selendx))
				{
					issel = true;
				}
				if (issel)
				{
					g.setFont(f2);
					g.setColor(fg);
				}
				else if (selb != -1)
				{
					g.setColor(hf);
				}
				g.drawString(chs[b0], hx, hy);
				hx += chw;
				g.drawString(chs[b1], hx, hy);
				hx += (chw + 4);
				if (b1 == 0x0F)
					hx += chw;
			}
			g.setFont(f);
		}

		for (int i = 0; i < bs.length; i++, pos++)
		{
			byte b = bs[i];
			byte b1 = (byte) (b & 0xF);
			byte b0 = (byte) (b >> 4 & 0xF);
			if (pos >= len)
				break;

			isinsel = selstart <= pos && selend >= pos;
			if (i % linesize == 0)
			{
				if (selstart <= pos + linesize - 1 && selend >= pos)
				{
					g.setColor(fg);
					g.setFont(f2);
					g.drawString(getHEXStr(pos), XGAP, y);
					g.setFont(f);
				}
				else
				{
					g.setColor(hf);
					g.drawString(getHEXStr(pos), XGAP, y);
					g.setColor(fg);
				}
			}

			if (selstart <= pos && selend >= pos)
			{
				g.setColor(h3f);
				g.fillRect(x - 2, y + chp - chh, chw + chw + 4, chh);
				g.setColor(fg);
			}

			if (isinsel)
				g.setColor(bg);
			g.drawString(chs[b0], x, y);
			x += chw;
			g.drawString(chs[b1], x, y);
			x += (chw + 4);
			g.setColor(fg);

			if ((i + 1) % 16 == 0)
			{
				x += chw;
				if ((i + 1) % linesize == 0)
				{
					y += chh;
					x = xgap;
				}
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
		boolean isshift = ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK);
		switch (keycode)
		{
			case KeyEvent.VK_PAGE_DOWN:
			{
				onPage(1);
				break;
			}
			case KeyEvent.VK_PAGE_UP:
			{
				onPage(-1);
				break;
			}
			case KeyEvent.VK_UP:
			{
				onMoveCursor(0 - m_linesize, isshift);
				break;
			}
			case KeyEvent.VK_DOWN:
			{
				onMoveCursor(m_linesize, isshift);
				break;
			}
			case KeyEvent.VK_LEFT:
			{
				onMoveCursor(-1, isshift);
				break;
			}
			case KeyEvent.VK_RIGHT:
			{
				onMoveCursor(1, isshift);
				break;
			}
		}
	}
}
