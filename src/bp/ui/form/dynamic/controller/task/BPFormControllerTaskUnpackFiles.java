package bp.ui.form.dynamic.controller.task;

import java.util.Arrays;
import java.util.List;

import bp.locale.BPLocaleHelpers;
import bp.task.BPTaskUnpackFiles;
import bp.task.BPTaskUnpackFiles.OVERWRITE_MODE;
import bp.ui.form.BPFormPanelTask;
import bp.ui.form.dynamic.BPFormContext;

public class BPFormControllerTaskUnpackFiles extends BPFormControllerTask
{
	@SuppressWarnings("unchecked")
	public <T> T decodeValue(String key, Object v, BPFormContext context)
	{
		switch (key)
		{
			case "owmode":
				return v == null ? null : (T) OVERWRITE_MODE.valueOf((String) v);
		}
		return super.decodeValue(key, v, context);
	}

	@SuppressWarnings("unchecked")
	public <T> T encodeValue(String key, Object v, BPFormContext context)
	{
		switch (key)
		{
			case "owmode":
				return (T) (v == null ? null : v.toString());
		}
		return super.encodeValue(key, v, context);
	}

	public List<OVERWRITE_MODE> listData(String key, BPFormContext context)
	{
		return Arrays.asList(BPTaskUnpackFiles.OVERWRITE_MODE.values());
	}
	
	public Object select(String key, Object oldvalue, BPFormContext context)
	{
		switch (key)
		{
			case "target":
				return onSelectResourceDirList((String) oldvalue);
			case "sourcedir":
			case "targetbase":
				return onSelectResourceDir((String) oldvalue, true);
		}
		return super.select(key, oldvalue, context);
	}

	public String render(String key, Object v, BPFormContext context)
	{
		String str = v == null ? "" : v.toString();
		return BPLocaleHelpers.translateByClass(BPFormPanelTask.class, str, "OWMODE_");
	}
}
