package bp.ui.view;

import bp.project.BPResourceProject;

public interface BPProjectOverviewCompFactory<T extends BPResourceProject>
{
	BPProjectOverviewComp<T> create(T prj);

	boolean check(BPResourceProject prj);
}
