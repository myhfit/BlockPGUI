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
			h.onTreeEvent(BPTreeActionType.SELECT, tree, node, null);
	}

	default void onOpen(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.OPEN, tree, node, null);
	}

	default void onDelete(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.DELETE, tree, node, null);
	}

	default void onPaste(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.PASTE, tree, node, null);
	}

	default void onCopy(BPTree tree, BPTreeNode node)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.COPY, tree, node, null);
	}

	default void onAction(BPTree tree, BPTreeNode node, String extact)
	{
		BPTreeActionEventHandler h = getTreeActionHandler();
		if (h != null)
			h.onTreeEvent(BPTreeActionType.EXT, tree, node, extact);
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

	default void clearResource()
	{

	}

	default boolean isOverwriteCopy()
	{
		return false;
	}

	public static interface BPTreeActionEventHandler
	{
		void onTreeEvent(BPTreeActionType actiontype, BPTree tree, BPTreeNode node, String extact);
	}

	public static enum BPTreeActionType
	{
		SELECT, OPEN, DELETE, COPY, PASTE, EXT;

		public final static String EXT_ACT_RENAME = "rename";
		public final static String EXT_ACT_PROPERTY = "property";
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