package bp.ui.form.dynamic;

import javax.swing.border.MatteBorder;

import bp.ui.scomp.BPTextField;

public class BPFormItemTextField extends BPFormItemTextFieldBase<BPTextField>
{
	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		comp = new BPTextField();
		comp.setMonoFont();
		comp.setEditable(!itemdef.readonly);
		comp.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorderColor(context)));
		comp.setNoMeasureSize(true);
	}

	protected String getNotEmptyText()
	{
		return comp.getNotEmptyText();
	}
}
