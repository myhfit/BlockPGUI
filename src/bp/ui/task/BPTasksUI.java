package bp.ui.task;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.config.BPSetting;
import bp.config.UIConfigs;
import bp.event.BPEventCoreUI;
import bp.task.BPTask;
import bp.task.BPTaskManager;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogForm;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.form.BPFormManager;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPProgressBar;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.table.BPTableFuncsTask;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
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
		m_tabtasks = new BPTable<BPTask<?>>(new BPTableFuncsTask());
		m_toolbar = new BPToolBarSQ(true);
		m_model = m_tabtasks.getBPTableModel();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(m_tabtasks);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_tabtasks.setTableFont();
		m_pgselcolor = UIManager.getColor("Table.selectionBackground");

		BPToolVIconButton btnadd = new BPToolVIconButton(BPAction.build("").callback(this::onAdd).tooltip("Add Task").vIcon(BPIconResV.ADD()).getAction(), this);
		BPToolVIconButton btndel = new BPToolVIconButton(BPAction.build("").callback(this::onDel).tooltip("Remove Task").vIcon(BPIconResV.DEL()).getAction(), this);
		BPToolVIconButton btnstart = new BPToolVIconButton(BPAction.build("").callback(this::onStart).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).tooltip("Start Task(F5)").vIcon(BPIconResV.START()).getAction(), this);
		BPToolVIconButton btnstop = new BPToolVIconButton(BPAction.build("").callback(this::onStop).tooltip("Stop Task").vIcon(BPIconResV.STOP()).getAction(), this);
		BPToolVIconButton btnedit = new BPToolVIconButton(BPAction.build("").callback(this::onEdit).tooltip("Edit Task").vIcon(BPIconResV.EDIT()).getAction(), this);
		BPToolVIconButton btnmoveup = new BPToolVIconButton(BPAction.build("").callback(this::onMoveUp).tooltip("Move Up").vIcon(BPIconResV.TOUP()).getAction(), this);
		BPToolVIconButton btnmovedown= new BPToolVIconButton(BPAction.build("").callback(this::onMoveDown).tooltip("Move Down").vIcon(BPIconResV.TODOWN()).getAction(), this);
		int btnsize = (int) (16f * UIConfigs.UI_SCALE());
		setupButtons(btnsize, btnadd, btndel, btnstart, btnstop, btnedit, btnmoveup);

		m_tabtasks.setModel(m_model);
		m_tabtasks.setDefaultRenderer(Float.class, new BPTable.BPTableRendererReplace(this::getCellComponent));
		m_toolbar.add(Box.createRigidArea(new Dimension(4, 4)));
		if (canModify())
		{
			m_toolbar.add(btnadd);
			m_toolbar.add(btndel);
			m_toolbar.add(btnedit);
			m_toolbar.add(Box.createRigidArea(new Dimension(4, 4)));
			m_toolbar.add(btnstart);
		}
		m_toolbar.add(btnstop);
		if (canModify())
		{
			m_toolbar.add(Box.createRigidArea(new Dimension(4, 4)));
			m_toolbar.add(btnmoveup);
			m_toolbar.add(btnmovedown);
		}

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
		List<BPTask<?>> datas = new ArrayList<BPTask<?>>();
		datas.addAll(listTasks());
		m_model.setDatas(datas);
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
		CommonUIOperations.showNewTask();
	}

	protected Component getCellComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
		Component rc = null;
		if (value != null && col == 2)
		{
			if (!(value instanceof Number))
				Std.info((String) value);
			BPProgressBar pbar = new BPProgressBar();
			pbar.setMaximum(1000);
			pbar.setFont(table.getFont());
			pbar.setSelectedBackgroundColor(m_pgselcolor);
			pbar.setSelectedBackground(isSelected);
			float v = ((Number) value).floatValue();
			int v2 = (int) Math.floor(v * 1000f);
			String pstr = m_model.getRow(row).getProgressText();
			if (pstr == null)
			{
				String vstr = NumberUtil.formatPercent(v);
				pbar.setString(vstr);
			}
			else
			{
				pbar.setString(pstr);
			}
			pbar.setValue(v2);
			pbar.setStringPainted(true);
			rc = pbar;
		}
		return rc;
	}

	protected void onDel(ActionEvent e)
	{
		List<BPTask<?>> tasks = m_tabtasks.getSelectedDatas();
		stopTasks(tasks);
		removeTasks(tasks);
	}

	protected void stopTasks(List<BPTask<?>> tasks)
	{
		for (BPTask<?> task : tasks)
		{
			task.stop();
		}
	}

	protected void removeTasks(List<BPTask<?>> tasks)
	{
		for (BPTask<?> task : tasks)
		{
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
				if (UIStd.confirm(getFocusCycleRootAncestor(), "BlockP - Start task", "Confirm start task(s)?"))
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

				Class<?> c = ClassUtil.tryLoopSuperClass((cls) ->
				{
					if (BPFormManager.containsKey(cls.getName()))
						return cls;
					return null;
				}, task.getClass(), BPTask.class);
				dlg.setup(c == null ? task.getClass().getName() : c.getName(), task);
				dlg.setTitle("Task:" + task.getName());
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
		List<BPTask<?>> tasks = m_model.getDatas();
		int i = tasks.indexOf(task);
		if (i > -1)
		{
			m_model.fireTableCellUpdated(m_model.getDatas().indexOf(task), 1);
			m_model.fireTableCellUpdated(m_model.getDatas().indexOf(task), 2);
		}
	}

	private void onTaskChanged(BPEventCoreUI event)
	{
		initDatas();
	}
}
