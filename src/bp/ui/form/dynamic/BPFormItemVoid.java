package bp.ui.form.dynamic;

import bp.ui.scomp.BPLabel;

public class BPFormItemVoid extends BPFormItemBase<BPLabel>
{
	public Object getComponentValue()
	{
		return null;
	}

	public void setComponentValue(Object v)
	{
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		BPLabel lbl = new BPLabel();
		lbl.setMonoFont();
		lbl.setText("[ITEM_NOT_FOUNT]:" + itemdef.key);
		comp = lbl;
	}
}
