package bp.ui.view;

import bp.project.BPResourceProject;

public class BPProjectOverviewCompFactoryCommon implements BPProjectOverviewCompFactory<BPResourceProject>
{
	public BPProjectOverviewComp<BPResourceProject> create(BPResourceProject prj)
	{
		BPProjectOverviewPanelCommon rc = new BPProjectOverviewPanelCommon();
		rc.setup(prj);
		return rc;
	}

	public boolean check(BPResourceProject prj)
	{
		return true;
	}
}
