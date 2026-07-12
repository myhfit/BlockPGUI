package bp.ui.actions;

import javax.swing.Action;

import bp.ui.dialog.BPDialogCommon;

public class BPCommonDialogActions
{
	public Action actionok;
	public Action actioncancel;
	public Action actionyes;
	public Action actionno;
	public Action actionapply;

	public BPCommonDialogActions(BPDialogCommon dlg)
	{
		actionok = BPActionHelpers.getAction(BPActionConstCommon.DLG_OK, dlg.makeCallCommonActionCB(BPDialogCommon.COMMAND_OK));
		actioncancel = BPActionHelpers.getAction(BPActionConstCommon.DLG_CC, dlg.makeCallCommonActionCB(BPDialogCommon.COMMAND_CANCEL));
		actionapply = BPActionHelpers.getAction(BPActionConstCommon.DLG_APPLY, dlg.makeCallCommonActionCB(BPDialogCommon.COMMAND_APPLY));
		actionyes = BPActionHelpers.getAction(BPActionConstCommon.DLG_YES, dlg.makeCallCommonActionCB(BPDialogCommon.COMMAND_YES));
		actionno = BPActionHelpers.getAction(BPActionConstCommon.DLG_NO, dlg.makeCallCommonActionCB(BPDialogCommon.COMMAND_NO));
	}
}
