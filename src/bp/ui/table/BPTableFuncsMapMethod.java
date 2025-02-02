package bp.ui.table;

import java.util.Map;
import java.util.function.Function;

public class BPTableFuncsMapMethod extends BPTableFuncsMap
{
	protected Function<Map<String, Object>, Object>[] m_cbs;

	@SuppressWarnings("unchecked")
	public void setColumnMethod(int col, Function<Map<String, Object>, Object> callback)
	{
		if (m_cbs == null)
			m_cbs = new Function[m_cols.length];
		m_cbs[col] = callback;
	}

	public Object getValue(Map<String, Object> o, int row, int col)
	{
		Function<Map<String, Object>, Object>[] cbs = m_cbs;
		if (cbs != null)
		{
			Function<Map<String, Object>, Object> cb = cbs[col];
			if (cb != null)
				return cb.apply(o);
		}
		return o.get(m_colnames[col]);
	}
}
