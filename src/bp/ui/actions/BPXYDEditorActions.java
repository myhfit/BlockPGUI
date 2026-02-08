package bp.ui.actions;

import javax.swing.Action;

import bp.ui.editor.BPXYDEditor;

public class BPXYDEditorActions implements BPActionHolder
{
	public Action actnewline;
	public Action actdelete;
	public Action actclone;
	protected BPXYDEditor<?> m_editor;

	public BPXYDEditorActions(BPXYDEditor<?> editor)
	{
		m_editor = editor;
		actnewline = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_NEWLINE, e -> m_editor.newLine());
		actdelete = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, e -> m_editor.delete());
		actclone = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCLONE, m_editor::showClone);
	}

	public Action[] getActions()
	{
		return new Action[] { actnewline, actdelete, BPAction.separator(), actclone, BPAction.separator() };
	}
}
