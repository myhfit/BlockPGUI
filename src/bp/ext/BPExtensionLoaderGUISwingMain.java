package bp.ext;

import bp.ui.frame.BPMainFrameIFC;
import bp.ui.util.UIStd;
import bp.util.Std;

public class BPExtensionLoaderGUISwingMain implements BPExtensionLoaderGUISwing
{
	public String getName()
	{
		return "GUI-Swing";
	}

	public String[] getParentExts()
	{
		return null;
	}

	public String[] getDependencies()
	{
		return null;
	}

	public void setup(BPMainFrameIFC mainframe)
	{
		Std.setupUI(UIStd::info, BPExtensionLoaderGUISwingMain::err_u, BPExtensionLoaderGUISwingMain::confirm_u, BPExtensionLoaderGUISwingMain::prompt_u);
	}

	private static boolean confirm_u(String str)
	{
		return UIStd.confirm(null, null, str);
	}

	private static void err_u(String str)
	{
		UIStd.textarea(str, "BlockP - error", false);
	}

	private static String prompt_u(String str)
	{
		return UIStd.input("", str, "");
	}
}
