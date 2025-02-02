package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.ui.scomp.BPKVTable;
import bp.ui.scomp.BPKVTable.BPKVTableFuncs.BPKVTableFuncsEditable;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.scomp.BPTable.BPTableModel;

public class BPFormPanelSetting extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6004775172036361788L;
	
	protected BPKVTable m_tabkvs;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		List<KV> datas = m_tabkvs.getBPTableModel().getDatas();
		Map<String, Object> rc = new HashMap<String, Object>();
		for (KV kv : datas)
		{
			rc.put(kv.key, kv.value);
		}
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		List<KV> kvs = new ArrayList<KV>();
		for (Entry<String, ?> entry : data.entrySet())
		{
			KV kv = new KV();
			kv.key = entry.getKey();
			kv.value = entry.getValue();
			kvs.add(kv);
		}
		m_tabkvs.getBPTableModel().setDatas(kvs);
		m_tabkvs.refreshData();
	}

	protected void initForm()
	{
		m_tabkvs = new BPKVTable();
		BPKVTableFuncsEditable funcs = new BPKVTable.BPKVTableFuncs.BPKVTableFuncsEditable();
		m_tabkvs.setModel(new BPTableModel<BPKVTable.KV>(funcs));
		JScrollPane scroll = new JScrollPane(m_tabkvs);
		JPanel pnl = new JPanel();

		m_tabkvs.setMonoFont();
		m_tabkvs.getColumnModel().getColumn(0).setPreferredWidth(100);
		m_tabkvs.getColumnModel().getColumn(1).setPreferredWidth(300);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}
}
