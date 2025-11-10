package bp.ui.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import bp.ui.scomp.BPTree.BPTreeNode;
import bp.util.ObjUtil;

public class BPTreeCellRendererObject extends DefaultTreeCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9085242423108943790L;

	protected int m_datacount = 4;
	protected int m_datalen = 200;

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

			v = ObjUtil.toString(v, "", m_datacount);
			if (((String) v).length() > m_datalen)
				v = ((String) v).substring(0, m_datalen) + "...";
		}
		return super.getTreeCellRendererComponent(tree, v, selected, expanded, leaf, row, hasFocus);
	}

	public void setDataCount(int c)
	{
		m_datacount = c;
	}

	public void setDataLength(int l)
	{
		m_datalen = l;
	}
}
