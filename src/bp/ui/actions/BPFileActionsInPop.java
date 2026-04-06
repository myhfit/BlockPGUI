package bp.ui.actions;

import bp.config.BPConfig;
import bp.format.BPFormat;
import bp.format.BPFormatDir;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.dialog.BPDialogSelectFormatEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.util.CommonUIOperations;

public class BPFileActionsInPop extends BPFileActions
{
	public BPAction getOpenFileAction(BPResource[] ress, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPEN, e -> CommonUIOperations.openResource(ress[0], null, null));
	}

	public BPAction getOpenFileAsAction(BPResource[] ress, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENAS, e ->
		{
			BPResource res0 = ress[0];
			boolean isdir = false;
			if (res0.isFileSystem() && ((BPResourceFileSystem) res0).isDirectory())
				isdir = true;
			BPFormat format = isdir ? new BPFormatDir() : BPFormatManager.getFormatByExt(res0.getExt());
			BPEditorFactory fac = null;
			BPConfig options = null;
			BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
			dlg.setFormat(format);
			dlg.setVisible(true);
			format = dlg.getSelectedFormat();
			fac = dlg.getSelectedEditorFactory();
			options = dlg.getEditorOptions();
			if (format == null && fac == null)
				return;
			CommonUIOperations.openResource(res0, format, fac, options);
		});
	}
}
