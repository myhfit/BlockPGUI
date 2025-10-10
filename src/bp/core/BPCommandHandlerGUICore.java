package bp.core;

import bp.BPCore;
import bp.BPGUICore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.util.CommonUIOperations;
import bp.util.LogicUtil;
import bp.util.ObjUtil;

public class BPCommandHandlerGUICore extends BPCommandHandlerBase implements BPCommandHandler
{
	public final static String CN_RES_OPEN = "res_open";
	public final static String CN_FILE_OPEN = "file_open";

	public BPCommandHandlerGUICore()
	{
		m_cmdnames = ObjUtil.makeList(CN_RES_OPEN, CN_FILE_OPEN);
	}

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toLowerCase();

		switch (cmdname)
		{
			case CN_RES_OPEN:
			{
				return openResource(getPSStringArr(cmd.ps));
			}
			case CN_FILE_OPEN:
			{
				return openFile(getPSStringArr(cmd.ps));
			}
		}
		return null;
	}

	protected BPCommandResult openResource(String[] ps)
	{
		if (ps[0] == null || ps[0].length() == 0)
			return null;
		BPResource res = BPCore.getFileContext().getRes(ps[0]);
		BPFormat format = LogicUtil.PAR_NN(ps.length > 1 ? ps[1] : null, fstr -> fstr != null ? BPFormatManager.getFormatByName((String) fstr) : null);
		BPEditorFactory fac = LogicUtil.PAR_NN(ps.length > 2 ? ps[2] : null, facstr -> facstr != null ? BPEditorManager.getFactory(ps.length > 1 ? ps[1] : null, (String) facstr) : null);
		if (res != null)
		{
			if (!BPGUICore.checkMainFrameVisible())
				CommonUIOperations.openResourceNewWindow(res, format, fac, null, null);
			else
				BPGUICore.runOnMainFrame(mf -> mf.openResource(res, format, fac, false, null));
			return BPCommandResult.OK(null);
		}
		return null;
	}

	protected BPCommandResult openFile(String[] ps)
	{
		if (ps == null || ps[0] == null || ps[0].length() == 0)
			return null;
		String filename = ps[0];
		if (filename != null)
		{
			if (!BPGUICore.checkMainFrameVisible())
				CommonUIOperations.openFileNewWindow(filename, null, null, null);
			else
				BPGUICore.runOnMainFrame(mf -> mf.openEditorByFileSystem(filename, null, null, null));
			return BPCommandResult.OK(null);
		}
		return null;
	}

	public String getName()
	{
		return "GUICore";
	}

}
