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
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.config.UIConfigs;
import bp.schedule.BPSchedule;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogForm;
import bp.ui.form.BPFormManager;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.table.BPTableFuncsBase;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.ScheduleUtil;

public class BPSchedulesUI extends JPanel implements BPComponent<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7510092455593893235L;
	protected BPTable<BPSchedule> m_tabschedules;
	protected BPTableModel<BPSchedule> m_model;
	protected BPTableFuncsSchedule m_tablefunc;
	protected BPToolBarSQ m_toolbar;

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
		m_tablefunc = new BPTableFuncsSchedule();
		m_tablefunc.setSchedulesUI(this);
		m_tabschedules = new BPTable<BPSchedule>(m_tablefunc);
		m_toolbar = new BPToolBarSQ(true);
		m_model = m_tabschedules.getBPTableModel();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(m_tabschedules);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_tabschedules.setTableFont();
		m_pgselcolor = UIManager.getColor("Table.selectionBackground");

		BPToolVIconButton btnadd = new BPToolVIconButton(BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNADD, this::onAdd));
		BPToolVIconButton btndel = new BPToolVIconButton((BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNDEL, BPActionConstCommon.ACT_BTNDEL_ACC, this::onDel)), this);
		BPToolVIconButton btnedit = new BPToolVIconButton(BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNEDIT, this::onEdit, ab -> ab.acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0))), this);
		BPToolVIconButton btnenable = new BPToolVIconButton(BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNENABLE, this::onEnable), this);
		BPToolVIconButton btndisable = new BPToolVIconButton(BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDISABLE, this::onDisable), this);
		int btnsize = (int) (16f * UIConfigs.UI_SCALE());
		setupButtons(btnsize, btnadd, btndel, btnedit, btnenable, btndisable);

		m_tabschedules.setModel(m_model);
		m_toolbar.add(Box.createRigidArea(new Dimension(4, 4)));
		m_toolbar.add(btnadd);
		m_toolbar.add(btndel);
		m_toolbar.add(btnedit);
		m_toolbar.add(Box.createRigidArea(new Dimension(4, 4)));
		m_toolbar.add(btnenable);
		m_toolbar.add(btndisable);

		setLayout(new BorderLayout());
		m_toolbar.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
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
		m_tablefunc.setScheduleKeyMap(sdkeymap);
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
		deleteSelectedSchedules();
	}

	public void deleteSelectedSchedules()
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		ScheduleUtil.removeSchedulesAndSave(sds);
		initDatas();
	}

	protected void onEnable(ActionEvent e)
	{
		enableSelectedSchedules();
	}

	public void enableSelectedSchedules()
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		for (BPSchedule sd : sds)
			sd.setEnabled(true);
		BPCore.saveSchedules();
		m_model.fireTableDataChanged();
	}

	protected void onDisable(ActionEvent e)
	{
		disableSelectedSchedules();
	}

	public void disableSelectedSchedules()
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		for (BPSchedule sd : sds)
			sd.setEnabled(false);
		BPCore.saveSchedules();
		m_model.fireTableDataChanged();
	}

	protected void onEdit(ActionEvent e)
	{
		editSelectedSchedule();
	}

	public void editSelectedSchedule()
	{
		List<BPSchedule> sds = m_tabschedules.getSelectedDatas();
		if (sds.size() > 0)
		{
			BPSchedule sd = sds.get(0);
			BPDialogForm dlg = new BPDialogForm();
			Class<?> c = ClassUtil.tryLoopSuperClass((cls) ->
			{
				if (BPFormManager.containsKey(cls.getName()))
					return cls;
				return null;
			}, sd.getClass(), BPSchedule.class);
			dlg.setup(c == null ? sd.getClass().getName() : c.getName(), sd);
			dlg.setTitle("Schedule:" + sd.getName());
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

	public static class BPTableFuncsSchedule extends BPTableFuncsBase<BPSchedule>
	{
		protected Map<BPSchedule, String> m_sdkeymap;
		protected WeakRefGo<BPSchedulesUI> m_sdsuiref;

		public BPTableFuncsSchedule()
		{
			m_colnames = new String[] { "Name", "Status", "Class", "Scheduler" };
			m_cols = new Class<?>[] { String.class, String.class, String.class, String.class };
		}

		public void setSchedulesUI(BPSchedulesUI ui)
		{
			m_sdsuiref = new WeakRefGo<BPSchedulesUI>(ui);
		}

		public String[] getColumnNames()
		{
			return m_colnames;
		}

		public Class<?>[] getColumnClasses()
		{
			return m_cols;
		}

		public void setScheduleKeyMap(Map<BPSchedule, String> sdkeymap)
		{
			m_sdkeymap = sdkeymap;
		}

		public Object getValue(BPSchedule sd, int row, int col)
		{
			Object rc = null;
			switch (col)
			{
				case 0:
				{
					return nvl(sd.getName());
				}
				case 1:
				{
					return sd.isEnabled() ? "Enabled" : "Disabled";
				}
				case 2:
				{
					return sd.getClass().getName();
				}
				case 3:
				{
					return nvl(m_sdkeymap.get(sd));
				}
			}
			return rc;
		}

		public List<Action> getActions(BPTable<BPSchedule> table, List<BPSchedule> datas, int[] rows, int r, int c)
		{
			List<Action> rc = new ArrayList<Action>();
			BPAction actdel = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDEL, e -> m_sdsuiref.run(ui -> ui.deleteSelectedSchedules()));
			BPAction actedit = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUEDIT, e -> m_sdsuiref.run(ui -> ui.editSelectedSchedule()));
			BPAction actenable = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUENABLE, e -> m_sdsuiref.run(ui -> ui.enableSelectedSchedules()));
			BPAction actdisable = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDISABLE, e -> m_sdsuiref.run(ui -> ui.disableSelectedSchedules()));
			rc.add(actenable);
			rc.add(actdisable);
			rc.add(BPAction.separator());
			rc.add(actedit);
			rc.add(BPAction.separator());
			rc.add(actdel);
			return rc;
		}
	}

}
