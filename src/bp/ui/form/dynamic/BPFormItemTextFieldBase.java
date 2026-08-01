package bp.ui.form.dynamic;

import javax.swing.JTextField;

import bp.util.ObjUtil;
import bp.util.TextUtil;

public abstract class BPFormItemTextFieldBase<TF extends JTextField> extends BPFormItemBase<TF>
{
	protected Object normalizeRenderValue(Object v)
	{
		return v == null ? "" : v;
	}

	public Object getComponentValue()
	{
		return def.convertEmptyToNull() ? getNotEmptyText() : getText();
	}

	protected abstract String getNotEmptyText();

	protected String getText()
	{
		return comp.getText();
	}

	public void setComponentValue(Object v)
	{
		comp.setText((String) v);
	}

	public boolean needCheckSTName()
	{
		return def.params == null ? false : ObjUtil.toBool(def.getParam("needstname"), false);
	}

	public boolean validateValue(BPFormContext context)
	{
		String txt = getText();
		if (txt == null || txt.length() == 0)
		{
			if (def.required)
				return false;
		}
		else if (needCheckSTName())
			if (!TextUtil.checkSTName(txt))
				return false;
		return super.validateValue(context);
	}
}
