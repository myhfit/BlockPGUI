package bp.ui.schedule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import bp.BPCore;
import bp.locale.BPLocaleConstCC;
import bp.schedule.BPSchedule;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogForm;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.table.BPTableFuncsSimple;
import bp.ui.table.BPTableFuncsSimple.BPTableFuncsContextActionMaker;
import bp.ui.table.BPTableFuncsSimple.BPTableFuncsValueGetter;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;
import bp.util.ScheduleUtil;

public class BPSchedulesUI extends JPanel implements BPComponent<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7510092455593893235L;
	protected BPTable<BPSchedule> m_tabschedules;
	protected BPTableModel<BPSchedule> m_model;
	protected BPTableFuncsSimple<BPSchedule> m_tablefunc;
	protected BPToolBarSQ m_toolbar;
	protected Map<BPSchedule, String> m_sdkeymap;
	protected BPTableFuncsValueGetter<BPSchedule> m_cellgetter;
	protected BPTableFuncsContextActionMaker<BPSchedule> m_actcb;

	protected Color m_pgselcolor;

	public BPSchedulesUI()
	{
		initUI();
		initDatas();
		initEvents();
	}

	protected void initEvents()
	{
	}

	protected void initUI()
	{
		m_cellgetter = this::getCellValue;
		m_actcb = this::getScheduleActions;
		m_tablefunc = new BPTableFuncsSimple<>();
		m_tablefunc.setup(new String[] { "Name", "Status", "Class", "Scheduler" }, new String[] { BPLocaleConstCC.NAME.text(), BPLocaleConstCC.STATUS.text(), BPLocaleConstCC.CLASS.text(), BPLocaleConstCC.SCHEDULER.text() },
				new Class<?>[] { String.class, String.class, String.class, String.class });

		m_tablefunc.setValueGetter(m_cellgetter);
		m_tablefunc.setContextActionMaker(m_actcb);
		m_tablefunc.setEditable(false);
		m_tabschedules = new BPTable<BPSchedule>(m_tablefunc);

		m_toolbar = new BPToolBarSQ(true);
		m_model = m_tabschedules.getBPTableModel();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(m_tabschedules);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_tabschedules.setTableFont();
		m_pgselcolor = UIManager.getColor("Table.selectionBackground");

		BPAction actadd = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNADD, this::onAdd);
		BPAction actdel = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNDEL, BPActionConstCommon.ACT_BTNDEL_ACC, this::onDel);
		BPAction actedit = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNEDIT, this::onEdit, ab -> ab.acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0)));
		BPAction actenable = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNENABLE, this::onEnable);
		BPAction actdisable = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDISABLE, this::onDisable);
		Action[] acts = new Action[] { BPAction.separator(), actadd, actdel, actedit, BPAction.separator(), actenable, actdisable };

		m_toolbar.setBorderVertical(0);
		m_tabschedules.setModel(m_model);
		m_toolbar.setActions(acts, this);

		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
	}

	protected void setupButtons(int btnsize, BPToolVIconButton... btns)
	{
		for (BPToolVIconButton btn : btns)
		{
			btn.setButtonSize(btnsize);
		}
	}

	protected void initDatas()
	{
		List<BPSchedule> datas = new ArrayList<BPSchedule>();
		Map<String, List<BPSchedule>> sdmap = BPCore.getScheduleMap();
		Map<BPSchedule, String> sdkeymap = new HashMap<BPSchedule, String>();
		for (Entry<String, List<BPSchedule>> entry : sdmap.entrySet())
		{
			String key = entry.getKey();
			List<BPSchedule> sds = entry.getValue();
			datas.addAll(sds);
			for (BPSchedule sd : sds)
			{
				sdkeymap.put(sd, key);
			}
		}
		m_sdkeymap = sdkeymap;
		m_model.setDatas(datas);
		m_model.fireTableDataChanged();
	}

	protected void onAdd(ActionEvent e)
	{
		CommonUIOperations.showNewSchedule();
		initDatas();
	}

	protected void onDel(ActionEvent e)
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		ScheduleUtil.removeSchedulesAndSave(sds);
		initDatas();
	}

	protected void onEnable(ActionEvent e)
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		for (BPSchedule sd : sds)
			sd.setEnabled(true);
		BPCore.saveSchedules();
		m_model.fireTableDataChanged();
	}

	protected void onDisable(ActionEvent e)
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		for (BPSchedule sd : sds)
			sd.setEnabled(false);
		BPCore.saveSchedules();
		m_model.fireTableDataChanged();
	}

	protected void onEdit(ActionEvent e)
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		if (sds.size() > 0)
		{
			BPSchedule sd = sds.get(0);
			BPDialogForm dlg = new BPDialogForm();
			dlg.setup(sd.getClass(), BPSchedule.class, sd);
			dlg.setTitle(UIUtil.wrapBPTitle(BPActionConstCommon.TXT_SCHEDULE) + ":" + sd.getName());
			dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(700, 600)));
			dlg.pack();
			dlg.setLocationRelativeTo(null);
			dlg.setVisible(true);
			Map<String, Object> formdata = dlg.getFormData();
			if (formdata != null)
			{
				sd.setMappedData(formdata);
				BPCore.saveSchedules();
				m_model.fireTableDataChanged();
			}
		}
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	protected Object getCellValue(BPSchedule sd, int row, int col)
	{
		Object rc = null;
		switch (col)
		{
			case 0:
			{
				String name = sd.getName();
				return name != null ? name : "";
			}
			case 1:
			{
				return sd.isEnabled() ? BPLocaleConstCC.ENABLED.text() : BPLocaleConstCC.DISABLED.text();
			}
			case 2:
			{
				return sd.getClass().getName();
			}
			case 3:
			{
				String sdkey = m_sdkeymap.get(sd);
				return sdkey != null ? sdkey : null;
			}
		}
		return rc;
	}

	protected List<Action> getScheduleActions(BPTable<BPSchedule> table, List<BPSchedule> datas, int[] rows, int r, int c)
	{
		List<Action> rc = new ArrayList<Action>();
		BPAction actdel = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDEL, this::onDel);
		BPAction actedit = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUEDIT, this::onEdit);
		BPAction actenable = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUENABLE, this::onEnable);
		BPAction actdisable = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDISABLE, this::onDisable);
		rc.add(actenable);
		rc.add(actdisable);
		rc.add(BPAction.separator());
		rc.add(actedit);
		rc.add(BPAction.separator());
		rc.add(actdel);
		return rc;
	}
}
