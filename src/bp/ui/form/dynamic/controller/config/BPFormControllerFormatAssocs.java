package bp.ui.form.dynamic.controller.config;

import java.util.Map;

import bp.format.BPFormat;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.BPFormItem;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.ui.form.dynamic.controller.BPFormControllerCommon;

public class BPFormControllerFormatAssocs extends BPFormControllerCommon implements BPFormController
{
	public boolean showData(Map<String, ?> data, boolean editable, BPFormContext context)
	{
		context.labelwidth = 120;
		String[] keys = sortKeys(data);
		context.createItemDefs(keys, "textfield", true);
		return true;
	}

	public Object controlSetValue(Object v, BPFormContext context, BPFormItem item)
	{
		BPFormat format = (BPFormat) v;
		return "[" + format.getClass().getSimpleName() + "]" + format.getName();
	}
}
