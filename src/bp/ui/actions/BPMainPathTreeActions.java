package bp.ui.actions;

import javax.swing.Action;

import bp.res.BPResource;
import bp.ui.frame.BPMainFrame;
import bp.ui.util.UIUtil;

public class BPMainPathTreeActions
{
	public Action pathtree;
	public Action prjstree;
	public Action refresh;

	protected BPMainFrame m_mf;

	public BPMainPathTreeActions(BPMainFrame mf)
	{
		refresh = BPActionHelpers.getAction(BPActionConstCommon.PTREE_REFRESH, UIUtil.makeDynamicInstCB(mf, "refreshPathTree", (BPResource) null));
		pathtree = BPActionHelpers.getAction(BPActionConstCommon.PTREE_PATHTREE, UIUtil.makeDynamicInstCB(mf, "switchPathTreeFunc", 1));
		prjstree = BPActionHelpers.getAction(BPActionConstCommon.PTREE_PRJTREE, UIUtil.makeDynamicInstCB(mf, "switchPathTreeFunc", 2));
	}
}