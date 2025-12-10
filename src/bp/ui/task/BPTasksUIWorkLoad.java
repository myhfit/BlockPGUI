package bp.ui.task;

import bp.BPCore;
import bp.task.BPTaskManager;

public class BPTasksUIWorkLoad extends BPTasksUI
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8185661746133754036L;

	protected boolean canModify()
	{
		return false;
	}

	protected BPTaskManager getTaskManager()
	{
		return BPCore.getWorkspaceContext().getWorkLoadManager();
	}
}
