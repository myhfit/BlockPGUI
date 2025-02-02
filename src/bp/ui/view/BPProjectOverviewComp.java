package bp.ui.view;

import bp.project.BPResourceProject;

public interface BPProjectOverviewComp<T extends BPResourceProject>
{
	void setup(T prj);
}
