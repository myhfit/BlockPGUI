package bp.ui.tree;

import java.util.List;
import java.util.function.BiFunction;

import javax.swing.Action;

import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.util.LogicUtil.WeakRefGo;

public abstract class BPTreeFuncsAbstract implements BPTreeFuncs
{
	protected WeakRefGo<BiFunction<BPTreeComponent<BPTree>, BPTreeNode, List<Action>>> m_cahandlerref;
	protected WeakRefGo<BPTreeActionEventHandler> m_tahandlerref;
	
	public BPTreeFuncsAbstract()
	{
		m_cahandlerref=new WeakRefGo<>();
		m_tahandlerref=new WeakRefGo<>();
	}

	public void installContextActionHandler(BiFunction<BPTreeComponent<BPTree>, BPTreeNode, List<Action>> handler)
	{
		m_cahandlerref.setTarget(handler);
	}

	public void installTreeActionHandler(BPTreeActionEventHandler handler)
	{
		m_tahandlerref.setTarget(handler);
	}

	public BiFunction<BPTreeComponent<BPTree>, BPTreeNode, List<Action>> getContextActionHandler()
	{
		return m_cahandlerref.get();
	}

	public BPTreeActionEventHandler getTreeActionHandler()
	{
		return m_tahandlerref.get();
	}
}
