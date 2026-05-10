package bp.ui.scomp;

import java.awt.BorderLayout;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

import bp.ui.BPComponent;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncs.BPTreeActionEventHandler;
import bp.ui.tree.BPTreeFuncs.BPTreeActionType;
import bp.ui.tree.BPTreeFuncsAbstract;
import bp.ui.util.CommonDataUIProcs;
import bp.util.LogicUtil.WeakRefGo;

public class BPCommonDataPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1544754402372325176L;

	protected int m_mode;

	protected Object m_data;
	protected WeakRefGo<BiConsumer<?,BPCommonDataPanel>> m_act0;
	protected WeakRefGo<BiConsumer<?,BPCommonDataPanel>> m_act1;

	protected JScrollPane m_scroll;

	protected BPTreeActionEventHandler m_treecb;

	public BPCommonDataPanel()
	{
		m_treecb = this::onTreeEvent;
		m_act0 = new WeakRefGo<BiConsumer<?,BPCommonDataPanel>>();
		m_act1 = new WeakRefGo<BiConsumer<?,BPCommonDataPanel>>();
		m_scroll = new JScrollPane();
		m_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		m_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		setBorder(null);
		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);
	}

	public void setMode(int mode)
	{
		m_mode = mode;
	}

	public void setActions(BiConsumer<?,BPCommonDataPanel> selaction, BiConsumer<?,BPCommonDataPanel> enteraction)
	{
		m_act0.setTarget(selaction);
		m_act1.setTarget(enteraction);
	}

	public void setData(Object data)
	{
		m_data = data;
	}

	public Object getData()
	{
		return m_data;
	}

	public void initByData()
	{
		setupDataPanel(m_data);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setupDataPanel(Object data)
	{
		final JScrollPane scroll = m_scroll;
		final Object data0 = data;
		CommonDataUIProcs.useCreatePanel(m_mode, (p0, p1) ->
		{
			JComponent comp;
			Object r = ((Function<Object, JComponent>) p0).apply(data0);
			if (r instanceof BPComponent)
			{
				comp = (JComponent) ((BPComponent<?>) r).getComponent();
			}
			else
			{
				comp = (JComponent) r;
			}
			scroll.setViewportView(comp);
			if (p1 != null)
				((BiConsumer) p1).accept(r, data0);
			tryAddListener(comp);
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void onTreeEvent(BPTreeActionType actiontype, BPTree tree, BPTreeNode node)
	{
		Object obj = node.getUserObject();
		if (node.getUserObject() == m_data || node.isVirtual())
			obj = null;
		switch (actiontype)
		{
			case SELECT:
			case OPEN:
			{
				final BPCommonDataPanel pthis = this;
				Object fobj = obj;
				(actiontype == BPTreeActionType.SELECT ? m_act0 : m_act1).run(seg -> ((BiConsumer) seg).accept(fobj, pthis));
				break;
			}
			default:
			{

			}
		}
	}

	protected void tryAddListener(JComponent comp)
	{
		if (m_mode == CommonDataUIProcs.MODE_OBJTREE)
		{
			BPTreeComponentBase tree = (BPTreeComponentBase) comp;
			((BPTreeFuncsAbstract) tree.getTreeFuncs()).installTreeActionHandler(m_treecb);
		}
		else if (m_mode == CommonDataUIProcs.MODE_OBJLIST)
		{
			BPTreeComponentBase tree = (BPTreeComponentBase) comp;
			((BPTreeFuncsAbstract) tree.getTreeFuncs()).installTreeActionHandler(m_treecb);
		}
	}

	public void clearResource()
	{
		JViewport v = m_scroll.getViewport();
		if (v != null)
		{
			JComponent comp = (JComponent) m_scroll.getViewport().getView();
			if (comp != null && comp instanceof BPComponent)
				((BPComponent<?>) comp).clearResource();
		}
	}
}