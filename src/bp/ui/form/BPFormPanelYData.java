package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.data.BPYData;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPList;

public class BPFormPanelYData extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6783690646828501214L;

	protected BPList<Object> m_list;
	protected BPListModelYData m_model;
	protected BPToolBarSQ m_tb;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		BPYData ydata = m_model.getYData();
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("_list", ydata);
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		BPYData ydata = (BPYData) data.get("_list");
		m_model.setYData(ydata);
		if (!editable)
		{
		}
		m_tb.setVisible(editable);
	}

	protected void onAdd(ActionEvent e)
	{
	}

	protected void onDel(ActionEvent e)
	{
	}

	protected void initForm()
	{
		m_list = new BPList<Object>();
		m_model = new BPListModelYData();
		m_list.setModel(m_model);
		JScrollPane scroll = new JScrollPane(m_list);
		JPanel pnl = new JPanel();
		BPToolBarSQ tb = new BPToolBarSQ(true);
		BPAction actadd = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNADD, this::onAdd);
		BPAction actdel = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, this::onDel);
		tb.setActions(new Action[] { BPAction.separator(), actadd, actdel });
		tb.setBorderVertical(0);
		m_tb = tb;

		m_list.setMonoFont();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		pnl.add(tb, BorderLayout.WEST);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}

	public static class BPListModelYData extends AbstractListModel<Object>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 404177365385186856L;

		protected volatile BPYData m_ydata;

		public BPListModelYData()
		{
			m_ydata = new BPYData.BPYDataArrayList();
		}

		public void setYData(BPYData ydata)
		{
			m_ydata = ydata;
			int s = 0;
			if (ydata != null)
				s = ydata.length();
			if (s > 0)
				fireIntervalRemoved(this, 0, s - 1);
			int rs = ydata.length();
			if (rs > 0)
				fireIntervalAdded(this, 0, rs - 1);
		}

		public BPYData getYData()
		{
			return m_ydata;
		}

		public int getSize()
		{
			return m_ydata.length();
		}

		public Object getElementAt(int index)
		{
			return m_ydata.getValue(index);
		}
	}
}