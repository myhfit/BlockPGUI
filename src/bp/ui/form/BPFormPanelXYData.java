package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;

import bp.data.BPXData;
import bp.data.BPXYData;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.actions.BPDataActionFactory.BPDataActionFactoryCommon;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsXY;
import bp.util.ObjUtil;

public class BPFormPanelXYData extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1662556250986914685L;

	protected BPTableFuncsXY m_funcs;
	protected BPTableModel<BPXData> m_model;
	protected BPTable<BPXData> m_table;
	protected BPToolBarSQ m_tb;

	public Map<String, Object> getFormData()
	{
		if (m_table.isEditing())
			m_table.getCellEditor().stopCellEditing();
		return ObjUtil.makeMap("_xydata", m_funcs.getRawData());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		BPXYData xydata = (BPXYData) data.get("_xydata");
		m_funcs = new BPTableFuncsXY(xydata);
		m_model = new BPTableModel<BPXData>(m_funcs);
		m_table.setModel(m_model);
		m_model.setDatas(xydata.getDatas());
		m_funcs.setReadonly(!editable);
		m_table.initRowSorter();
		m_tb.setVisible(editable);

		TableColumnModel tcm = m_table.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
			tcm.getColumn(i).setPreferredWidth(180);
	}

	protected void initForm()
	{
		m_table = new BPTable<BPXData>();
		JScrollPane scroll = new JScrollPane(m_table);
		JPanel pnl = new JPanel();
		BPToolBarSQ tb = new BPToolBarSQ(true);
		BPAction actclone = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCLONE, this::onClone);
		tb.setActions(new Action[] { BPAction.separator(), actclone });
		tb.setBorderVertical(0);
		m_tb = tb;

		m_table.setMonoFont();
		m_table.setAutoResizeMode(BPTable.AUTO_RESIZE_OFF);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		pnl.add(tb, BorderLayout.WEST);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}

	public BPTable<BPXData> getTable()
	{
		return m_table;
	}

	protected void onClone(ActionEvent e)
	{
		BPXYData xydata = m_funcs.getRawData();
		BPDataActionFactoryCommon.cloneXYDataToNewEditor(xydata, e);
	}

	protected boolean needScroll()
	{
		return false;
	}
}
