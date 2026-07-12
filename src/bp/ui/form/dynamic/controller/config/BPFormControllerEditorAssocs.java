package bp.ui.form.dynamic.controller.config;

import java.util.List;
import java.util.Map;

import bp.typeext.Nameable;
import bp.ui.editor.BPEditorFactory;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.BPFormItem;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.ui.form.dynamic.controller.BPFormControllerCommon;

public class BPFormControllerEditorAssocs extends BPFormControllerCommon implements BPFormController
{
	public boolean showData(Map<String, ?> data, boolean editable, BPFormContext context)
	{
		context.labelwidth = 120;
		String[] keys = sortKeys(data);
		context.createItemDefs(keys, "textfield", true);
		return true;
	}

	@SuppressWarnings("unchecked")
	public Object controlSetValue(Object v, BPFormContext context, BPFormItem item)
	{
		List<BPEditorFactory> facs = (List<BPEditorFactory>) v;
		return Nameable.joinName(facs, ">");
	}
}
