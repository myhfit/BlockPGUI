package bp.ui.tree;

import java.util.List;
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

	default void onSelect(BPTree tree, BPTreeNode node)
	{

	}

	default void onOpen(BPTree tree, BPTreeNode node)
	{

	}
	
	default void setTreeFilter(Predicate<?> filter)
	{
		
	}

	default List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
	{
		return null;
	}
}