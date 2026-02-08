package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPKVTable;
import bp.ui.scomp.BPKVTable.BPKVTableFuncs.BPKVTableFuncsEditable;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPTextField;

public class BPFormPanelMap extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6771891840832623340L;

	protected BPKVTable m_tabkvs;
	protected BPKVTableFuncsEditable m_tabfuncs;
	protected BPToolBarSQ m_tb;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		if (m_tabkvs.isEditing())
			m_tabkvs.getCellEditor().stopCellEditing();
		List<KV> datas = m_tabkvs.getBPTableModel().getDatas();
		Map<String, Object> rc = createMap();
		for (KV kv : datas)
		{
			rc.put(kv.key, kv.value);
		}
		return rc;
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String, Object>();
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		m_tabkvs.editingCanceled(null);
		List<KV> kvs = new ArrayList<KV>();
		for (String key : data.keySet())
		{
			KV kv = new KV();
			kv.key = key;
			kv.value = data.get(key);
			kvs.add(kv);
		}
		m_tabkvs.getBPTableModel().setDatas(kvs);
		m_tabkvs.refreshData();
		if (!editable)
		{
			BPTextField tf = new BPTextField();
			tf.setMonoFont();
			TableCellEditor editor = new BPTable.BPCellEditorReadonly(tf);
			m_tabkvs.getColumnModel().getColumn(0).setCellEditor(editor);
			m_tabkvs.getColumnModel().getColumn(1).setCellEditor(editor);
		}
		m_tb.setVisible(editable);
	}

	protected void onAdd(ActionEvent e)
	{
		m_tabkvs.getBPTableModel().getDatas().add(new KV());
		m_tabkvs.getBPTableModel().fireTableDataChanged();
	}

	protected void onDel(ActionEvent e)
	{
		List<KV> datas = m_tabkvs.getBPTableModel().getDatas();
		datas.remove(m_tabkvs.convertRowIndexToModel(m_tabkvs.getSelectedRow()));
		m_tabkvs.getBPTableModel().fireTableDataChanged();
	}

	protected void initForm()
	{
		m_tabkvs = new BPKVTable();
		BPKVTableFuncsEditable funcs = new BPKVTable.BPKVTableFuncs.BPKVTableFuncsEditable();
		m_tabkvs.setModel(new BPTableModel<BPKVTable.KV>(funcs));
		m_tabkvs.getBPTableModel().setDatas(new ArrayList<KV>());
		m_tabkvs.initRowSorter();
		m_tabfuncs = funcs;
		JScrollPane scroll = new JScrollPane(m_tabkvs);
		JPanel pnl = new JPanel();
		BPToolBarSQ tb = new BPToolBarSQ(true);
		tb.setActions(makeToolBarActions().toArray(new Action[0]));
		tb.setBorderVertical(0);
		m_tb = tb;

		m_tabkvs.setMonoFont();
		m_tabkvs.getColumnModel().getColumn(0).setPreferredWidth(100);
		m_tabkvs.getColumnModel().getColumn(1).setPreferredWidth(300);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		pnl.add(tb, BorderLayout.WEST);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}

	protected List<Action> makeToolBarActions()
	{
		List<Action> rc = new ArrayList<Action>();
		BPAction actadd = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNADD, this::onAdd);
		BPAction actdel = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, this::onDel);
		rc.add(BPAction.separator());
		rc.add(actadd);
		rc.add(actdel);
		return rc;
	}
}
