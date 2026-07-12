package bp.ui.form.dynamic.controller.task;

import bp.ui.form.dynamic.BPFormContext;

public class BPFormControllerTaskExec extends BPFormControllerTask
{
	public Object select(String key, Object oldvalue, BPFormContext context)
	{
		switch (key)
		{
			case "target":
				return onSelectResourceFile((String) oldvalue);
			case "workdir":
				return onSelectResourceDir((String) oldvalue, true);
		}
		return super.select(key, oldvalue, context);
	}
}
