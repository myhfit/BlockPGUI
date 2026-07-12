package bp.ui.form.dynamic.controller.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.task.BPTask;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.ui.form.dynamic.controller.BPFormControllerCommon;

public class BPFormControllerTask extends BPFormControllerCommon implements BPFormController
{
	public boolean validateValue(String key, BPFormContext context)
	{
		switch (key)
		{
			case "name":
			{
				String newname = getItemValue("name", context);
				if (context.snapshot == null || (!newname.equals(context.snapshot.getOrDefault("name", null))))
					return checkTaskName(newname);
			}
		}
		return true;
	}

	protected boolean checkTaskName(String name)
	{
		List<BPTask<?>> tasks = BPCore.getWorkspaceContext().getTaskManager().listTasks();
		for (BPTask<?> t : tasks)
		{
			if (name.equals(t.getName()))
				return false;
		}
		return true;
	}

	public void initSnapshot(Map<String, ?> data, BPFormContext context)
	{
		context.snapshot = new HashMap<String, Object>();
		if (data != null)
			context.snapshot.put("name", data.get("name"));
	}
}
