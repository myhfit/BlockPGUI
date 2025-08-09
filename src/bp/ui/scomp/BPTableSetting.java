package bp.ui.scomp;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import bp.config.BPSetting;
import bp.config.BPSettingItem;
import bp.res.BPResource;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.table.BPTableFuncsBase;
import bp.ui.util.CommonUIOperations;

public class BPTableSetting extends BPTable<BPSettingItem>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7030538427166413294L;

	public BPTableSetting()
	{
		super(new BPTableFuncsSettingItem());

		getColumnModel().getColumn(1).setCellEditor(new BPCellEditorSettingItem());
		putClientProperty("terminateEditOnFocusLost", true);
	}

	public void setSetting(BPSetting setting)
	{
		List<BPSettingItem> items = new ArrayList<BPSettingItem>();
		((BPTableFuncsSettingItem) m_tablefuncs).setSetting(setting);
		if (setting != null)
		{
			BPSettingItem[] ritems = setting.getItems();
			for (BPSettingItem ritem : ritems)
			{
				items.add(ritem);
			}
		}
		getBPTableModel().setDatas(items);
		refreshData();
	}

	protected final static class BPCellEditorSettingItem extends AbstractCellEditor implements TableCellEditor
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5241729134322687090L;

		protected BPComboBox<String> m_cb;
		protected BPTextField m_txt;
		protected BPButton m_btn;
		protected boolean m_isressave;
		protected int m_sb;

		public BPCellEditorSettingItem()
		{
			m_cb = new BPComboBox<String>();
			m_cb.setMonoFont();
			m_txt = new BPTextField();
			m_txt.setMonoFont();
			m_btn = new BPButton();
			m_btn.setMonoFont();
			m_btn.setBorder(null);
			m_btn.setFocusable(false);
			m_btn.addActionListener(this::onShowSelectResource);
		}

		protected void onShowSelectResource(ActionEvent e)
		{
			BPResource res = CommonUIOperations.selectResource((Window) m_btn.getTopLevelAncestor(), m_isressave);
			if (res != null)
			{
				m_btn.setText(res.getName());
				m_btn.setUserObject(res);
			}
		}

		public Object getCellEditorValue()
		{
			switch (m_sb)
			{
				case 0:
					return m_txt.getText();
				case 1:
					return m_cb.getSelectedItem();
				case 2:
					return m_btn.getUserObject();
			}
			return null;
		}

		public boolean isCellEditable(EventObject e)
		{
			return true;
		}

		@SuppressWarnings("unchecked")
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			BPSettingItem item = ((BPTable<BPSettingItem>) table).getBPTableModel().getRow(row);
			if (BPSettingItem.ITEM_TYPE_SELECT.equals(item.itemtype))
			{
				m_sb = 1;
				BPComboBoxModel<String> model = new BPComboBoxModel<String>();
				model.setDatas(Arrays.asList(item.candidates));
				m_cb.setModel(model);
				m_cb.setSelectedItem(value);
				return m_cb;
			}
			else if (BPSettingItem.ITEM_TYPE_RESOURCE.equals(item.itemtype) || BPSettingItem.ITEM_TYPE_RESOURCE_SAVE.equals(item.itemtype))
			{
				m_isressave = BPSettingItem.ITEM_TYPE_RESOURCE_SAVE.equals(item.itemtype);
				m_sb = 2;
				BPButton btn = m_btn;
				if (value != null && value instanceof BPResource)
					btn.setText(((BPResource) value).getName());
				else
					btn.setText("Select...");
				return btn;
			}
			else
			{
				m_txt.setText(value == null ? "" : value.toString());
			}
			m_sb = 0;
			return m_txt;
		}
	}

	protected final static class BPTableFuncsSettingItem extends BPTableFuncsBase<BPSettingItem>
	{
		protected BPSetting m_setting;

		public BPTableFuncsSettingItem()
		{
			m_colnames = new String[] { "Name", "Value" };
			m_cols = new Class<?>[] { String.class, String.class };
		}

		public void setSetting(BPSetting setting)
		{
			m_setting = setting;
		}

		public Object getValue(BPSettingItem item, int row, int col)
		{
			if (m_setting == null)
				return "";
			switch (col)
			{
				case 0:
					return item.name;
				case 1:
				{
					Object v = m_setting.get(item.key);
					return v == null ? "" : v;
				}
			}
			return null;
		}

		public void setValue(Object v, BPSettingItem item, int row, int col)
		{
			m_setting.set(item.key, v);
		}

		public boolean isEditable(BPSettingItem o, int row, int col)
		{
			return col == 1;
		}
	}
}
