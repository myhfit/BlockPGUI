package bp.ui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.BPCore;
import bp.project.BPResourceProject;
import bp.ui.BPComponent;
import bp.ui.util.UIUtil;

public class BPProjectsOverviewPanel extends JPanel implements BPComponent<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1799481858603693317L;

	protected JPanel m_pnlprjs;
	protected JScrollPane m_scroll;
	protected ComponentListener m_compl;

	protected int m_cellw = 200;
	protected int m_cellh = 120;

	protected int m_count = 0;

	public BPProjectsOverviewPanel()
	{
		initUI();
		initEvents();
		initDatas();
	}

	protected void initUI()
	{
		removeAll();
		if (m_compl != null)
			removeComponentListener(m_compl);

		m_pnlprjs = new JPanel();
		m_pnlprjs.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		m_scroll = new JScrollPane();
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		m_scroll.setViewportView(m_pnlprjs);

		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);

		m_compl = new UIUtil.BPComponentListener(this::onResize, null, null, null);
		addComponentListener(m_compl);
	}

	protected void initEvents()
	{
	}

	protected void initDatas()
	{
		m_pnlprjs.removeAll();
		BPResourceProject[] prjs = BPCore.getProjectsContext().listProject();
		int prjc = 0;
		if (prjs != null)
		{
			for (BPResourceProject prj : prjs)
			{
				BPProjectBlockViewComp comp = new BPProjectBlockViewComp();
				comp.setPreferredSize(new Dimension(m_cellw, m_cellh));
				comp.initData(prj);
				m_pnlprjs.add(comp);
			}
			prjc = prjs.length;
		}
		m_count = prjc;
		updateUI();
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	private void onResize(ComponentEvent e)
	{
		reLayout();
	}

	public boolean isRoutable()
	{
		return true;
	}

	protected void reLayout()
	{
		int w = getWidth();
		int h = getHeight();
		int cw = m_cellw;
		int ch = m_cellh;
		int wc = w / cw;
		if (wc <= 0)
			wc = 1;
		int hc = h / ch;
		if (m_count > (wc * hc))
		{
			wc = (w - 20) / cw;
			if (wc <= 0)
				wc = 1;
			hc = (int) Math.ceil((double) m_count / (double) wc);
			m_pnlprjs.setPreferredSize(new Dimension(cw * wc, ch * hc));
		}
		else
		{
			m_pnlprjs.setPreferredSize(new Dimension(cw * wc, ch * hc));
		}
	}
}
