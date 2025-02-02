package bp.ui.tree;

import bp.context.BPFileContext;

public interface BPPathTreeFuncs extends BPTreeFuncs
{
	void setup(BPFileContext base);
	
	String getRootPath();
}
