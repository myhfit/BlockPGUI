package bp.ui.form.dynamic;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.border.MatteBorder;

import bp.ui.actions.BPActionConstCommon;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.scomp.BPToolSQButton;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;

public class BPFormItemTextFieldPane extends BPFormItemTextField
{
	public BPTextFieldPane stcomp;

	public Component getSTComponent()
	{
		return stcomp;
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		BPTextFieldPane rc = new BPTextFieldPane();
		BPTextField txt = rc.getTextComponent();
		txt.setMonoFont();
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
			case "textfield_more":
				btn = p.addMoreBtnAuto(this::onMore);
				break;
			case "textfield_area":
				btn = p.addMoreBtnAuto(this::onInput);
				break;
		}
		if (btn != null)
			btn.setPreferredSize(new Dimension(context.lineheight, context.lineheight));
	}

	protected String onInput(String str)
	{
		if (!comp.isEditable())
			return null;
		return UIStd.textarea(str, UIUtil.wrapBPTitle(BPActionConstCommon.TXT_EDIT), true);
	}

	protected String onMore(String str)
	{
		if (!comp.isEditable())
			return null;
		BPFormContext context = getContext();
		if (context == null)
			return null;
		return (String) context.controller.select(def.key, str, context);
	}
}
