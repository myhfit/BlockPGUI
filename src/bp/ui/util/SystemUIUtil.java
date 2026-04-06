package bp.ui.util;

import java.awt.Component;
import java.awt.Window;
import java.util.function.Supplier;

import javax.swing.Action;

import bp.BPCore;
import bp.service.BPServiceFreeCall;
import bp.service.BPServiceManager;
import bp.util.ClassUtil;
import bp.util.SystemUtil;

public class SystemUIUtil
{
	private volatile static Supplier<FileAssocActionBuilder> S_FAAB_FAC;
	private volatile static Boolean S_WIN_DARK;
	private volatile static boolean S_DARK;

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

	public final static void setByDarkTheme(boolean flag)
	{
		S_DARK = flag;
	}

	public final static boolean isWinDark()
	{
		if (!S_DARK)
			return false;
		if (S_WIN_DARK == null)
		{
			if (SystemUtil.getOS().isWindows() && SystemUtil.checkOSVersion(10, 0) && BPCore.checkExtension("Windows"))
			{
				S_WIN_DARK = true;
			}
			else
			{
				S_WIN_DARK = false;
			}
		}
		return S_WIN_DARK;
	}

	public final static void setWinDarkCaption(Window w)
	{
		BPServiceFreeCall service = BPServiceManager.get("WindowsUtil");
		long hwnd = service.call("getComponentHWND", w);
		if (hwnd != 0)
			service.call("setImmersiveDarkMode", hwnd);
	}

	public final static long getWindowHWND(Window w)
	{
		return ClassUtil.callMethod(ClassUtil.getTClass("com.sun.jna.Native", ClassUtil.getExtensionClassLoader()), "getComponentID", new Class<?>[] { Component.class }, null, false, w);
	}

	public final static void initWindow(Window w)
	{
		if (isWinDark())
		{
			setWinDarkCaption(w);
		}
	}
}
