package bp.ui.util;

import bp.env.BPEnvEditorAssocOverride;
import bp.env.BPEnvManager;
import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;

public class EditorUtil
{
	public final static String getOverrideFacName(BPResource res)
	{
		String ext = res.getExt();
		if (ext != null && ext.length() > 0)
		{
			String facname = BPEnvManager.getEnvValue(BPEnvEditorAssocOverride.ENV_NAME_EA, "OPEN_WITH" + ext);
			if (facname != null)
				return facname;
		}
		if (res.canOpen() && res.isFileSystem())
		{
			BPResourceFileSystem fs = (BPResourceFileSystem) res;
			if (fs.isFile())
			{
				BPResourceFile f = (BPResourceFile) fs;
				long s = f.getSize();
				if (s > 0x20FFFFF)
					return "Raw Editor";
				else
					return null;
			}
		}
		return null;
	}

	public final static String getDefaultFileFacName()
	{
		String rc = null;
		String v = BPEnvManager.getEnvValue(BPEnvEditorAssocOverride.ENV_NAME_EA, BPEnvEditorAssocOverride.ENVKEY_OPEN_WITH_F);
		if (v != null)
		{
			v = v.trim();
			if (v.length() > 0)
				rc = v;
		}
		return rc;
	}
}
