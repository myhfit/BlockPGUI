package bp.ui.dialog;

import java.util.function.Predicate;

import bp.res.BPResource;

public interface BPDialogSelectResource
{
	BPDialogSelectResource switchPathTreeFunc(int func);

	BPDialogSelectResource setSelectType(SELECTTYPE flag);

	BPDialogSelectResource setScopes(SELECTSCOPE... scopes);

	default BPDialogSelectResource setScope(SELECTSCOPE scope)
	{
		return setScopes(scope);
	}

	BPDialogSelectResource setCheckExist(CHECKEXITFLAG flag);

	BPDialogSelectResource setMultiSelect(boolean flag);

	BPDialogSelectResource setFilter(Predicate<BPResource> filter);

	BPDialogSelectResource setFilterWithExts(String[] exts);

	BPDialogSelectResource setTargetFilter(Predicate<BPResource> filter);

	BPDialogSelectResource setPreSelectedResource(BPResource res);

	void setTitle(String title);

	public static enum SELECTTYPE
	{
		FILE, DIR, ALL
	}

	public static enum SELECTSCOPE
	{
		WORKSPACE, PROJECT, COMPUTER, SPECIAL
	}

	public static enum CHECKEXITFLAG
	{
		DONOTHING, CONFIRMOVERWRITE, BLOCKNOTEXIST
	}
}
