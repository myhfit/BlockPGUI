package bp.ui.form.dynamic;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.border.MatteBorder;

import bp.ui.actions.BPActionConstCommon;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
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
		if ("textfield_more".equals(def.itemtype))
			rc.addMoreBtnAuto(this::onMore).setPreferredSize(new Dimension(context.lineheight, context.lineheight));
		else if ("textfield_area".equals(def.itemtype))
			rc.addMoreBtnAuto(this::onInput).setPreferredSize(new Dimension(context.lineheight, context.lineheight));

		stcomp = rc;
		comp = txt;
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
