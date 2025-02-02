package bp.ui.scomp;

import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Function;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.KeyStroke;
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
		finddlgref.run(dlg -> dlg.dispose());
		finddlgref.setTarget(null);

		BPDialogFind dlg = new BPDialogFind((Window) this.getFocusCycleRootAncestor());
		dlg.setFindCallBack(m_findcb);
		finddlgref.setTarget(dlg);
		dlg.setVisible(true);
	}

	protected boolean onFindCall(BPFindPs ps)
	{
		return find(ps.src, ps.isforward, ps.iswholeword, ps.iscasesensitive, ps.onlyselection);
	}

	public boolean find(String target, boolean isforward, boolean wholeword, boolean casesensitive, boolean onlysel)
	{
		int si = getSelectedIndex();
		int delta = isforward ? 1 : -1;
		int i = si + delta;
		int c = getModel().getSize();
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
			String t = ObjUtil.toString(model.getElementAt(i));
			// int c2 = model.getColumnCount();
			// int r = convertRowIndexToModel(i);
			// for (int j = 0; j < c2; j++)
			// {
			// String t = ObjUtil.toString(model.getValueAt(r, j));
			if (TextUtil.containsText(t, target, wholeword, !casesensitive))
			{
				ListSelectionModel selmodel = getSelectionModel();
				selmodel.clearSelection();
				selmodel.setSelectionInterval(i, i);
				scrollTo(i);
				return true;
			}
			// }
			techc++;
		}
		return false;
	}

	public void scrollTo(int row)
	{
		Rectangle rect = getCellBounds(row, row + 1);
		scrollRectToVisible(rect);
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

	@SuppressWarnings("serial")
	public static class BPListRenderer extends DefaultListCellRenderer
	{
		protected Function<Object, ?> m_transfunc;

		public BPListRenderer(Function<Object, ?> transfunc)
		{
			m_transfunc = transfunc;
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			return super.getListCellRendererComponent(list, m_transfunc == null ? value : m_transfunc.apply(value), index, isSelected, cellHasFocus);
		}
	}
}
