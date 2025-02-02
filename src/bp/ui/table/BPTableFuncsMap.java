package bp.ui.table;

import java.util.Map;

public class BPTableFuncsMap extends BPTableFuncsBase<Map<String, Object>>
{
	protected boolean m_editable;

	public BPTableFuncsMap()
	{
		m_editable = true;
	}

	public void setColumns(String[] cols, String[] collabels)
	{
		int c = cols.length;
		m_colnames = cols;
		m_collabels = collabels;
		m_cols = new Class<?>[c];
		for (int i = 0; i < c; i++)
			m_cols[i] = Object.class;
	}

	public Object getValue(Map<String, Object> o, int row, int col)
	{
		return o.get(m_colnames[col]);
	}

	public boolean isEditable(Map<String, Object> o, int row, int col)
	{
		return m_editable;
	}

	public void setEditable(boolean flag)
	{
		m_editable = flag;
	}
}
