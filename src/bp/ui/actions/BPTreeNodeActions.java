package bp.ui.actions;

import bp.res.BPResource;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPTreeComponent;
import bp.ui.util.EventUtil;

public class BPTreeNodeActions
{
	public final static String ACTION_OPENRES = "openres";
	public final static String ACTION_OPENRESAS = "openresas";

	public BPAction getOpenResourceAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPEN, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENRES));
	}

	public BPAction getOpenResourceAsAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENAS, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENRESAS));
	}
}