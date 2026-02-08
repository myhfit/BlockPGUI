package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.util.UIStd;
import bp.util.JSONUtil;

public class BPFormPanelList extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2864750142824142763L;

	protected BPList<Object> m_list;
	protected BPListModel<Object> m_model;
	protected BPToolBarSQ m_tb;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		List<Object> datas = m_model.getDatas();
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("_list", datas);
		return rc;
	}

	@SuppressWarnings("unchecked")
	public void showData(Map<String, ?> data, boolean editable)
	{
		List<Object> ydata = (List<Object>) data.get("_list");
		m_model.setDatas(ydata);
		if (!editable)
		{
		}
		m_tb.setVisible(editable);
	}

	protected void onAdd(ActionEvent e)
	{
		String json = UIStd.input("", "Input Data(json)", "BlockP - Input data");
		if (json != null)
		{
			Object obj = JSONUtil.decode(json);
			if (obj == null)
				obj = ((List<?>) JSONUtil.decode("[" + json + "]")).get(0);
			List<Object> datas = m_model.getDatas();
			datas.add(obj);
			m_list.updateUI();
		}
	}

	protected void onDel(ActionEvent e)
	{
		List<Object> datas = m_model.getDatas();
		datas.remove(m_list.getSelectedIndex());
		m_list.updateUI();
	}

	protected void initForm()
	{
		m_list = new BPList<Object>();
		m_model = new BPListModel<Object>();
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
}