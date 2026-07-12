package bp.ui.scomp;

import javax.swing.UIDefaults;

import bp.locale.BPLocaleConstCC;
import bp.typeext.KV;
import bp.ui.table.BPTableFuncsBase;

public class BPKVTable extends BPTable<KV>
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

	public static class BPKVTableFuncs extends BPTableFuncsBase<KV>
	{
		public BPKVTableFuncs()
		{
			m_colnames = new String[] { "Key", "Value" };
			m_colnames = new String[] { BPLocaleConstCC.KEY.text(), BPLocaleConstCC.VALUE.text() };
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

			public boolean isEditable(KV o, int row, int col)
			{
				return m_colen[col];
			}
		}
	}
}
