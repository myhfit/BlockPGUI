package bp.ui.form.dynamic;

import bp.ui.editor.BPCodePanel;
import bp.ui.scomp.BPTextPane;

public class BPFormItemTextArea extends BPFormItemWrapped<BPTextPane,BPCodePanel>
{
	protected Object normalizeRenderValue(Object v)
	{
		return v == null ? "" : v;
	}

	public int getLineHeight(BPFormContext context)
	{
		return 0;
	}

	public Object getComponentValue()
	{
		return comp.getText();
	}

	public void setComponentValue(Object v)
	{
		comp.setText((String) v);
	}

	public boolean noLabel()
	{
		return true;
	}

	public boolean validateValue(BPFormContext context)
	{
		String txt = comp.getText();
		if (def.required && txt.length() == 0)
			return false;
		return super.validateValue(context);
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		stcomp = new BPCodePanel();
		comp = stcomp.getTextPanel();
		comp.setEditable(!itemdef.readonly);
	}
}