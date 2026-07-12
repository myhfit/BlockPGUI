package bp.ui.form.dynamic.controller.task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.task.BPTaskUserInput;
import bp.ui.form.dynamic.BPFormContext;

public class BPFormControllerTaskUserInput extends BPFormControllerTask
{
	public List<?> listData(String key, BPFormContext context)
	{
		switch (key)
		{
			case "vtype":
				return new CopyOnWriteArrayList<>(new String[] { BPTaskUserInput.S_VTYPE_SV, BPTaskUserInput.S_VTYPE_JSON });
			case "vcast":
				return new CopyOnWriteArrayList<>(new String[] { "", Integer.class.getName(), Double.class.getName() });
		}
		return super.listData(key, context);
	}
}
