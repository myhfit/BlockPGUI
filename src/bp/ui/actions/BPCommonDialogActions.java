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

	protected BPDialogCommon m_dlg;

	public BPCommonDialogActions(BPDialogCommon dlg)
	{
		m_dlg = dlg;
		actionok = BPActionHelpers.getAction(BPActionConstCommon.DLG_OK, e -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_OK));
		actioncancel = BPActionHelpers.getAction(BPActionConstCommon.DLG_CC, e -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_CANCEL));
		actionapply = BPActionHelpers.getAction(BPActionConstCommon.DLG_APPLY, e -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_APPLY));
		actionyes = BPActionHelpers.getAction(BPActionConstCommon.DLG_YES, e -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_YES));
		actionno = BPActionHelpers.getAction(BPActionConstCommon.DLG_NO, e -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_NO));
	}
}
