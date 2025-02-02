package bp.ui.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

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
		actionok = BPAction.build("OK").callback((e) -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_OK)).mnemonicKey(KeyEvent.VK_O).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK)).getAction();
		actioncancel = BPAction.build("Cancel").callback((e) -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_CANCEL)).mnemonicKey(KeyEvent.VK_C).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)).getAction();
		actionapply = BPAction.build("Apply").callback((e) -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_APPLY)).mnemonicKey(KeyEvent.VK_A).getAction();
		actionyes = BPAction.build("Yes").callback((e) -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_YES)).mnemonicKey(KeyEvent.VK_Y).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)).getAction();
		actionno = BPAction.build("No").callback((e) -> m_dlg.callCommonAction(BPDialogCommon.COMMAND_NO)).mnemonicKey(KeyEvent.VK_N).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)).getAction();
	}
}
