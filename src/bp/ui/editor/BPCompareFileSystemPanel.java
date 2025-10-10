package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.context.BPFileContext;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.event.BPEventCoreUI;
import bp.format.BPFormat;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.res.BPResourceDirLocal;
import bp.res.BPResourceFileSystem;
import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPTextField;
import bp.ui.tree.BPPathTreeEventHandler;
import bp.ui.tree.BPPathTreeLocalFuncs;
import bp.ui.tree.BPPathTreePanel;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.FileUtil;
import bp.util.IOUtil;
import bp.util.Std;
import bp.util.ZipUtil;

public class BPCompareFileSystemPanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 512016896817943676L;

	protected int m_channelid;
	protected String m_id;

	protected Component m_leftcomp;
	protected Component m_rightcomp;

	protected BPTextField m_lpath;
	protected BPTextField m_rpath;

	protected JPanel m_lp = new JPanel();
	protected JPanel m_rp = new JPanel();

	protected BPPathTreeEventHandler m_lh;
	protected BPPathTreeEventHandler m_rh;

	protected JScrollPane m_leftscroll;
	protected JScrollPane m_rightscroll;

	protected Action[] m_acts = null;

	protected Consumer<BPEventCoreUI> m_refreshtreehandler;

	public BPCompareFileSystemPanel()
	{
		JPanel sp = new JPanel();

		m_lp = new JPanel();
		m_rp = new JPanel();

		m_lpath = new BPTextField();
		m_rpath = new BPTextField();

		m_lpath.addKeyListener(new UIUtil.BPKeyListener(null, this::onLPathKeyDown, null));
		m_rpath.addKeyListener(new UIUtil.BPKeyListener(null, this::onRPathKeyDown, null));

		m_lpath.setMonoFont();
		m_rpath.setMonoFont();

		m_lpath.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_rpath.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));

		m_lp.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));

		setLayout(new BorderLayout());
		sp.setLayout(new GridLayout(1, 2, 0, 0));

		m_lp.setLayout(new BorderLayout());
		m_rp.setLayout(new BorderLayout());

		m_lp.add(m_lpath, BorderLayout.NORTH);
		m_rp.add(m_rpath, BorderLayout.NORTH);

		sp.add(m_lp);
		sp.add(m_rp);

		initActions();
		initEvents();
		add(sp, BorderLayout.CENTER);
	}

	protected void initEvents()
	{
		m_refreshtreehandler = this::onRefreshPathTree;
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_refreshtreehandler);
	}

	protected void onRefreshPathTree(BPEventCoreUI event)
	{
		Component leftcomp = m_leftcomp;
		Component rightcomp = m_rightcomp;
		if (leftcomp != null && leftcomp instanceof BPPathTreePanel)
		{
			((BPPathTreePanel) leftcomp).getCoreUIRefreshPathTreeHandler().accept(event);
		}
		if (rightcomp != null && rightcomp instanceof BPPathTreePanel)
		{
			((BPPathTreePanel) rightcomp).getCoreUIRefreshPathTreeHandler().accept(event);
		}
	}

	protected void initActions()
	{
		BPAction actcopyr = BPAction.build("Copy to Right").callback(this::copyToRight).vIcon(BPIconResV.TORIGHT()).tooltip("Copy to Right").getAction();
		BPAction actcopyl = BPAction.build("Copy to Left").callback(this::copyToLeft).vIcon(BPIconResV.TOLEFT()).tooltip("Copy to Left").getAction();

		BPAction actcopyrc = BPAction.build("Copy to Right(Clean)").callback(this::copyToRightClean).vIcon(BPIconResV.TORIGHTCLEAN()).tooltip("Copy to Right(Clean)").getAction();
		BPAction actcopylc = BPAction.build("Copy to Left(Clean)").callback(this::copyToLeftClean).vIcon(BPIconResV.TOLEFTCLEAN()).tooltip("Copy to Left(Clean)").getAction();
		m_acts = new Action[] { actcopyl, actcopyr, BPAction.separator(), actcopylc, actcopyrc };
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	protected void onLPathKeyDown(KeyEvent e)
	{
		onPathKeyDown(e, true);
	}

	protected void onRPathKeyDown(KeyEvent e)
	{
		onPathKeyDown(e, false);
	}

	protected void onPathKeyDown(KeyEvent e, boolean isleft)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			BPTextField tf = isleft ? m_lpath : m_rpath;
			String path = tf.getText();
			BPResource res = BPCore.getFileContext().getDir(path);
			if (isleft)
			{
				setLeftResource(res, null);
			}
			else
			{
				setRightResource(res, null);
			}
		}
	}

	public void focusEditor()
	{

	}

	public String getEditorInfo()
	{
		return null;
	}

	public void save()
	{
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return false;
	}

	public void setNeedSave(boolean needsave)
	{
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public void setLeftResource(BPResource res, BPFormat format)
	{
		if (m_leftcomp != null)
		{
			if (format != null && format.checkFeature(BPFormatFeature.ARCHIVE))
			{
				if (m_leftcomp instanceof BPArchivePanel)
				{
					BPArchivePanel pnl = (BPArchivePanel) m_leftcomp;
					pnl.unbind();
					BPDataContainerBase con = new BPDataContainerBase();
					con.bind(res);
					pnl.setFormat(format);
					pnl.bind(con);
					return;
				}
			}
		}
		if (m_leftcomp != null)
		{
			m_lp.remove(m_leftcomp);
			m_leftcomp = null;
		}
		if (format != null && format.checkFeature(BPFormatFeature.ARCHIVE))
		{
			BPArchivePanel pnl = new BPArchivePanel();
			BPDataContainerBase con = new BPDataContainerBase();
			con.bind(res);
			pnl.setFormat(format);
			pnl.bind(con);
			m_lp.add(pnl, BorderLayout.CENTER);
			m_leftcomp = pnl;
			m_lpath.setText(((BPResourceFileSystem) res).getFileFullName());
		}
		else
		{
			BPPathTreePanel pnl = new BPPathTreePanel();
			BPPathTreeLocalFuncs funcs = new BPPathTreeLocalFuncs();
			BPFileContext context = BPCore.getFileContext();
			String relpath = context.comparePath(((BPResourceFileSystem) res).getFileFullName());
			funcs.setSkipRoot(true);
			funcs.setup(BPCore.getFileContext(), relpath);
			pnl.setPathTreeFuncs(funcs);
			m_lp.add(pnl, BorderLayout.CENTER);
			m_lp.updateUI();
			m_leftcomp = pnl.getComponent();
		}
	}

	public void setRightResource(BPResource res, BPFormat format)
	{
		if (m_rightcomp != null)
		{
			if (format != null && format.checkFeature(BPFormatFeature.ARCHIVE))
			{
				if (m_rightcomp instanceof BPArchivePanel)
				{
					BPArchivePanel pnl = (BPArchivePanel) m_rightcomp;
					pnl.unbind();
					BPDataContainerBase con = new BPDataContainerBase();
					con.bind(res);
					pnl.setFormat(format);
					pnl.bind(con);
					return;
				}
			}
		}
		if (m_rightcomp != null)
		{
			m_rp.remove(m_rightcomp);
			m_rightcomp = null;
		}
		if (format != null && format.checkFeature(BPFormatFeature.ARCHIVE))
		{
			BPArchivePanel pnl = new BPArchivePanel();
			BPDataContainerBase con = new BPDataContainerBase();
			con.bind(res);
			pnl.setFormat(format);
			pnl.bind(con);
			m_rp.add(pnl, BorderLayout.CENTER);
			m_rightcomp = pnl;
			m_rpath.setText(((BPResourceFileSystem) res).getFileFullName());
		}
		else
		{
			BPPathTreePanel pnl = new BPPathTreePanel();
			BPPathTreeLocalFuncs funcs = new BPPathTreeLocalFuncs();
			BPFileContext context = BPCore.getFileContext();
			String relpath = context.comparePath(((BPResourceFileSystem) res).getFileFullName());
			funcs.setSkipRoot(true);
			funcs.setup(BPCore.getFileContext(), relpath);
			pnl.setPathTreeFuncs(funcs);
			m_rp.add(pnl, BorderLayout.CENTER);
			m_rp.updateUI();
			m_rightcomp = pnl.getComponent();
		}
	}

	protected void copyToLeft(ActionEvent e)
	{
		copy(true, false);
	}

	protected void copyToLeftClean(ActionEvent e)
	{
		copy(true, false);
	}

	protected void copyToRight(ActionEvent e)
	{
		copy(false, false);
	}

	protected void copyToRightClean(ActionEvent e)
	{
		copy(false, true);
	}

	public void copy(boolean istoleft, boolean isclean)
	{
		Object[][] src = null;
		Object[][] tar = null;
		src = getSelection(!istoleft);
		tar = getSelection(istoleft);
		if (src == null || tar == null)
			return;
		String srctype = getComponentType(!istoleft);
		String tartype = getComponentType(istoleft);
		Component srccomp = istoleft ? m_rightcomp : m_leftcomp;
		Component tarcomp = istoleft ? m_leftcomp : m_rightcomp;
		List<BPResource> refreshroots = new ArrayList<>();
		if (copyResources(src, tar, srctype, tartype, (BPPathSelector) srccomp, (BPPathSelector) tarcomp, isclean, refreshroots))
		{
			refreshTrees(refreshroots);
		}
	}

	public void refreshTrees(List<BPResource> refreshroots)
	{
		for (BPResource res : refreshroots)
			CommonUIOperations.refreshPathTree(res, true);
	}

	protected boolean copyResources(Object[][] srcs, Object[][] tars, String srctype, String tartype, BPPathSelector srcselector, BPPathSelector tarselector, boolean isclean, List<BPResource> refreshroots)
	{
		boolean success = false;
		try
		{
			if (srcs.length != tars.length)
			{
				UIStd.info("Selection error");
			}
			if (isclean)
			{
				if (IOUtil.PATH_TYPE_LOCALFS.equals(tartype))
				{
					for (int i = 0; i < srcs.length; i++)
					{
						Object[] tarpath = tars[i];
						Object tar = tarpath[tarpath.length - 1];
						BPResourceFileSystem tarfs = (BPResourceFileSystem) tar;
						tarfs.delete(true);
						if (tarfs.isDirectory())
							((BPResourceDirLocal) tarfs).makeDir();
					}
				}
				else
				{
				}
			}
			if (IOUtil.PATH_TYPE_LOCALFS.equals(tartype))
			{
				List<String> roots = new ArrayList<String>();
				Map<String, String> rootmap = new HashMap<String, String>();
				for (int i = 0; i < srcs.length; i++)
				{
					Object[] srcpath = srcs[i];
					Object[] tarpath = tars[i];
					Object tar = tarpath[tarpath.length - 1];
					BPResourceFileSystem tarfs = (BPResourceFileSystem) tar;
					refreshroots.add(tarfs);
					String srcroot = getPath(srcpath, srctype);
					String tarroot = tarfs.getFileFullName();
					roots.add(srcroot);
					rootmap.put(srcroot, tarroot);
				}
				if (IOUtil.PATH_TYPE_ZIP.equals(srctype))
				{
					BPDataContainer con = srcselector.getRootData();
					con.open();
					success = con.useInputStream((in) ->
					{
						return ZipUtil.readTrees(roots.toArray(new String[roots.size()]), in, (reader, root) ->
						{
							String path = reader.res;
							String tarpath = path;
							if (File.separatorChar != '/')
								tarpath = tarpath.replace('/', File.separatorChar);
							if (path != null && path.length() == 0 && !reader.isdir)
							{
								int vi = root.lastIndexOf("/");
								if (vi > -1)
									tarpath = root.substring(vi + 1);
								else
									tarpath = root;
							}
							String tarfilename = rootmap.get(root) + File.separatorChar + tarpath;
							Std.debug("Copy " + root + path + ">" + tarfilename);
							return FileUtil.copyFileByReader(rootmap.get(root), tarpath, reader);
						});
					});
				}
				else if (IOUtil.PATH_TYPE_LOCALFS.equals(srctype))
				{
					String srcroot = srcselector.getRootData();
					if (!(srcroot.endsWith("/") || srcroot.endsWith(File.separator)))
						srcroot += File.separator;
					for (String csrc : roots)
					{
						String tar = rootmap.get(csrc);
						String src = srcroot + csrc;
						File fsrc = new File(src);
						File ftar = new File(tar);
						if (fsrc.exists())
						{
							if (fsrc.isFile())
							{
								if (ftar.isDirectory())
								{
									ftar = new File(ftar, fsrc.getName());
									if (ftar.isDirectory())
									{
										Std.err(ftar.getAbsolutePath() + " unwritable");
										continue;
									}
								}
								else if (!ftar.isFile())
								{
									Std.err(ftar.getAbsolutePath() + " unwritable");
									continue;
								}
								FileUtil.copyFile(fsrc, ftar);
							}
							else if (fsrc.isDirectory())
							{
								FileUtil.copyDir(fsrc, ftar);
								Std.debug("Copy " + src + ">" + tar);
							}
						}
					}
					success = true;
				}
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return success;
	}

	protected String getPath(Object[] path, String pathtype)
	{
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		char sp = IOUtil.PATH_TYPE_LOCALFS.equals(pathtype) ? File.separatorChar : '/';
		for (Object obj : path)
		{
			if (obj == null)
				continue;
			if (!flag)
			{
				flag = true;
			}
			else
			{
				sb.append(sp);
			}
			if (obj instanceof ZipEntry)
			{
				String pname = ((ZipEntry) obj).getName();
				sb = new StringBuilder(pname);
			}
			else if (obj instanceof BPResource && ((BPResource) obj).isFileSystem())
			{
				sb.append(((BPResource) obj).getName());
			}
			else if (obj instanceof String)
			{
				if (IOUtil.PATH_TYPE_ZIP.equals(pathtype))
				{
					sb.append(obj);
				}
			}
		}

		return sb.toString();
	}

	public Object[][] getSelection(boolean isleft)
	{
		Component comp = isleft ? m_leftcomp : m_rightcomp;
		Object[][] sels = ((BPPathSelector) comp).getSelectedPaths();
		return sels;
	}

	public String getComponentType(boolean isleft)
	{
		Component comp = isleft ? m_leftcomp : m_rightcomp;
		return ((BPPathSelector) comp).getPathType();
	}

	public Action[] getActBarActions()
	{
		Action[] sacts = BPEditorActionManager.getBarActions(this);
		if (sacts == null || sacts.length == 0)
		{
			return m_acts;
		}
		else
		{
			List<Action> acts = new ArrayList<Action>();
			for (Action act : m_acts)
			{
				acts.add(act);
			}
			for (Action act : sacts)
			{
				acts.add(act);
			}
			return acts.toArray(new Action[acts.size()]);
		}
	}

	public final static class BPEditorFactoryCompareFileSystem implements BPEditorFactory
	{
		public String[] getFormats()
		{
			List<BPFormat> fs = BPFormatManager.getFormatsByFeature(BPFormatFeature.ARCHIVE);
			String[] fnames = new String[fs.size()];
			for (int i = 0; i < fs.size(); i++)
			{
				fnames[i] = fs.get(i).getName();
			}
			return fnames;
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPCompareFileSystemPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				((BPCompareFileSystemPanel) editor).setLeftResource(res, format);
			}
		}

		public String getName()
		{
			return "Compare FileSystem";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}
}