package bp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.ui.dialog.BPDialogGate;
import bp.util.ClassUtil;
import bp.util.ClassUtil.BPExtClassLoader;
import bp.util.FileUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPGUILauncher
{
	public final static String KEY_SHOWLAUNCHER = "show_launcher";

	public final static void main(String[] args)
	{
		File f = new File(".bpenvcfgs");
		Map<String, String> envs = null;
		String laststr = null;
		if (f.exists() && f.isFile())
		{
			byte[] bs = FileUtil.readFile(".bpenvcfgs");
			if (bs != null)
			{
				String str = TextUtil.toString(bs, "utf-8");
				envs = TextUtil.getPlainMap(str);
				laststr = str;
			}
		}
		if (envs == null || !("false".equals(envs.get(KEY_SHOWLAUNCHER))))
		{
			if (envs == null)
			{
				envs = new HashMap<String, String>();
			}
			envs.put("java.home", System.getProperty("java.home"));
			BPDialogGate frame = new BPDialogGate();
			frame.setupByEnvs(envs);
			frame.setVisible(true);
			envs = frame.getResult();
			if (envs != null)
				saveConfigs(envs, laststr);
			frame = null;
		}
		if (envs != null)
			start(envs);
	}

	protected final static void saveConfigs(Map<String, String> envs, String laststr)
	{
		String workspace = envs.get("workspace");
		String recentws = envs.get("recentworkspaces");

		List<String> rwss = TextUtil.splitTextToList(recentws, ",");
		if (!rwss.contains(workspace))
		{
			rwss.add(0, workspace);
		}
		else
		{
			rwss.remove(workspace);
			rwss.add(0, workspace);
		}
		while (rwss.size() > 10)
			rwss.remove(rwss.size() - 1);
		String wsstr = TextUtil.join(rwss, ",");
		envs.put("recentworkspaces", wsstr);
		String newenvstr = TextUtil.fromPlainMap(ObjUtil.toPlainMap(envs), null).trim();
		if (!newenvstr.equals(laststr))
		{
			Std.info("EnvConfigs Saved");
			byte[] bs = TextUtil.fromString(newenvstr, "utf-8");
			if (bs != null)
			{
				FileUtil.writeFile(".bpenvcfgs", bs);
			}
		}
	}

	public final static void start(Map<String, String> envs)
	{
		if (envs == null)
			return;
		String workspace = envs.get("workspace");
		String extjarstr = envs.get("extensionjars");
		if (extjarstr != null && extjarstr.length() > 0)
		{
			BPExtClassLoader cloader = ClassUtil.getExtensionClassLoader();
			String[] jars = extjarstr.split(",");
			for (String jar : jars)
			{
				cloader.addExtURL("exts/" + jar);
			}
		}
		String[] args = null;
		if (workspace != null && workspace.length() > 0)
		{
			args = new String[] { workspace };
		}
		else
		{
			args = new String[] {};
		}
		BPGUIMain.main(args);
	}
}
