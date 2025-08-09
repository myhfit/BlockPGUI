package bp.ui.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.border.MatteBorder;

import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.env.BPEnvCommon;
import bp.env.BPEnvManager;
import bp.ui.console.BPConsoleUI;
import bp.ui.res.icon.BPIconResV;
import bp.ui.schedule.BPSchedulesUI;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.task.BPTasksUI;
import bp.ui.task.BPTasksUIWorkLoad;

public class BPTabBottom extends BPTabbedContainerBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7593371633606229770L;

	protected BPTasksUI m_tasksui;
	protected BPTasksUIWorkLoad m_tasksuiwl;
	protected BPSchedulesUI m_sdsui;

	public BPTabBottom()
	{
		initUI();
	}

	protected void initUI()
	{
		m_tabbar.setLeftComponent(m_sdsui);
		m_tabbar.setNoClose(true);
		m_tabbar.setTabBorderPos(2);
		m_tabbar.setLabelCenter(true);
		m_tabbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_tabbar.setBarWidth(90);
		m_tabbar.setBarHeight(18);

		setMinimumSize(new Dimension(1, (int) m_tabbar.getMinimumSize().getHeight() - 1));

		m_tasksui = new BPTasksUI();
		m_tasksuiwl = new BPTasksUIWorkLoad();
		addBPTab("TasksUI", (Icon) null, "Tasks", m_tasksui);

		if ("true".equalsIgnoreCase(BPEnvManager.getEnvValue(BPEnvCommon.ENV_NAME_COMMON, BPEnvCommon.ENVKEY_ENABLE_SCHEDULE)))
		{
			m_sdsui = new BPSchedulesUI();
			addBPTab("SchedulesUI", null, "Schedules", m_sdsui, false);
		}
		BPConsoleUI consoleui = new BPConsoleUI();
		addBPTab("ConsoleUI", null, "Console", consoleui, false);
		addBPTab("TasksUIWL", null, "Work", m_tasksuiwl, false);

		BPToolVIconButton togglebtn = new BPToolVIconButton(BPIconResV.UPDOWN(), this::onToggle);
		togglebtn.setPreferredSize(new Dimension((int) (UIConfigs.UI_FIX_SCALE() * UIConfigs.BAR_HEIGHT_VERTICAL()), 0));
		togglebtn.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		m_tabbar.setLeftComponent(togglebtn);
		m_tabbar.setRightComponent(null);
	}

	protected void onToggle(ActionEvent e)
	{
		BPGUICore.runOnMainFrame(mf -> mf.toggleBottomPanel());
	}

	public void setInfoComp(Component comp)
	{
		m_tabbar.setRightComponent(comp);
	}
}
