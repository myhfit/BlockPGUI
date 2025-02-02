package bp.ui.table;

import java.util.List;

import javax.swing.Action;

import bp.ui.scomp.BPTable;

public abstract class BPTableFuncsBase<T> implements BPTableFuncs<T>
{
	protected String[] m_colnames;
	protected String[] m_collabels;
	protected Class<?>[] m_cols;

	public String[] getColumnNames()
	{
		return m_colnames;
	}

	public String[] getColumnLabels()
	{
		return m_collabels == null ? m_colnames : m_collabels;
	}

	public Class<?>[] getColumnClasses()
	{
		return m_cols;
	}

	protected final static String nvl(String str)
	{
		return str != null ? str : "";
	}

	public String getColumnName(int col)
	{
		return getColumnLabels()[col];
	}

	public Class<?> getColumnClass(int col)
	{
		return m_cols[col];
	}

	public boolean isEditable(T o, int row, int col)
	{
		return false;
	}

	public void setValue(Object v, T o, int row, int col)
	{

	}

	public List<Action> getActions(BPTable<T> table, List<T> datas, int[] rows, int r, int c)
	{
		return null;
	}
}
