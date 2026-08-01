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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bp.ui.BPComponent;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditor.BPEditorEvent;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncs.BPTreeActionEventHandler;
import bp.ui.tree.BPTreeFuncs.BPTreeActionType;
import bp.ui.tree.BPTreeFuncsAbstract;
import bp.ui.util.CommonDataUIProcs;
import bp.util.LogicUtil.WeakRefGoBiConsumer;

public class BPCommonDataPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1544754402372325176L;

	protected volatile int m_mode;
	protected volatile int m_lastmode;

	protected Object m_data;
	protected WeakRefGoBiConsumer<?, BPCommonDataPanel> m_act0;
	protected WeakRefGoBiConsumer<?, BPCommonDataPanel> m_act1;

	protected JComponent m_comp;
	protected JComponent m_cc;

	protected BPTreeActionEventHandler m_treecb;
	protected ListSelectionListener m_listcb;
	protected Consumer<BPEditorEvent> m_editorcb;

	protected Action[] m_acts;

	public BPCommonDataPanel()
	{
		m_lastmode = -1;
		m_treecb = this::onTreeEvent;
		m_listcb = this::onListSelectionChanged;
		m_editorcb = this::onEditorEvent;
		m_act0 = new WeakRefGoBiConsumer<>();
		m_act1 = new WeakRefGoBiConsumer<>();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setActions(BiConsumer<?, BPCommonDataPanel> selaction, BiConsumer<?, BPCommonDataPanel> enteraction)
	{
		((WeakRefGoBiConsumer) m_act0).setTarget(selaction);
		((WeakRefGoBiConsumer) m_act1).setTarget(enteraction);
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
		initByData(false);
	}

	public void initByData(boolean noforcecreate)
	{
		setupDataPanel(m_data, noforcecreate);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setupDataPanel(Object data, boolean noforcecreate)
	{
		final Object data0 = data;
		clearActions();
		if (!noforcecreate || m_lastmode != m_mode)
		{
			CommonDataUIProcs.useCreatePanel(m_mode, (p0, p1) ->
			{
				if (m_comp != null)
				{
					remove(m_cc);
					m_comp = null;
					m_cc = null;
				}
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
					m_cc = comp;
				}
				else
				{
					JScrollPane scroll = new JScrollPane();
					scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
					scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					scroll.setViewportView(comp);
					scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
					add(scroll, BorderLayout.CENTER);
					m_cc = scroll;
				}
				if (p1 != null)
					((BiConsumer) p1).accept(r, data0);
				tryAddListener(comp);
				m_comp = comp;
				m_lastmode = m_mode;
			});
		}
		else
		{
			CommonDataUIProcs.useInitPanel(m_mode, p1 ->
			{
				if (p1 != null)
					((BiConsumer) p1).accept(m_comp, data0);
			});
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void onTreeEvent(BPTreeActionType actiontype, BPTree tree, BPTreeNode node, String extact)
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
				((WeakRefGoBiConsumer) (actiontype == BPTreeActionType.SELECT ? m_act0 : m_act1)).accept(fobj, pthis);
				break;
			}
			default:
			{

			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void onListSelectionChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		final BPCommonDataPanel pthis = this;
		BPList<Object> list = (BPList<Object>) m_comp;
		Object fobj = list.getSelectedValue();
		((WeakRefGoBiConsumer) m_act0).accept(fobj, pthis);
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
				((WeakRefGoBiConsumer) (BPEditorEvent.ACT_SELECT.equals(e.action) ? m_act0 : m_act1)).accept(fobj, pthis);
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void tryAddListener(JComponent comp)
	{
		if (m_mode == CommonDataUIProcs.MODE_OBJTREE)
		{
			BPTreeComponentBase tree = (BPTreeComponentBase) comp;
			((BPTreeFuncsAbstract) tree.getTreeFuncs()).installTreeActionHandler(m_treecb);
		}
		else if (m_mode == CommonDataUIProcs.MODE_OBJLIST)
		{
			BPList<Object> list=(BPList<Object>) comp;
			list.addListSelectionListener(m_listcb);
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
		m_comp = null;
		m_cc = null;
	}

	public void requestEditorFocus()
	{
		m_comp.requestFocusInWindow();
	}
}