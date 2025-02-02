package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.data.BPSLData;
import bp.task.BPTask;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogForm;
import bp.ui.dialog.BPDialogNewTask;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPBoxButtons;
import bp.ui.scomp.BPToolVIconButton;
import bp.util.ObjUtil;

public class BPFormPanelTaskSerial extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3877137979902221630L;

	protected BPBoxButtons<Map<String, Object>> m_lsttasks;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		List<Map<String, Object>> tasks = m_lsttasks.getDatas();
		rc.put("tasks", tasks);
		return rc;
	}

	protected boolean needScroll()
	{
		return false;
	}

	protected void initForm()
	{
		super.initForm();

		m_lsttasks = new BPBoxButtons<Map<String, Object>>(BoxLayout.Y_AXIS);
		m_lsttasks.setShowSelect(false);
		JLabel lbl = makeLineLabel("Sub Tasks", true);
		JScrollPane scroll = new JScrollPane();
		JPanel tpan = new JPanel();
		JPanel tpan2 = new JPanel();
		JPanel panpm = new JPanel();
		BPToolVIconButton btnadd = new BPToolVIconButton(BPAction.build("").vIcon(BPIconResV.ADD()).callback(this::onAdd).getAction());

		m_lsttasks.setRenderer(this::renderTask);
		m_lsttasks.setClickHandler(this::onClickTask);

		lbl.setPreferredSize(new Dimension(m_labelwidth, m_lineheight));
		lbl.setMinimumSize(new Dimension(m_labelwidth, m_lineheight));

		panpm.setLayout(new FlowLayout(FlowLayout.CENTER));
		tpan.setLayout(new BorderLayout());
		tpan2.setLayout(new BorderLayout());

		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		panpm.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		tpan2.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_TABLEGRID()));

		tpan.setBorder(null);

		panpm.add(btnadd);
		scroll.setViewportView(m_lsttasks);
		tpan2.add(scroll, BorderLayout.CENTER);
		tpan2.add(panpm, BorderLayout.SOUTH);
		tpan.add(lbl, BorderLayout.WEST);
		tpan.add(tpan2, BorderLayout.CENTER);

		m_lsttasks.setDatas(new ArrayList<Map<String, Object>>());

		doAddLineComponents(null, true, 0, new Component[] { tpan });
	}

	protected void onAdd(ActionEvent e)
	{
		BPDialogNewTask dlg = new BPDialogNewTask();
		dlg.setVisible(true);
		BPTask<?> task = dlg.getTask();
		if (task != null)
		{
			m_lsttasks.addData(task.getSaveData());
		}
	}

	protected void onClickTask(Map<String, Object> task)
	{
		BPDialogForm dlg = new BPDialogForm();
		String clsname = (String) task.get(BPSLData.CLSNAME_FIELD);
		dlg.setup(clsname, task);
		dlg.setTitle("Task:" + ObjUtil.toString(task.get("name"), ""));
		dlg.setVisible(true);
		Map<String, Object> r = dlg.getFormData();
		if (r != null)
		{
			task.clear();
			task.putAll(r);
			task.put(BPSLData.CLSNAME_FIELD, clsname);
			m_lsttasks.refresh();
		}
	}

	protected String renderTask(Map<String, Object> taskmap)
	{
		return (String) taskmap.get("name");
	}

	@SuppressWarnings("unchecked")
	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		List<Map<String, Object>> tasks = (List<Map<String, Object>>) data.get("tasks");
		m_lsttasks.setDatas(tasks);
	}
}
