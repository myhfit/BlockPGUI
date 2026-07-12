package bp.ui.form.dynamic.controller.task;

import java.util.Map;

import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.BPFormItemDef;

public class BPFormControllerTaskProxy extends BPFormControllerTask
{
	public boolean showData(Map<String, ?> data, boolean editable, BPFormContext context)
	{
//		context.labelwidth = 120;
//		String[] keys = sortKeys(data);
//		context.createItemDefs(keys, "textfield", true);
		
		context.addItemDef(BPFormItemDef.createSimple("test", "textfield", editable));
		return true;
	}
}
