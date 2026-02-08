package bp.ui.actions;

import javax.swing.Action;

import bp.ui.editor.BPTreeDataEditor;

public class BPTreeDataEditorActions implements BPActionHolder
{
	public Action actdelete;
	public Action actclone;
	public Action acteditkv;
	public Action actgrabkeys;
	protected BPTreeDataEditor<?> m_editor;

	public BPTreeDataEditorActions(BPTreeDataEditor<?> editor)
	{
		m_editor = editor;
		actdelete = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, e -> m_editor.delete());
		actclone = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCLONE, m_editor::showClone);
		acteditkv = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNEDIT, BPActionConstCommon.ACT_BTNEDIT_KV, m_editor::showEditKV);
		actgrabkeys = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNEDIT, BPActionConstCommon.ACT_BTNEDIT_GRABKEYS, m_editor::grabKeys);
	}

	public Action[] getActions()
	{
		return new Action[] { actdelete, BPAction.separator(), acteditkv, actgrabkeys, actclone, BPAction.separator() };
	}
}
