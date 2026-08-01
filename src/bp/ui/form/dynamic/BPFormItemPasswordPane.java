package bp.ui.form.dynamic;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.border.MatteBorder;

import bp.ui.scomp.BPPasswordField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.scomp.BPToolSQButton;

public class BPFormItemPasswordPane extends BPFormItemPasswordField
{
	public BPTextFieldPane stcomp;

	public Component getSTComponent()
	{
		return stcomp;
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		BPPasswordField txt=new BPPasswordField();
		BPTextFieldPane rc = new BPTextFieldPane(txt);
		txt.setMonoFont();
		txt.setNoMeasureSize(true);
		txt.setBorder(new MatteBorder(0, 1, 0, 1, getGridBorderColor(context)));
		txt.setEditable(!itemdef.readonly);
		createMoreBtn(itemdef, context, rc);
		stcomp = rc;
		comp = txt;
	}

	protected void createMoreBtn(BPFormItemDef itemdef, BPFormContext context, BPTextFieldPane p)
	{
		BPToolSQButton btn = null;
		switch (def.itemtype)
		{
			case "passfield_area":
				btn = p.addMoreBtnAuto(this::onTogglePass);
				btn.setText("A");
				break;
		}
		if (btn != null)
			btn.setPreferredSize(new Dimension(context.lineheight, context.lineheight));
	}

	protected String onTogglePass(String str)
	{
		comp.toggleEchoChar();
		return null;
	}

	public Object getComponentValue()
	{
		return def.convertEmptyToNull() ? comp.getNotEmptyText() : comp.getPasswordText();
	}

	public void setComponentValue(Object v)
	{
		comp.setText((String) v);
	}
}
