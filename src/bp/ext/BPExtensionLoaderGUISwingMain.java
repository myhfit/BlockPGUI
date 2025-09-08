package bp.ext;

import java.util.Arrays;
import java.util.function.Consumer;

import bp.BPCore;
import bp.context.BPFileContext;
import bp.core.BPCommandHandlerGUICore;
import bp.ui.frame.BPMainFrameIFC;
import bp.ui.util.UIStd;
import bp.util.ObjUtil;
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

	public void install(BPFileContext context)
	{
		BPCore.addCommandHandler(new BPCommandHandlerGUICore());
	}

	public void setup(BPMainFrameIFC mainframe)
	{
		Std.setupUI(UIStd::info, BPExtensionLoaderGUISwingMain::err_u, BPExtensionLoaderGUISwingMain::confirm_u, BPExtensionLoaderGUISwingMain::prompt_u, BPExtensionLoaderGUISwingMain::select_u);
		Std.setupAdv(ObjUtil.makeMap("showdata", (Consumer<Object>) UIStd::showData));
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

	private static String select_u(String[] strs)
	{
		return UIStd.select(Arrays.asList(strs), "BlockP - select", null);
	}
}
