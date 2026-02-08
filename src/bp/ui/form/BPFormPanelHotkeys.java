package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import bp.config.Hotkeys.Hotkey;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPInputHotkey;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPTextField;
import bp.ui.table.BPTableFuncsBase;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPFormPanelHotkeys extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3559822840080814271L;

	protected BPTable<Hotkey> m_tabhks;
	protected BPTableFuncsHotkey m_tabfuncs;
	protected BPToolBarSQ m_tb;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		if (m_tabhks.isEditing())
			m_tabhks.getCellEditor().stopCellEditing();
		List<Hotkey> datas = m_tabhks.getBPTableModel().getDatas();
		Map<String, Object> rc = createMap();
		for (Hotkey hk : datas)
			rc.put(hk.getMapKey(), hk.target);
		return rc;
	}

	protected Map<String, Object> createMap()
	{
		return new LinkedHashMap<String, Object>();
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		List<Hotkey> hks = new ArrayList<Hotkey>();
		for (String key : data.keySet())
		{
			Hotkey hk = new Hotkey(key, (String) data.get(key));
			hks.add(hk);
		}
		m_tabhks.getBPTableModel().setDatas(hks);
		m_tabhks.refreshData();
		if (!editable)
		{
			BPTextField tf = new BPTextField();
			tf.setMonoFont();
			TableCellEditor editor = new BPTable.BPCellEditorReadonly(tf);
			m_tabhks.getColumnModel().getColumn(0).setCellEditor(editor);
			m_tabhks.getColumnModel().getColumn(1).setCellEditor(editor);
			m_tabhks.getColumnModel().getColumn(2).setCellEditor(editor);
		}
		else
		{
			m_tabhks.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new BPInputHotkey()));
		}
		m_tb.setVisible(editable);
	}

	protected void onAdd(ActionEvent e)
	{
		m_tabhks.getBPTableModel().getDatas().add(new Hotkey(null, null));
		m_tabhks.getBPTableModel().fireTableDataChanged();
	}

	protected void onDel(ActionEvent e)
	{
		List<Hotkey> datas = m_tabhks.getBPTableModel().getDatas();
		datas.remove(m_tabhks.convertRowIndexToModel(m_tabhks.getSelectedRow()));
		m_tabhks.getBPTableModel().fireTableDataChanged();
	}

	protected void initForm()
	{
		m_tabhks = new BPTable<>();
		BPTableFuncsHotkey funcs = new BPTableFuncsHotkey();
		m_tabhks.setModel(new BPTableModel<Hotkey>(funcs));
		m_tabhks.initRowSorter();
		m_tabfuncs = funcs;
		JScrollPane scroll = new JScrollPane(m_tabhks);
		JPanel pnl = new JPanel();
		BPToolBarSQ tb = new BPToolBarSQ(true);
		tb.setActions(makeToolBarActions().toArray(new Action[0]));
		tb.setBorderVertical(0);
		m_tb = tb;

		m_tabhks.setAutoResizeMode(BPTable.AUTO_RESIZE_OFF);
		m_tabhks.setMonoFont();
		m_tabhks.getColumnModel().getColumn(0).setPreferredWidth(UIUtil.scale(40));
		m_tabhks.getColumnModel().getColumn(1).setPreferredWidth(UIUtil.scale(110));
		m_tabhks.getColumnModel().getColumn(2).setPreferredWidth(UIUtil.scale(250));
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

	protected static class BPTableFuncsHotkey extends BPTableFuncsBase<Hotkey>
	{
		public BPTableFuncsHotkey()
		{
			m_cols = new Class[] { Boolean.class, String.class, String.class };
			m_colnames = new String[] { "Sys", "Key", "Target" };
		}

		public Object getValue(Hotkey o, int row, int col)
		{
			switch (col)
			{
				case 0:
					return o.issystem;
				case 1:
					return nvl(o.key);
				case 2:
					return nvl(o.target);
			}
			return "";
		}

		public void setValue(Object v, Hotkey hk, int row, int col)
		{
			switch (col)
			{
				case 0:
					hk.issystem = ObjUtil.toBool(v, false);
					break;
				case 1:
					hk.key = (String) v;
					break;
				case 2:
					hk.target = (String) v;
					break;
			}
		}

		public boolean isEditable(Hotkey o, int row, int col)
		{
			return true;
		}
	}

	protected static class BPTableCellEditorHotkey extends DefaultCellEditor
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6339096170907612537L;

		public BPTableCellEditorHotkey(JTextField textfield)
		{
			super(textfield);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			JTextField comp = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			comp.setEditable(false);
			return comp;
		}
	}
}
