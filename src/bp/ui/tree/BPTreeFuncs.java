package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.swing.Action;

import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;

public interface BPTreeFuncs
{
	public List<?> getRoots();

	default List<?> getChildren(BPTreeNode node)
	{
		return getChildren(node, false);
	}

	public List<?> getChildren(BPTreeNode node, boolean isdelta);

	public boolean isLeaf(BPTreeNode node);

	default void initNode(BPTreeNode node, Object userobj)
	{

	}

	default void onSelect(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.SELECT, tree, node);
	}

	default void onOpen(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.OPEN, tree, node);
	}

	default void onDelete(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.DELETE, tree, node);
	}

	default void setTreeFilter(Predicate<?> filter)
	{

	}

	default List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
	{
		BiFunction<BPTreeComponent<BPTree>, BPTreeNode, List<Action>> h = getContextActionHandler();
		if (h != null)
			return h.apply(tree, node);
		return null;
	}

	default void setTreePathText(String str)
	{

	}

	default BiFunction<BPTreeComponent<BPTree>, BPTreeNode, List<Action>> getContextActionHandler()
	{
		return null;
	}

	default BPTreeActionEventHandler getTreeActionHandler()
	{
		return null;
	}

	public static interface BPTreeActionEventHandler
	{
		void onTreeEvent(BPTreeActionType actiontype, BPTree tree, BPTreeNode node);
	}

	public static enum BPTreeActionType
	{
		SELECT, OPEN, DELETE
	}

	public final static class BPTreeFuncsVoid implements BPTreeFuncs
	{
		public List<?> getRoots()
		{
			return new ArrayList<>();
		}

		public List<?> getChildren(BPTreeNode node, boolean isdelta)
		{
			return new ArrayList<>();
		}

		public boolean isLeaf(BPTreeNode node)
		{
			return true;
		}
	}
}