package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.ui.util.UIUtil;

public class BPContentsPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8374311926583767227L;

	protected JPanel m_pnl;
	protected JScrollPane m_scroll;
	protected ComponentListener m_compl;

	protected int m_cellw = 200;
	protected int m_cellh = 120;
	protected LayoutMode m_layoutmode = LayoutMode.GRID;

	public BPContentsPanel()
	{
		initUI();
		initEvents();
	}

	protected void initUI()
	{
		removeAll();
		if (m_compl != null)
			removeComponentListener(m_compl);

		m_pnl = new JPanel();
		m_pnl.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		m_scroll = new JScrollPane();
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		m_scroll.setViewportView(m_pnl);

		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);

		m_compl = new UIUtil.BPComponentListener(this::onResize, null, null, null);
		addComponentListener(m_compl);
	}

	protected void initEvents()
	{
	}

	public Container getRealContainer()
	{
		return m_pnl;
	}

	private void onResize(ComponentEvent e)
	{
		if (m_layoutmode == LayoutMode.GRID)
			reLayoutGrid();
	}

	public void setContentLayoutMode(LayoutMode mode)
	{
		if (m_layoutmode == mode)
			return;
		m_layoutmode = mode;
		switch (m_layoutmode)
		{
			case GRID:
			{
				m_pnl.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
				reLayoutGrid();
				break;
			}
			case HORIZONTAL:
			{
				m_pnl.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				break;
			}
			case VERTICAL:
			{
				m_pnl.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				break;
			}
		}
	}

	public boolean isRoutable()
	{
		return true;
	}

	protected void reLayoutGrid()
	{
		int w = getWidth();
		int h = getHeight();
		int cw = m_cellw;
		int ch = m_cellh;
		int wc = w / cw;
		if (wc <= 0)
			wc = 1;
		int hc = h / ch;
		int count = m_pnl.getComponentCount();
		if (count > (wc * hc))
		{
			wc = (w - 20) / cw;
			if (wc <= 0)
				wc = 1;
			hc = (int) Math.ceil((double) count / (double) wc);
			m_pnl.setPreferredSize(new Dimension(cw * wc, ch * hc));
		}
		else
		{
			m_pnl.setPreferredSize(new Dimension(cw * wc, ch * hc));
		}
	}

	public static enum LayoutMode
	{
		VERTICAL, HORIZONTAL, GRID;
	}
}
