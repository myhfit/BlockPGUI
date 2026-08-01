package bp.ui.scomp;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Function;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogFind;
import bp.ui.dialog.BPDialogFind.BPFindPs;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPList<T> extends JList<T>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3195380305008294991L;

	protected WeakRefGo<BPDialogFind> m_finddlgref;
	protected Function<BPFindPs, Boolean> m_findcb;

	public BPList()
	{
		m_finddlgref = new WeakRefGo<BPDialogFind>();
		m_findcb = this::onFindCall;
		initListener();
	}

	protected void initListener()
	{
		setupFindDlg();
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.LISTFONT_SIZE()));
	}

	public void setListFont()
	{
		int fontsize = UIConfigs.LISTFONT_SIZE();
		Font tfont = new Font(UIConfigs.LIST_FONT_NAME(), Font.PLAIN, fontsize);
		setFont(tfont);
	}

	public void setupFindDlg()
	{
		getInputMap().put(KeyStroke.getKeyStroke("control F"), "find");
		getActionMap().put("find", BPAction.build("find").callback(this::onFind).getAction());
	}

	public BPListModel<T> getBPModel()
	{
		return (BPListModel<T>) getModel();
	}

	public void onFind(ActionEvent e)
	{
		WeakRefGo<BPDialogFind> finddlgref = m_finddlgref;
		BPDialogFind dlg = finddlgref.get();
		if (dlg != null)
			dlg.dispose();
		finddlgref.setTarget(null);

		dlg = new BPDialogFind(this);
		dlg.setReplaceable(false);
		dlg.setFindCallBack(m_findcb);
		finddlgref.setTarget(dlg);
		dlg.setVisible(true);
	}

	protected boolean onFindCall(BPFindPs ps)
	{
		if (!ps.isReplace())
			return find(ps.src, ps.isforward, ps.iswholeword, ps.iscasesensitive, ps.onlyselection);
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean find(String target, boolean isforward, boolean wholeword, boolean casesensitive, boolean onlysel)
	{
		int si = getSelectedIndex();
		int delta = isforward ? 1 : -1;
		int i = si + delta;
		int c = getModel().getSize();
		Function<?, ?> br = getRendererTransFunction();
		if (isforward)
		{
			if (i >= c)
				i = 0;
			if (i < 0)
				i = 0;
		}
		else
		{
			if (i < 0)
				i = c - 1;
			if (i < 0)
				i = 0;
		}
		if (i >= c)
			return false;
		BPListModel<T> model = getBPModel();
		int techc = 0;
		for (; i != si; i += delta)
		{
			if (techc >= c)
				break;
			if (isforward)
			{
				if (i >= c)
					i = 0;
				if (i < 0)
					i = 0;
			}
			else
			{
				if (i < 0)
					i = c - 1;
				if (i < 0)
					i = 0;
			}

			String t = null;
			{
				Object ele = model.getElementAt(i);
				if (br != null)
					ele = ((Function)br).apply(ele);
				t = ObjUtil.toString(ele);
			}

			if (TextUtil.containsText(t, target, wholeword, !casesensitive))
			{
				ListSelectionModel selmodel = getSelectionModel();
				selmodel.clearSelection();
				selmodel.setSelectionInterval(i, i);
				ensureIndexIsVisible(i);
				return true;
			}
			techc++;
		}
		return false;
	}

	protected Function<?, ?> getRendererTransFunction()
	{
		ListCellRenderer<? super T> r = getCellRenderer();
		if (r != null && r instanceof BPListRendererIFC)
		{
			BPListRendererIFC br = (BPListRendererIFC) r;
			return br.getTransFunction();
		}
		return null;
	}

	public static class BPListModel<T> extends AbstractListModel<T>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -297240821004165961L;

		protected List<T> m_datas;

		public BPListModel()
		{

		}

		public void setDatas(List<T> datas)
		{
			int s = 0;
			if (m_datas != null)
				s = m_datas.size();
			m_datas = datas;
			if (s > 0)
				fireIntervalRemoved(this, 0, s - 1);
			if (m_datas.size() > 0)
				fireIntervalAdded(this, 0, m_datas.size() - 1);
		}

		public List<T> getDatas()
		{
			return m_datas;
		}

		public int getSize()
		{
			return m_datas == null ? 0 : m_datas.size();
		}

		public T getElementAt(int index)
		{
			return m_datas == null ? null : m_datas.get(index);
		}
	}

	public static interface BPListRendererIFC
	{
		Function<?, ?> getTransFunction();
	}

	@SuppressWarnings("serial")
	public static class BPListRendererT<T> extends DefaultListCellRenderer implements BPListRendererIFC
	{
		protected Function<? super T, ?> m_transfunc;

		public BPListRendererT(Function<? super T, ?> transfunc)
		{
			m_transfunc = transfunc;
		}

		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			return super.getListCellRendererComponent(list, m_transfunc == null ? value : m_transfunc.apply((T) value), index, isSelected, cellHasFocus);
		}

		public Function<? super T, ?> getTransFunction()
		{
			return m_transfunc;
		}
	}

	@SuppressWarnings("serial")
	public static class BPListRendererWeakRef extends DefaultListCellRenderer implements BPListRendererIFC
	{
		protected WeakReference<Function<?, ?>> m_transfuncref;

		public BPListRendererWeakRef(Function<?, ?> transfunc)
		{
			m_transfuncref = new WeakReference<>(transfunc);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Function<?, ?> tf = m_transfuncref == null ? null : m_transfuncref.get();
			return super.getListCellRendererComponent(list, tf == null ? value : ((Function)tf).apply(value), index, isSelected, cellHasFocus);
		}

		public Function<?, ?> getTransFunction()
		{
			return m_transfuncref.get();
		}
	}
}
