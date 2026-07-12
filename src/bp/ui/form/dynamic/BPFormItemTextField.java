package bp.ui.form.dynamic;

import javax.swing.border.MatteBorder;

import bp.ui.scomp.BPTextField;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPFormItemTextField extends BPFormItemBase<BPTextField>
{
	protected Object normalizeRenderValue(Object v)
	{
		return v == null ? "" : v;
	}

	public Object getComponentValue()
	{
		return def.convertEmptyToNull() ? comp.getNotEmptyText() : comp.getText();
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
		String txt = comp.getText();
		if (txt.length() == 0)
		{
			if (def.required)
				return false;
		}
		else if (needCheckSTName())
			if (!TextUtil.checkSTName(txt))
				return false;
		return super.validateValue(context);
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		comp = new BPTextField();
		comp.setMonoFont();
		comp.setEditable(!itemdef.readonly);
		comp.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorderColor(context)));
		comp.setNoMeasureSize(true);
	}
}
