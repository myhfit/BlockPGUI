package bp.ui.form.dynamic.controller.project;

import java.util.HashMap;
import java.util.Map;

import bp.BPCore;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.ui.form.dynamic.controller.BPFormControllerCommon;

public class BPFormControllerProjectFile extends BPFormControllerCommon implements BPFormController
{
	public boolean validateValue(String key, BPFormContext context)
	{
		switch (key)
		{
			case "name":
			{
				String newname = getItemValue("name", context);
				if (context.snapshot == null || (!newname.equals(context.snapshot.getOrDefault("name", null))))
					return checkProjectName(newname);
			}
		}
		return true;
	}

	public Object select(String key, Object old, BPFormContext context)
	{
		switch (key)
		{
			case "path":
				return onSelectResourceDir((String) old, true);
		}
		return null;
	}

	public void initSnapshot(Map<String, ?> data, BPFormContext context)
	{
		context.snapshot = new HashMap<String, Object>();
		if (data != null)
			context.snapshot.put("name", data.get("name"));
	}

	protected boolean checkProjectName(String name)
	{
		return BPCore.getProjectsContext().checkProjectName(name);
	}
}
