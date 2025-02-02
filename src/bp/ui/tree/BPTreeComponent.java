package bp.ui.tree;

import java.awt.Component;

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
}
