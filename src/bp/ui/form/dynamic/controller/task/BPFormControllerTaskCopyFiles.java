package bp.ui.form.dynamic.controller.task;

import bp.ui.form.dynamic.BPFormContext;

public class BPFormControllerTaskCopyFiles extends BPFormControllerTask
{
	public Object select(String key, Object old, BPFormContext context)
	{
		switch (key)
		{
			case "source":
				return onSelectResourceFileSystem((String) old);
			case "target":
				return onSelectResourceDir((String) old, true);
		}
		return super.select(key, old, context);
	}
}
