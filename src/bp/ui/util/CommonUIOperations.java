package bp.ui.util;

import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfig;
import bp.config.BPConfigSimple;
import bp.context.BPFileContext;
import bp.context.BPProjectsContext;
import bp.event.BPEventCoreUI;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.project.BPProjectItemFactory;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceDirLocal;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceFileSystemLocal;
import bp.schedule.BPSchedule;
import bp.task.BPTask;
import bp.tool.BPTool;
import bp.tool.BPToolGUI;
import bp.ui.BPComponent;
import bp.ui.container.BPRoutableContainer;
import bp.ui.dialog.BPDialogCommonCategoryView;
import bp.ui.dialog.BPDialogForm;
import bp.ui.dialog.BPDialogLocateCachedResource;
import bp.ui.dialog.BPDialogNewProject;
import bp.ui.dialog.BPDialogNewSchedule;
import bp.ui.dialog.BPDialogNewTask;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.editor.BPTextEditor;
import bp.ui.form.BPFormManager;
import bp.ui.frame.BPFrameComponent;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPTreeComponent;
import bp.util.ClassUtil;
import bp.util.FileUtil;
import bp.util.ObjUtil;
import bp.util.ScheduleUtil;
import bp.util.Std;
import bp.util.SystemUtil;

public class CommonUIOperations
{
	public final static String showOpenDirDialog(Window par, String filename)
	{
		FileDialog fd = null;
		if (par instanceof Frame)
			fd = new FileDialog((Frame) par, filename, FileDialog.LOAD);
		else if (par instanceof Dialog)
			fd = new FileDialog((Frame) par, filename, FileDialog.LOAD);
		fd.setVisible(true);
		String dir = fd.getDirectory();
		dir = dir == null ? "" : dir;
		String f = fd.getFile();
		if (f != null && f.length() > 0)
			return dir + f;
		return null;
	}

	public final static String showOpenFileDialog(Window par, String filename)
	{
		String rc = null;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
		dlg.setScope(BPDialogSelectResource2.SELECTSCOPE.COMPUTER);
		dlg.setTitle("BlockP - Select File");
		dlg.showOpen();
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			BPResourceFileSystem fres = (BPResourceFileSystem) res;
			rc = fres.getFileFullName();
		}
		return rc;
	}

	public final static String[] showOpenFilesDialog(Window par)
	{
		FileDialog fd = null;
		if (par instanceof Frame)
			fd = new FileDialog((Frame) par, "", FileDialog.LOAD);
		else if (par instanceof Dialog)
			fd = new FileDialog((Frame) par, "", FileDialog.LOAD);
		fd.setMultipleMode(true);
		fd.setVisible(true);
		File[] fs = fd.getFiles();
		if (fs != null && fs.length > 0)
		{
			String[] filenames = new String[fs.length];
			for (int i = 0; i < fs.length; i++)
			{
				filenames[i] = fs[i].getAbsolutePath();
			}
			return filenames;
		}
		return null;
	}

	public final static String showSaveFileDialog(Window par)
	{
		return showSaveFileDialog(par, null);
	}

	public final static String showSaveFileDialog(Window par, String presetfilename)
	{
		FileDialog fd = null;
		if (par instanceof Frame)
			fd = new FileDialog((Frame) par, "", FileDialog.SAVE);
		else if (par instanceof Dialog)
			fd = new FileDialog((Frame) par, "", FileDialog.SAVE);
		if (presetfilename != null)
			fd.setFile(presetfilename);
		fd.setVisible(true);
		String dir = fd.getDirectory();
		dir = dir == null ? "" : dir;
		String f = fd.getFile();
		if (f != null && f.length() > 0)
			return dir + f;
		return null;
	}

	public final static void showNewDirectory(BPResource res)
	{
		if (res != null)
		{
			if (res.isFileSystem())
			{
				if (((BPResourceFileSystem) res).isDirectory())
				{
					BPResourceDir dir = (BPResourceDir) res;
					String filename = UIStd.input(null, "Name:", "Input");
					if (filename != null && filename.length() > 0)
					{
						dir.createChild(filename, false);
						refreshPathTree(res, false);
					}
				}
			}
		}
	}

	public final static void showNewDirectory(BPResource res, BPTreeComponent<? extends BPTree> tree)
	{
		if (res == null)
		{
			Object[] respath = tree.getSelectedNodePath();
			for (int i = respath.length - 1; i >= 0; i--)
			{
				BPResource tres = (BPResource) respath[i];
				if (!tres.isLeaf())
				{
					res = tres;
					break;
				}
			}
		}
		showNewDirectory(res);
	}

	public final static void showNewFile(BPResource res)
	{
		if (res != null)
		{
			if (res.isFileSystem())
			{
				if (((BPResourceFileSystem) res).isDirectory())
				{
					BPResourceDir dir = (BPResourceDir) res;
					String filename = UIStd.input(null, "Name:", "Input");
					if (filename != null && filename.length() > 0)
					{
						try
						{
							dir.createChild(filename, true);
							refreshPathTree(res, false);
						}
						catch (RuntimeException re)
						{
							Std.err(re);
							UIStd.err(re);
						}
					}
				}
			}
		}
	}

	public final static void openFileNewWindow(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params)
	{
		try
		{
			BPResourceFileSystem res = null;
			String ext = null;
			if (filename != null && filename.length() > 0)
			{
				if (FileUtil.isDir(filename))
				{
					res = new BPResourceDirLocal(filename);
					ext = res.getExt();
				}
				else
				{
					res = new BPResourceFileLocal(filename);
					ext = res.getExt();
				}
			}
			BPEditorFactory fac = null;
			BPFormat nformat = (format != null ? BPFormatManager.getFormatByName(format) : ext == null ? null : BPFormatManager.getFormatByExt(ext));
			BPConfig options = optionsdata == null ? null : BPConfigSimple.fromData(optionsdata);
			if (facname != null)
				fac = BPEditorManager.getFactory(nformat == null ? null : nformat.getName(), facname);
			else
				fac = BPEditorManager.getFactory(nformat.getName());
			if (fac != null)
			{
				if (res == null)
				{
					res = new BPResourceFileLocal("untitiled" + (ext == null ? "" : ext));
					newResourceNewWindow(res, nformat, fac, null, options, params);
				}
				else
				{
					openResourceNewWindow(res, nformat, fac, null, options, params);
				}
			}
		}
		catch (Exception e)
		{
			UIStd.err(e);
		}
	}

	public final static void createFileNewWindow(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params)
	{
		try
		{
			BPResourceFileSystem res = null;
			String ext = null;
			if (filename != null && filename.length() > 0)
			{
				if (FileUtil.isDir(filename))
				{
					res = new BPResourceDirLocal(filename);
					ext = res.getExt();
				}
				else
				{
					res = new BPResourceFileLocal(filename);
					ext = res.getExt();
				}
			}
			BPEditorFactory fac = null;
			BPFormat nformat = (format != null ? BPFormatManager.getFormatByName(format) : ext == null ? null : BPFormatManager.getFormatByExt(ext));
			BPConfig options = optionsdata == null ? null : BPConfigSimple.fromData(optionsdata);
			if (facname != null)
				fac = BPEditorManager.getFactory(nformat == null ? null : nformat.getName(), facname);
			else
				fac = BPEditorManager.getFactory(nformat.getName());
			if (fac != null)
			{
				if (res == null)
				{
					res = new BPResourceFileLocal("untitiled" + (ext == null ? "" : ext));
					newResourceNewWindow(res, nformat, fac, null, options, params);
				}
				else
				{
					newResourceNewWindow(res, nformat, fac, null, options, params);
				}
			}
		}
		catch (Exception e)
		{
			UIStd.err(e);
		}
	}

	public final static void showBPComponentInNewWindow(BPComponent<?> comp)
	{
		if (comp.isRoutableContainer())
		{
			BPRoutableContainer<?> rcomp = (BPRoutableContainer<?>) comp;
			comp = rcomp.getCurrent();
		}
		BPFrameComponent fe = new BPFrameComponent();
		fe.setComponent(comp);
		fe.setVisible(true);
	}

	public final static void newResourceNewWindow(BPResourceFileSystem file, BPFormat _format, BPEditorFactory _fac, String routecontainerid, BPConfig options, Object... params)
	{
		String id = file.getTempID();
		if (id == null)
		{
			id = BPCore.genID(BPCore.getFileContext());
			file.setTempID(id);
		}
		BPComponent<?> comp = null;
		{
			String ext = file.getExt();
			BPFormat format = _format == null ? BPFormatManager.getFormatByExt(ext) : _format;
			BPEditorFactory fac = _fac == null ? BPEditorManager.getFactory(format.getName()) : _fac;
			BPEditor<?> editor = fac.createEditor(format, file, options, params);
			if (editor == null)
				return;
			editor.setID(id);
			fac.initEditor(editor, format, file, options);
			comp = editor;
			editor.setNeedSave(true);
			if (editor instanceof BPTextEditor)
			{
				BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
				teditor.getTextPanel().resizeDoc();
			}
			showBPComponentInNewWindow(comp);
		}
	}

	public final static void openResourceNewWindow(BPResource res, BPFormat fformat, BPEditorFactory ffac, String routecontainerid, BPConfig options, Object... params)
	{
		String id = res.openWithTempID() ? BPCore.genID(BPCore.getFileContext()) : res.getID();
		{
			String ext = res.getExt();
			BPFormat format = (fformat != null ? fformat : BPFormatManager.getFormatByExt(ext));
			BPEditorFactory fac = (ffac != null ? ffac : BPEditorManager.getFactory(format.getName()));
			if (fac == null)
			{
				UIStd.info("No Editor for " + format.getName());
				return;
			}
			BPEditor<?> editor = fac.createEditor(format, res, options, params);
			if (editor == null)
				return;
			editor.setID(id);
			fac.initEditor(editor, format, res, options);
			if (editor instanceof BPTextEditor)
			{
				BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
				teditor.getTextPanel().resizeDoc();
			}
			showBPComponentInNewWindow(editor);
		}
	}

	public final static BPResource selectResource(Window par)
	{
		return selectResource(par, false);
	}

	public final static BPResource selectResource(Window par, boolean issave)
	{
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2(par);
		if (issave)
			dlg.showSave();
		else
			dlg.showOpen();
		return dlg.getSelectedResource();
	}

	public final static BPResource selectCachedResource(Window par, String defaultkey, String filterext, boolean autosearch)
	{
		BPDialogLocateCachedResource dlg = new BPDialogLocateCachedResource();
		if (defaultkey != null)
			dlg.setDefaultResourceKey(defaultkey);
		if (filterext != null)
			dlg.setFilterExt(filterext);
		if (autosearch)
			dlg.doSearch();
		dlg.setVisible(true);
		return dlg.getSelectedResource();
	}

	public final static void showProperty(BPResource res, BPResource root)
	{
		if (res != null)
		{
			BPDialogForm dlg = new BPDialogForm();
			Class<?> cls = res.getClass();
			String clsname = ClassUtil.tryLoopSuperClass((rcls) -> BPFormManager.containsKey(rcls.getName()) ? rcls.getName() : null, cls, BPResource.class);
			if (clsname != null)
			{
				dlg.setup(clsname, res);
				dlg.setTitle("Properties:" + res.getResType());
				dlg.setVisible(true);
				Map<String, Object> data = dlg.getFormData();
				if (data != null)
				{
					res.setMappedData(data);
					if (root != null && root instanceof BPResourceProject)
					{
						if (root != res)
						{
							BPResourceProject project = (BPResourceProject) root;
							project.save(res);
						}
						if (res instanceof BPResourceProject)
						{
							((BPResourceProject) res).savePrjFile();
							BPProjectsContext prjcontext = BPCore.getProjectsContext();
							prjcontext.saveProjects();
							prjcontext.sendProjectChangedEvent();
						}
					}
				}
			}
		}
	}

	public final static BPResourceProject getSelectedProject(BPTreeComponent<? extends BPTree> tree)
	{
		Object[] objs = tree.getSelectedNodeUserObjectPath();
		BPResourceProject prj = null;
		for (int i = objs.length - 1; i >= 0; i--)
		{
			Object obj = objs[i];
			if (obj instanceof BPResourceProject)
			{
				prj = (BPResourceProject) obj;
				break;
			}
		}
		return prj;
	}

	public final static void showProperty(BPResource res, BPTreeComponent<? extends BPTree> tree)
	{
		if (res == null && tree != null)
			res = (BPResource) tree.getSelectedNodeUserObject();
		if (res != null)
			showProperty(res, (BPResource) tree.getSelectedNodeUserObjectPath()[0]);
	}

	public final static void showNewFile(BPResource res, BPTreeComponent<? extends BPTree> tree)
	{
		if (res == null)
		{
			Object[] respath = tree.getSelectedNodePath();
			if (respath == null)
				return;
			for (int i = respath.length - 1; i >= 0; i--)
			{
				BPResource tres = (BPResource) respath[i];
				if (!tres.isLeaf())
				{
					res = tres;
					break;
				}
			}
		}
		showNewFile(res);
	}

	public final static void deleteResources(BPResource[] ress)
	{
		if (ress != null && ress.length > 0)
		{
			BPResource par = ress[0].getParentResource();
			boolean flag = false;
			for (BPResource res : ress)
			{
				flag = flag | res.delete();
			}
			CommonUIOperations.refreshPathTree(par, false);
		}
	}

	public final static void refreshPathTree(BPResource res, boolean recursive)
	{
		BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshPathTree(res, recursive));
	}

	public final static void showNewTask()
	{
		BPDialogNewTask dlg = new BPDialogNewTask();
		dlg.setVisible(true);
		BPTask<?> task = dlg.getTask();
		if (task != null)
		{
			BPCore.addTask(task);
		}
	}

	public final static void showNewSchedule()
	{
		BPDialogNewSchedule dlg = new BPDialogNewSchedule();
		dlg.setVisible(true);
		BPSchedule sd = dlg.getSchedule();
		if (sd != null)
		{
			ScheduleUtil.addScheduleAndSave(sd);
		}
	}

	public final static void showNewProject()
	{
		BPFileContext context = BPCore.getFileContext();
		if (context.isProjectsContext())
		{
			BPProjectsContext prjcontext = (BPProjectsContext) context;
			BPDialogNewProject dlg = new BPDialogNewProject();
			dlg.setVisible(true);
			BPResourceProject project = dlg.getProject();
			if (project != null)
			{
				prjcontext.addProject(project);
				project.savePrjFile();
				prjcontext.saveProjects();
				prjcontext.sendProjectChangedEvent();
			}
		}
	}

	public final static void showSystemInfo()
	{
		List<String> cats = SystemUtil.getSystemInfoKeys();
		Function<String, Object> ctt = (cat) ->
		{
			Object sysinfo = SystemUtil.getSystemInfo(cat);
			Object rc = null;
			if (sysinfo != null)
				rc = ObjUtil.wrapUIData(sysinfo);
			return rc;
		};
		BPDialogCommonCategoryView<String, Object> dlg = new BPDialogCommonCategoryView<String, Object>();
		dlg.setup(cats, null, ctt, false);
		dlg.setCommandBarMode(BPDialogCommonCategoryView.COMMANDBAR_OKESCAPE);
		dlg.setTitle("BlockP - System Info");
		dlg.setVisible(true);
	}

	public final static void refreshResourceCache(BPResource res)
	{
		if (res.isFileSystem())
		{
			BPCore.FS_CACHE.invalidate(((BPResourceFileSystem) res).getFileFullName());
			BPCore.FS_CACHE.refresh();
		}
	}

	public final static void openExternal(BPResourceFileSystemLocal res)
	{
		UIStd.wrapSegE(() -> Desktop.getDesktop().open(res.getFileObject()));
	}

	public final static void editExternal(BPResourceFileSystemLocal res)
	{
		UIStd.wrapSegE(() -> Desktop.getDesktop().edit(res.getFileObject()));
	}

	public final static void printExternal(BPResourceFileSystemLocal res)
	{
		UIStd.wrapSegE(() -> Desktop.getDesktop().print(res.getFileObject()));
	}

	public final static void openExternal(URI uri)
	{
		UIStd.wrapSegE(() -> Desktop.getDesktop().browse(uri));
	}

	public final static void openWithTool(BPResource[] ress)
	{
		Map<String, List<BPTool>> toolmap = new HashMap<String, List<BPTool>>(BPGUICore.TOOL_MAP);
		List<BPToolGUI> tools = new ArrayList<BPToolGUI>();
		for (List<BPTool> ts : toolmap.values())
		{
			for (BPTool t : ts)
			{
				if (t instanceof BPToolGUI)
					tools.add((BPToolGUI) t);
			}
		}

		BPToolGUI tool = UIStd.select(tools, BPGUICore.S_BP_TITLE + " - Select Tool", t -> ((BPToolGUI) t).getName());
		if (tool != null)
		{
			tool.showTool(new Object[] { ress });
		}
	}

	public final static void showRenameResource(BPResource res)
	{
		BPResource par = res.getParentResource();
		String newname = UIStd.input(res.getName(), "New Name:", "Input new Name");
		if (newname != null)
		{
			newname = newname.trim();
			if (newname.length() > 0)
			{
				if (res.rename(newname))
				{
					CommonUIOperations.refreshPathTree(par, false);
				}
			}
		}
	}

	public final static void createProjectItem(BPResourceProject prj, BPResource par, BPProjectItemFactory fac)
	{
		BPDialogForm dlg = new BPDialogForm();
		dlg.setup(fac.getItemClassName(), new HashMap<String, Object>());
		dlg.setTitle("Create " + fac.getName());
		dlg.setVisible(true);
		Map<String, Object> data = dlg.getFormData();
		if (data != null)
		{
			try
			{
				fac.create(data, prj, par);
				refreshPathTree(par, false);
				refreshResourceCache(par);
			}
			catch (RuntimeException re)
			{
				Std.err(re);
				UIStd.err(re);
			}
		}
	}
}
