package bp.ui.editor;

public interface BPPathSelector
{
	Object[][] getSelectedPaths();

	String getPathType();

	Object[] getResourcesUnder(Object path);

	<T> T getRootData();
}
