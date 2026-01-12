package bp.ui.tree;

import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.Action;

import bp.res.BPResource;
import bp.ui.scomp.BPTree;

public interface BPProjectsTreeNodeActionFactory
{
	List<Action> getActions(BPTreeComponent<BPTree> tree, BPResource res, int channelid);

	default boolean callOpen(BPTree tree, BPResource res, int channelid)
	{
		return false;
	}

	void register(BiConsumer<String, BPProjectsTreeNodeActionFactory> regfunc);
}
