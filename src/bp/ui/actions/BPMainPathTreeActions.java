package bp.ui.actions;

import javax.swing.Action;

import bp.ui.frame.BPMainFrame;

public class BPMainPathTreeActions
{
	public Action pathtree;
	public Action prjstree;
	public Action refresh;

	protected BPMainFrame m_mf;

	public BPMainPathTreeActions(BPMainFrame mf)
	{
		m_mf = mf;
		refresh = BPActionHelpers.getAction(BPActionConstCommon.PTREE_REFRESH, e -> m_mf.refreshPathTree(null));
		pathtree = BPActionHelpers.getAction(BPActionConstCommon.PTREE_PATHTREE, e -> m_mf.switchPathTreeFunc(1));
		prjstree = BPActionHelpers.getAction(BPActionConstCommon.PTREE_PRJTREE, e -> m_mf.switchPathTreeFunc(2));
	}
}