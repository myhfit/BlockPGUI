package bp.ui.tree;

import java.awt.Component;
import java.util.List;
import java.util.function.Function;

import javax.swing.Action;

import bp.ui.BPComponent;
import bp.ui.scomp.BPTree.BPTreeNode;

public interface BPTreeComponent<C extends Component> extends BPComponent<C>
{
	void setTreeFuncs(BPTreeFuncs funcs);

	default BPComponentType getComponentType()
	{
		return BPComponentType.TREE;
	}

	BPTreeNode getSelectedNode();

	Object getSelectedNodeUserObject();

	Object[] getSelectedNodeUserObjectPath();

	Object[][] getSelectedNodeUserObjectPaths();

	Object[] getSelectedNodePath();

	<T> T[] getSelectedLeafs(Class<T> leafcls);

	void setContextActionFixer(Function<List<Action>, List<Action>> fixer);
}
