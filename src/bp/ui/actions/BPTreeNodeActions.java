package bp.ui.actions;

import java.awt.event.KeyEvent;

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
		return BPAction.build("Open").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENRES)).mnemonicKey(KeyEvent.VK_O).getAction();
	}

	public BPAction getOpenResourceAsAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Open As...").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENRESAS)).mnemonicKey(KeyEvent.VK_A).getAction();
	}
}
