package bp.ui.util;

import java.util.function.Supplier;

import javax.swing.Action;

public class SystemUIUtil
{
	private volatile static Supplier<FileAssocActionBuilder> S_FAAB_FAC;

	public final static void registerFAAB_Fac(Supplier<FileAssocActionBuilder> faab_fac)
	{
		S_FAAB_FAC = faab_fac;
	}

	public final static FileAssocActionBuilder getFileAssocActionBuilder(String ext)
	{
		FileAssocActionBuilder rc = null;
		Supplier<FileAssocActionBuilder> faabfac = S_FAAB_FAC;
		if (faabfac != null)
		{
			rc = faabfac.get();
			rc.setExt(ext);
		}
		return rc;
	}

	public static interface FileAssocActionBuilder extends Supplier<Action[]>
	{
		void setExt(String ext);
	}
}
