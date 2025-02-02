package bp.ui.scomp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.UIDefaults;

import bp.ui.table.BPTableFuncsBase;

public class BPKVTable extends BPTable<BPKVTable.KV>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6112064639193352011L;

	public BPKVTable()
	{
		super(new BPKVTableFuncs());
	}

	@SuppressWarnings("unchecked")
	protected void createDefaultRenderers()
	{
		super.createDefaultRenderers();
		defaultRenderersByColumnClass.put(Object.class, (UIDefaults.LazyValue) t -> new BPTableRendererCommonObj());
	}

	public static class KV
	{
		public String key;
		public Object value;

		public final static List<KV> getKVs(Map<String, Object> data)
		{
			List<KV> rc = new ArrayList<KV>();
			for (Entry<String, Object> entry : data.entrySet())
			{
				KV kv = new KV();
				kv.key = entry.getKey();
				kv.value = entry.getValue();
				rc.add(kv);
			}
			return rc;
		}
	}

	public static class BPKVTableFuncs extends BPTableFuncsBase<BPKVTable.KV>
	{
		public BPKVTableFuncs()
		{
			m_colnames = new String[] { "Key", "Value" };
			m_cols = new Class<?>[] { String.class, Object.class };
		}

		public Object getValue(KV kv, int row, int col)
		{
			if (col == 0)
			{
				return kv.key;
			}
			else if (col == 1)
			{
				return kv.value;
			}
			return null;
		}

		public void setValue(Object v, KV kv, int row, int col)
		{
			if (col == 1)
				kv.value = v;
			else if (col == 0)
				kv.key = (String) v;
		}

		public static class BPKVTableFuncsEditable extends BPKVTableFuncs
		{
			protected boolean[] m_colen = new boolean[] { true, true };

			public BPKVTableFuncsEditable()
			{

			}

			public void setColumnEditable(int c, boolean flag)
			{
				m_colen[c] = flag;
			}

			public boolean isEditable(BPKVTable.KV o, int row, int col)
			{
				return m_colen[col];
			}
		}
	}
}
