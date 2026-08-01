package bp.ui.tree;

import bp.context.BPFileContext;

public interface BPPathTreeFuncs extends BPTreeFuncs
{
	void setup(BPFileContext base);

	String getRootPath();

	default boolean canLocatePath()
	{
		return false;
	}

	default void setSelectOnly(boolean flag)
	{

	}

	default boolean locatePath(BPTreeComponentBase tree, String path)
	{
		return false;
	}
}
