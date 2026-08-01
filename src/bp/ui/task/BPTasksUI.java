package bp.ui.task;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import bp.BPCore;
import bp.config.BPSetting;
import bp.config.UIConfigs;
import bp.event.BPEventCoreUI;
import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleHelpers;
import bp.task.BPTask;
import bp.task.BPTaskFactory;
import bp.task.BPTaskManager;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogForm;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPProgressBar;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableColumnModel;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsTask;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.NumberUtil;
import bp.util.Std;

public class BPTasksUI extends JPanel implements BPComponent<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7510092455593893235L;
	protected BPTable<BPTask<?>> m_tabtasks;
	protected BPTableModel<BPTask<?>> m_model;
	protected BPToolBarSQ m_toolbar;

	protected Consumer<BPEventCoreUI> m_statushandler;
	protected Consumer<BPEventCoreUI> m_changedhandler;

	protected Color m_pgselcolor;
	protected BPTableCellRendererTask m_taskrenderer;

	public BPTasksUI()
	{
		initUI();
		initDatas();
		initEvents();
	}

	protected void initEvents()
	{
		m_statushandler = this::onTaskStatusChanged;
		m_changedhandler = this::onTaskChanged;
		registerEventHandlers();
	}

	protected void registerEventHandlers()
	{
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKSTATUS, m_statushandler);
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASK, m_changedhandler);
	}

	protected boolean canModify()
	{
		return true;
	}

	protected void initUI()
	{
		BPTableFuncsTask tf = new BPTableFuncsTask();
		m_tabtasks = new BPTable<BPTask<?>>(tf);
		m_toolbar = new BPToolBarSQ(true);
		m_model = m_tabtasks.getBPTableModel();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(m_tabtasks);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_tabtasks.setTableFont();
		m_pgselcolor = UIManager.getColor("Table.selectionBackground");
		m_taskrenderer = new BPTableCellRendererTask();

		{
			BPAction actadd = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_CREATE_ACC, this::onAdd);
			BPAction actdel = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNDEL, BPActionConstCommon.ACT_BTNDEL_ACC, this::onDel);
			BPAction actstart = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNSTART, BPActionConstCommon.ACT_BTNSTART_ACC, this::onStart);
			BPAction actstop = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNSTOP, BPActionConstCommon.ACT_BTNSTOP_ACC, this::onStop);
			BPAction actedit = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNEDIT, BPActionConstCommon.ACT_BTNEDIT_ACC, this::onEdit);
			BPAction actmoveup = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNUP, BPActionConstCommon.ACT_BTNUP_ACC, this::onMoveUp);
			BPAction actmovedown = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNDOWN, BPActionConstCommon.ACT_BTNDOWN_ACC, this::onMoveDown);

			List<Action> acts = new ArrayList<>();

			m_tabtasks.setModel(m_model);
			m_tabtasks.setDefaultRenderer(String.class, m_taskrenderer);
			m_tabtasks.setDefaultRenderer(Float.class, m_taskrenderer);
			m_tabtasks.setDefaultRenderer(BPTask.class, m_taskrenderer);
			{
				BPTableColumnModel tcm = m_tabtasks.getBPColumnModel();
				tcm.getColumnBuilder(1).setPreferredWidth(80).setMaxWidth(80).setMinWidth(80);
			}
			if (canModify())
			{
				acts.add(BPAction.separator());
				acts.add(actadd);
				acts.add(actdel);
				acts.add(actedit);
				acts.add(BPAction.separator());
				acts.add(actstart);
			}
			acts.add(actstop);
			if (canModify())
			{
				acts.add(BPAction.separator());
				acts.add(actmoveup);
				acts.add(actmovedown);
			}
			m_toolbar.setBorderVertical(0);
			m_toolbar.setMaxScrollSize();
			m_toolbar.setActions(acts.toArray(new Action[acts.size()]), this);
		}

		{
			BPAction ctxstart = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSTART, this::onStart);
			BPAction ctxstop = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSTOP, this::onStop);
			BPAction ctxdel = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDEL, this::onDel);
			BPAction ctxedit = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUEDIT, this::onEdit);
			BPAction.batchSetNameFromTooltip(ctxstart, ctxstop);
			List<Action> acts = new ArrayList<>();
			if (canModify())
			{
				acts.add(ctxstart);
			}
			acts.add(ctxstop);
			if (canModify())
			{
				acts.add(BPAction.separator());
				acts.add(ctxdel);
				acts.add(BPAction.separator());
				acts.add(ctxedit);
			}
			tf.setCustomActions(acts);
		}

		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
	}

	protected void initDatas()
	{
		m_model.setDatas(new ArrayList<BPTask<?>>(listTasks()));
		m_model.fireTableDataChanged();
	}

	protected List<BPTask<?>> listTasks()
	{
		return getTaskManager().listTasks();
	}

	protected BPTaskManager getTaskManager()
	{
		return BPCore.getWorkspaceContext().getTaskManager();
	}

	protected void onAdd(ActionEvent e)
	{
		BPTask<?> task = CommonUIOperations.showCreate(BPTaskFactory.class);
		if (task != null)
			BPCore.addTask(task);
	}

	protected void onDel(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		if (tasks != null && tasks.size() > 0 && UIStd.confirm(this.getTopLevelAncestor(), null, BPActionConstCommon.TXT_CONFIRM_DEL_TASK.text() + "?"))
		{
			for (BPTask<?> task : tasks)
				task.stop();
			for (BPTask<?> task : tasks)
				BPCore.removeTask(task);
		}
	}

	protected void onStop(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		for (BPTask<?> task : tasks)
		{
			if (task.isRunning())
				task.stop();
		}
	}

	protected void onStart(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		for (BPTask<?> task : tasks)
		{
			if (task.needConfirm())
			{
				if (UIStd.confirm(SwingUtilities.getWindowAncestor(this), UIUtil.wrapBPTitles(BPLocaleConstCC.START, BPLocaleConstCC.TASK), BPActionConstCommon.TXT_CONFIRMSTARTTASK.text() + "?"))
					break;
				else
					return;
			}
		}
		for (BPTask<?> task : tasks)
		{
			if (!task.isRunning())
				task.start();
		}
	}

	protected void onMoveUp(ActionEvent e)
	{
		int pos = m_tabtasks.getSelectedRow();
		if (pos <= 0)
			return;
		BPTask<?> task = m_tabtasks.getSelectedData();
		getTaskManager().moveTask(task, -1);
		BPCore.getWorkspaceContext().saveTasks();
		initDatas();
		m_tabtasks.setSelectionRows(new int[] { pos - 1 });
	}

	protected void onMoveDown(ActionEvent e)
	{
		int pos = m_tabtasks.getSelectedRow();
		if (pos < 0 || pos == m_tabtasks.getRowCount() - 1)
			return;
		BPTask<?> task = m_tabtasks.getSelectedData();
		getTaskManager().moveTask(task, 1);
		BPCore.getWorkspaceContext().saveTasks();
		initDatas();
		m_tabtasks.setSelectionRows(new int[] { pos + 1 });
	}

	protected void onEdit(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		if (tasks.size() > 0)
		{
			BPTask<?> task = tasks.get(0);
			boolean isrun = task.isRunning();
			BPSetting setting = task.getSetting();
			if (setting != null)
			{
				BPDialogSetting dlg = new BPDialogSetting();
				dlg.setSetting(setting);
				dlg.setEditable(!isrun);
				dlg.setTitle(task.getClass().getName() + ":" + task.getName());
				dlg.pack();
				dlg.setVisible(true);
				if (!isrun)
				{
					BPSetting newsetting = dlg.getResult();
					if (newsetting != null)
					{
						task.setSetting(newsetting);
						saveTask(task);
						m_model.fireTableDataChanged();
					}
				}
			}
			else
			{
				BPDialogForm dlg = new BPDialogForm();
				dlg.setEditable(!isrun);
				dlg.setup(task.getClass(), null, task);
				dlg.setTitle(UIUtil.wrapBPTitle(BPActionConstCommon.TXT_TASK) + ":" + task.getName());
				dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(700, 600)));
				dlg.pack();
				dlg.setLocationRelativeTo(null);
				dlg.setVisible(true);
				if (!isrun)
				{
					Map<String, Object> formdata = dlg.getFormData();
					if (formdata != null)
					{
						task.setMappedData(formdata);
						saveTask(task);
						m_model.fireTableDataChanged();
					}
				}
			}
		}
	}

	protected void saveTask(BPTask<?> task)
	{
		BPCore.saveTasks();
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	private void onTaskStatusChanged(BPEventCoreUI event)
	{
		BPTask<?> task = (BPTask<?>) event.datas[0];
		int i = m_model.getDatas().indexOf(task);
		if (i > -1)
		{
			m_model.fireTableCellUpdated(i, 1);
			m_model.fireTableCellUpdated(i, 2);
		}
	}

	private void onTaskChanged(BPEventCoreUI event)
	{
		initDatas();
	}

	public static class BPTableCellRendererTask extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 242433924056354449L;

		protected Color m_pgselcolor;
		protected Color m_pgcolor;
		protected Color m_hfcolor;

		protected JPanel m_pnl;
		protected BPLabel m_lbl1;
		protected BPLabel m_lbl2;
		
		public BPTableCellRendererTask()
		{
			m_pgselcolor=UIManager.getColor("Table.selectionBackground");
			m_pgcolor=UIManager.getColor("Table.background");
			m_pnl = new JPanel();
			m_lbl1 = new BPLabel();
			m_lbl2 = new BPLabel();
			m_lbl1.setPreferredSize(new Dimension(120,0));
			m_lbl1.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 2), new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER())));
			m_lbl1.setForeground(UIConfigs.COLOR_TEXTHALF());
			m_lbl1.setLabelFont();
			m_lbl2.setLabelFont();
			m_pnl.setLayout(new BorderLayout());
			m_pnl.add(m_lbl1, BorderLayout.WEST);
			m_pnl.add(m_lbl2, BorderLayout.CENTER);
		}

		@SuppressWarnings("unchecked")
		public Component getTableCellRendererComponent(JTable table, Object value, boolean issel, boolean isfocus, int row, int col)
		{
			if (value != null)
			{
				if (col == 0)
				{
					BPTask<?> task = (BPTask<?>) value;
					m_lbl1.setText(BPLocaleHelpers.translateByClass(BPTask.class, task.getTaskName()));
					m_lbl2.setText(task.getName());
					m_pnl.setBackground(issel ? m_pgselcolor : m_pgcolor);
					if (issel)
					{
						if (m_hfcolor == null)
							m_hfcolor = UIUtil.mix(UIConfigs.COLOR_TEXTHALF(), table.getSelectionForeground(), 255);
						m_lbl1.setForeground(m_hfcolor);
						m_lbl2.setForeground(table.getSelectionForeground());
					}
					else
					{
						m_lbl1.setForeground(UIConfigs.COLOR_TEXTHALF());
						m_lbl2.setForeground(table.getForeground());
					}
					return m_pnl;
				}
				else if (col == 1)
				{
					super.getTableCellRendererComponent(table, value, issel, isfocus, row, col);
					setHorizontalAlignment(CENTER);
					return this;
				}
				else if (col == 2)
				{
					if (!(value instanceof Number))
						Std.info((String) value);
					BPProgressBar pbar = new BPProgressBar();
					pbar.setMaximum(1000);
					pbar.setFont(table.getFont());
					pbar.setSelectedBackgroundColor(m_pgselcolor);
					pbar.setSelectedBackground(issel);
					if (!(value instanceof Number))
						Std.info((String) value);
					float v = ((Number) value).floatValue();
					int v2 = (int) Math.floor(v * 1000f);
					BPTableModel<BPTask<?>> model = ((BPTable<BPTask<?>>) table).getBPTableModel();
					String pstr = model.getRow(row).getProgressText();
					if (pstr == null)
						pstr = NumberUtil.formatPercent(v);
					pbar.setString(pstr);
					pbar.setValue(v2);
					pbar.setStringPainted(true);
					return pbar;
				}
			}
			return super.getTableCellRendererComponent(table, value, issel, isfocus, row, col);
		}
	}
}
