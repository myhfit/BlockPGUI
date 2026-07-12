package bp.ui.form.dynamic.controller.task;

import bp.ui.form.dynamic.BPFormContext;

public class BPFormControllerTaskPackFiles extends BPFormControllerTask
{
	public Object select(String key, Object oldvalue, BPFormContext context)
	{
		switch (key)
		{
			case "source":
				return onSelectResourceDirList((String) oldvalue);
			case "sourcebase":
			case "targetdir":
				return onSelectResourceDir((String) oldvalue, true);
		}
		return super.select(key, oldvalue, context);
	}
}
