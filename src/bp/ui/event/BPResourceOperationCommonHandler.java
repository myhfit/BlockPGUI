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
					{
						BPResource[] ress = event.getSelectedResources();
						for (BPResource res : ress)
						{
							if (res.isProjectResource())
							{
								if (res.isFileSystem() && res.isLocal())
								{
									CommonUIOperations.openExternal((BPResourceFileSystemLocal) ((BPResourceProject) res).getDir());
								}
							}
							else if (res.isFileSystem() && res.isLocal())
							{
								CommonUIOperations.openExternal((BPResourceFileSystemLocal) res);
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
