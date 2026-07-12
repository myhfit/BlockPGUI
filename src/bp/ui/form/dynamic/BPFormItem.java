package bp.ui.form.dynamic;

import java.awt.Component;
import java.util.Map;

import bp.typeext.Traversable;

public interface BPFormItem extends Traversable
{
	BPFormItemDef getDefine();

	String getLabel();

	boolean validateValue(BPFormContext context);

	Component getComponent();

	default Component getSTComponent()
	{
		return getComponent();
	}

	default boolean noLabel()
	{
		return false;
	}

	void assembleFormValue(Map<String, Object> result);

	Object getValue(Map<String, ?> data);

	BPFormItem setup(BPFormItemDef itemdef, BPFormContext context);

	void initComponent(BPFormContext context);

	Object getComponentValue();

	void setComponentValue(Object v);

	default int getLineHeight(BPFormContext context)
	{
		return context.lineheight;
	}
}
