package bp.ui.actions;

import javax.swing.Action;

import bp.ui.frame.BPMainFrame;
import bp.ui.res.icon.BPIconResV;

public class BPMainPathTreeActions
{
	public Action pathtree;
	public Action prjstree;
	public Action refresh;

	protected BPMainFrame m_mf;

	public BPMainPathTreeActions(BPMainFrame mf)
	{
		m_mf = mf;
		refresh = BPAction.build("Refresh").vIcon(BPIconResV.REFRESH()).callback((e) -> m_mf.refreshPathTree(null)).tooltip("Refresh").getAction();
		pathtree = BPAction.build("PathTree").vIcon(BPIconResV.PATHTREE()).callback((e) -> m_mf.switchPathTreeFunc(1)).tooltip("Switch To Path Tree").getAction();
		prjstree = BPAction.build("ProjectsTree").vIcon(BPIconResV.PRJSTREE()).callback((e) -> m_mf.switchPathTreeFunc(2)).tooltip("Switch To Projects Tree").getAction();
	}
}
