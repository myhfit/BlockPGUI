package bp.ui.form.dynamic;

import javax.swing.JPanel;

import bp.ui.scomp.BPCheckBox;
import bp.util.ObjUtil;

public class BPFormItemCheckBox extends BPFormItemWrapped<BPCheckBox, JPanel>
{
	protected Object normalizeRenderValue(Object v)
	{
		return ObjUtil.toBool(v, false);
	}

	public Object getComponentValue()
	{
		return comp.isSelected();
	}

	public void setComponentValue(Object v)
	{
		comp.setSelected((Boolean) v);
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		comp = new BPCheckBox();
		comp.setLabelFont();
		comp.setEnabled(!itemdef.readonly);
		stcomp = wrapSingleLineComponent(comp, context);
	}
}
