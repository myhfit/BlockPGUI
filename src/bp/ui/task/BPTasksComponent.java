package bp.ui.task;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import bp.BPCore;
import bp.event.BPEventCoreUI;
import bp.task.BPTask;
import bp.ui.BPComponent;
import bp.ui.scomp.BPProgressBar;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsTask;
import bp.util.NumberUtil;

public class BPTasksComponent extends JPanel implements BPComponent<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6757021729889711106L;

	protected BPTable<BPTask<?>> m_tabtasks;
	protected BPTableModel<BPTask<?>> m_model;

	protected Consumer<BPEventCoreUI> m_statushandler;
	protected Consumer<BPEventCoreUI> m_changedhandler;

	protected Color m_pgselcolor;

	public BPTasksComponent()
	{
		initUI();
		initDatas();
		initEvents();
	}

	protected void initUI()
	{
		m_tabtasks = new BPTable<BPTask<?>>(new BPTableFuncsTask());
		m_model = m_tabtasks.getBPTableModel();
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(m_tabtasks);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_tabtasks.setTableFont();
		m_pgselcolor = UIManager.getColor("Table.selectionBackground");

		m_tabtasks.setModel(m_model);
		m_tabtasks.setDefaultRenderer(Float.class, new BPTable.BPTableRendererReplace(this::getCellComponent));

		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	protected void initEvents()
	{
		m_statushandler = this::onTaskStatusChanged;
		m_changedhandler = this::onTaskChanged;
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKSTATUS, m_statushandler);
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASK, m_changedhandler);
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

	protected void initDatas()
	{
		List<BPTask<?>> datas = new ArrayList<BPTask<?>>();
		datas.addAll(BPCore.listTasks());
		m_model.setDatas(datas);
		m_model.fireTableDataChanged();
	}

	protected Component getCellComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
		Component rc = null;
		if (value != null && col == 2)
		{
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
}
