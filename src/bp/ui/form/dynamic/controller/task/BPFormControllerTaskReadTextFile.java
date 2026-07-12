package bp.ui.form.dynamic.controller.task;

import bp.ui.form.dynamic.BPFormContext;
import bp.ui.util.CommonUIOperations;

public class BPFormControllerTaskReadTextFile extends BPFormControllerTask
{
	public Object select(String key, Object old, BPFormContext context)
	{
		switch (key)
		{
			case "filename":
				return onSelectResourceFile((String) old);
			case "encoding":
				return CommonUIOperations.showSelectCharset((String) old);
		}
		return super.select(key, old, context);
	}
}
