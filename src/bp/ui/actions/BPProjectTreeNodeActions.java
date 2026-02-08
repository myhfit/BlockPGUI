package bp.ui.actions;

import bp.res.BPResource;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPTreeComponent;
import bp.ui.util.EventUtil;

public class BPProjectTreeNodeActions extends BPTreeNodeActions
{
	public final static String ACTION_PRJ_OVERVIEW = "prj_overview";
	public final static String ACTION_PRJ_STATISTICS = "prj_stats";

	public BPProjectTreeNodeActions()
	{
	}

	public BPAction getOverviewAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOVERVIEW, new EventUtil.EventConsumerNodeAction(new BPResource[] { res }, channelid, ACTION_PRJ_OVERVIEW));
	}

	public BPAction getStatsAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUSTATISTICS, new EventUtil.EventConsumerNodeAction(new BPResource[] { res }, channelid, ACTION_PRJ_STATISTICS));
	}
}
