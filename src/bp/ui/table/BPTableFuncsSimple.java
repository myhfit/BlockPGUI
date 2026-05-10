package bp.ui.table;

import java.util.List;

import javax.swing.Action;

import bp.ui.scomp.BPTable;
import bp.util.LogicUtil.WeakRefGo;

public class BPTableFuncsSimple<T> extends BPTableFuncsBase<T>
{
	protected WeakRefGo<BPTableFuncsValueGetter<T>> m_vgetterref;
	protected WeakRefGo<BPTableFuncsValueSetter<T>> m_vsetterref;
	protected WeakRefGo<BPTableFuncsEditableChecker<T>> m_editcheckerref;
	protected WeakRefGo<BPTableFuncsContextActionMaker<T>> m_camakerref;
	protected WeakRefGo<BPTableFuncsOpenActionHandler<T>> m_oahandlerref;
	protected boolean m_editable;

	public BPTableFuncsSimple()
	{
		m_vgetterref = new WeakRefGo<>();
		m_vsetterref = new WeakRefGo<>();
		m_editcheckerref = new WeakRefGo<>();
		m_camakerref = new WeakRefGo<>();
		m_oahandlerref = new WeakRefGo<>();
	}

	public void setup(String[] colnames, String[] collabels, Class<?>[] cols)
	{
		m_colnames = colnames;
		m_collabels = collabels;
		m_cols = cols;
	}

	public static interface BPTableFuncsValueGetter<T>
	{
		Object getValue(T o, int row, int col);
	}

	public static interface BPTableFuncsValueSetter<T>
	{
		void setValue(Object v, T o, int row, int col);
	}

	public static interface BPTableFuncsEditableChecker<T>
	{
		boolean check(T o, int row, int col);
	}

	public static interface BPTableFuncsContextActionMaker<T>
	{
		List<Action> getActions(BPTable<T> table, List<T> datas, int[] rows, int r, int c);
	}

	public static interface BPTableFuncsOpenActionHandler<T>
	{
		Action getOpenAction(BPTable<T> table, T data, int row, int col);
	}

	public void setValueGetter(BPTableFuncsValueGetter<T> getter)
	{
		m_vgetterref.setTarget(getter);
	}

	public void setValueSetter(BPTableFuncsValueSetter<T> setter)
	{
		m_vsetterref.setTarget(setter);
	}

	public void setEditableChecker(BPTableFuncsEditableChecker<T> checker)
	{
		m_editcheckerref.setTarget(checker);
	}

	public void setContextActionMaker(BPTableFuncsContextActionMaker<T> maker)
	{
		m_camakerref.setTarget(maker);
	}

	public void setOpenActionHandler(BPTableFuncsOpenActionHandler<T> handler)
	{
		m_oahandlerref.setTarget(handler);
	}

	public Object getValue(T o, int row, int col)
	{
		BPTableFuncsValueGetter<T> s = m_vgetterref.get();
		return s != null ? s.getValue(o, row, col) : null;
	}

	public void setValue(Object v, T o, int row, int col)
	{
		BPTableFuncsValueSetter<T> s = m_vsetterref.get();
		if (s != null)
			s.setValue(v, o, row, col);
	}

	public boolean isEditable(T o, int row, int col)
	{
		BPTableFuncsEditableChecker<T> s = m_editcheckerref.get();
		return s != null ? s.check(o, row, col) : m_editable;
	}

	public void setEditable(boolean flag)
	{
		m_editable = flag;
	}

	public List<Action> getActions(BPTable<T> table, List<T> datas, int[] rows, int r, int c)
	{
		BPTableFuncsContextActionMaker<T> s = m_camakerref.get();
		return s != null ? s.getActions(table, datas, rows, r, c) : null;
	}

	public Action getOpenAction(BPTable<T> table, T data, int row, int col)
	{
		BPTableFuncsOpenActionHandler<T> s = m_oahandlerref.get();
		return s != null ? s.getOpenAction(table, data, row, col) : null;
	}
}
