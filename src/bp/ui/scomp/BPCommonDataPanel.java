package bp.ui.scomp;

import java.awt.BorderLayout;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import bp.ui.BPComponent;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditor.BPEditorEvent;
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
	protected WeakRefGo<BiConsumer<?, BPCommonDataPanel>> m_act0;
	protected WeakRefGo<BiConsumer<?, BPCommonDataPanel>> m_act1;

	protected JComponent m_comp;

	protected BPTreeActionEventHandler m_treecb;
	protected Consumer<BPEditorEvent> m_editorcb;

	protected Action[] m_acts;

	public BPCommonDataPanel()
	{
		m_treecb = this::onTreeEvent;
		m_editorcb = this::onEditorEvent;
		m_act0 = new WeakRefGo<BiConsumer<?, BPCommonDataPanel>>();
		m_act1 = new WeakRefGo<BiConsumer<?, BPCommonDataPanel>>();
		setBorder(null);
		setLayout(new BorderLayout());
	}

	public void setMode(int mode)
	{
		m_mode = mode;
	}

	public int getMode()
	{
		return m_mode;
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
		final Object data0 = data;
		clearActions();
		removeAll();
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
			if (comp instanceof BPEditor)
			{
				add(comp, BorderLayout.CENTER);
			}
			else
			{
				JScrollPane scroll = new JScrollPane();
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scroll.setViewportView(comp);
				scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
				add(scroll, BorderLayout.CENTER);
			}
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void onEditorEvent(BPEditorEvent e)
	{
		switch (e.action)
		{
			case BPEditorEvent.ACT_SELECT:
			case BPEditorEvent.ACT_OPEN:
			{
				final BPCommonDataPanel pthis = this;
				Object fobj = e.data;
				(BPEditorEvent.ACT_SELECT.equals(e.action) ? m_act0 : m_act1).run(seg -> ((BiConsumer) seg).accept(fobj, pthis));
				break;
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
		else if (comp instanceof BPEditor)
		{
			BPEditor<?> editor = (BPEditor<?>) comp;
			editor.getEditorEventController().installHandler(m_editorcb);
			Action[] acts = editor.getSeparatorActions();
			if (acts != null)
			{
				for (Action act : acts)
				{
					KeyStroke ks = (KeyStroke) act.getValue(Action.ACCELERATOR_KEY);
					if (ks != null)
						registerKeyboardAction(act, ks, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				}
				m_acts = acts;
			}
		}
	}

	protected void clearActions()
	{
		Action[] acts = m_acts;
		if (acts != null)
		{
			m_acts = null;
			for (Action act : acts)
			{
				KeyStroke ks = (KeyStroke) act.getValue(Action.ACCELERATOR_KEY);
				if (ks != null)
					unregisterKeyboardAction(ks);
			}
		}
	}

	public void clearResource()
	{
		clearActions();
		JComponent comp = m_comp;
		if (comp != null && comp instanceof BPComponent)
			((BPComponent<?>) comp).clearResource();
	}
}