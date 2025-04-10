package bp.ui.event;

import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.res.BPResourceFileSystemLocal;
import bp.ui.actions.BPFileActions;
import bp.ui.util.CommonUIOperations;

public class BPResourceOperationCommonHandler
{
	public final static void onResourceOperationEvent(BPEventUIResourceOperation event)
	{
		switch (event.subkey)
		{
			case BPEventUIResourceOperation.RES_ACTION:
			{
				switch (event.getActionName())
				{
					case BPFileActions.ACTION_OPEN:
					{
						BPResource[] ress = event.getSelectedResources();
						for (BPResource res : ress)
						{
							CommonUIOperations.openResourceNewWindow(res, null, null, null, null);
						}
						break;
					}
					case BPFileActions.ACTION_PROPERTIES:
					{
						BPResource[] ress = event.getSelectedResources();
						for (BPResource res : ress)
						{
							CommonUIOperations.showProperty(res, (BPResource) null);
						}
						break;
					}
					case BPFileActions.ACTION_NEWFILE:
					{
						BPResource res = event.getSelectedResource();
						CommonUIOperations.showNewFile(res);
						break;
					}
					case BPFileActions.ACTION_NEWDIR:
					{
						BPResource res = event.getSelectedResource();
						CommonUIOperations.showNewDirectory(res, null);
						break;
					}
					case BPFileActions.ACTION_OPENEXTERNAL_SYSTEM:
					case BPFileActions.ACTION_EDITEXTERNAL_SYSTEM:
					case BPFileActions.ACTION_PRINTEXTERNAL_SYSTEM:
					{
						BPResource[] ress = event.getSelectedResources();
						for (BPResource res : ress)
						{
							BPResourceFileSystemLocal fres = null;
							if (res.isProjectResource())
							{
								if (res.isFileSystem() && res.isLocal())
								{
									fres = (BPResourceFileSystemLocal) ((BPResourceProject) res).getDir();
								}
							}
							else if (res.isFileSystem() && res.isLocal())
							{
								fres = (BPResourceFileSystemLocal) res;
							}
							if (fres != null)
							{
								switch (event.getActionName())
								{
									case BPFileActions.ACTION_OPENEXTERNAL_SYSTEM:
										CommonUIOperations.openExternal(fres);
										break;
									case BPFileActions.ACTION_EDITEXTERNAL_SYSTEM:
										CommonUIOperations.editExternal(fres);
										break;
									case BPFileActions.ACTION_PRINTEXTERNAL_SYSTEM:
										CommonUIOperations.printExternal(fres);
										break;
								}
							}
						}
						break;
					}
					case BPFileActions.ACTION_DELETE:
					{
						CommonUIOperations.deleteResources(event.getSelectedResources());
						break;
					}
					case BPFileActions.ACTION_RENAME:
					{
						CommonUIOperations.showRenameResource(event.getSelectedResource());
						break;
					}
				}
				break;
			}
		}
	}
}
