package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.data.BPXData;
import bp.data.BPXYData;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsXY;

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
		return null;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		BPXYData xydata = (BPXYData) data.get("_xydata");
		m_funcs=new BPTableFuncsXY(xydata);
		m_model=new BPTableModel<BPXData>(m_funcs);
		m_table.setModel(m_model);
		m_model.setDatas(xydata.getDatas());
		if (!editable)
		{
		}
		m_table.initRowSorter();
		m_tb.setVisible(editable);
	}

	protected void initForm()
	{
		m_table = new BPTable<BPXData>();
		JScrollPane scroll = new JScrollPane(m_table);
		JPanel pnl = new JPanel();
		BPToolBarSQ tb = new BPToolBarSQ(true);
//		BPAction actadd = BPAction.build("add").callback(this::onAdd).vIcon(BPIconResV.ADD()).getAction();
//		BPAction actdel = BPAction.build("del").callback(this::onDel).vIcon(BPIconResV.DEL()).getAction();
		tb.setActions(new Action[] { BPAction.separator() });
		tb.setBorderVertical(0);
		m_tb = tb;

		m_table.setMonoFont();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		pnl.add(tb, BorderLayout.WEST);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}

}
