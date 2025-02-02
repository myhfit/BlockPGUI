package bp.ui.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import bp.ui.scomp.BPTree.BPTreeNode;

public class BPTreeCellRendererObject extends DefaultTreeCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9085242423108943790L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Object v = value;
		if (v instanceof BPTreeNode)
			v = ((BPTreeNode) v).getUserObject();
		if (v != null)
		{
			if (v.getClass().isArray())
			{
				Object[] vs = (Object[]) v;
				v = vs[0] + ":" + vs[1];
			}
		}
		return super.getTreeCellRendererComponent(tree, v, selected, expanded, leaf, row, hasFocus);
	}
}
