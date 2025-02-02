package bp.ui.task;

import java.util.List;

import bp.BPCore;
import bp.task.BPTask;

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

	protected List<BPTask<?>> listTasks()
	{
		return BPCore.getWorkspaceContext().getWorkLoadManager().listTasks();
	}
}
