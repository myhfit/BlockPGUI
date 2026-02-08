package bp.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfig;
import bp.config.BPConfigSimple;
import bp.config.UIConfigs;
import bp.event.BPEventChannelUI;
import bp.event.BPEventCoreUI;
import bp.ext.BPExtensionLoader;
import bp.ext.BPExtensionManager;
import bp.format.BPFormat;
import bp.format.BPFormatDir;
import bp.format.BPFormatManager;
import bp.format.BPFormatProject;
import bp.format.BPFormatUnknown;
import bp.module.BPModule;
import bp.module.BPModuleManager;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceDirLocal;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceFileSystemLocal;
import bp.res.BPResourceHolder;
import bp.tool.BPTool;
import bp.ui.BPComponent;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.actions.BPFileActions;
import bp.ui.actions.BPMainFrameActions;
import bp.ui.actions.BPMainPathTreeActions;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.actions.BPProjectTreeNodeActions;
import bp.ui.actions.BPTreeNodeActions;
import bp.ui.container.BPEditors;
import bp.ui.container.BPEditors.BPEventUIEditors;
import bp.ui.container.BPRoutableContainer;
import bp.ui.container.BPRoutableContainerBase;
import bp.ui.container.BPTabBottom;
import bp.ui.dialog.BPDialogConfigs;
import bp.ui.dialog.BPDialogLocateCachedResource;
import bp.ui.dialog.BPDialogLocateProjectItem;
import bp.ui.dialog.BPDialogScriptManager;
import bp.ui.dialog.BPDialogSelectFormatEditor;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.editor.BPTextPanel;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.event.BPResourceOperationCommonHandler;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPCommandPane;
import bp.ui.scomp.BPGUIInfoPanel;
import bp.ui.scomp.BPMenu;
import bp.ui.scomp.BPMenuItem.BPMenuItemInTray;
import bp.ui.scomp.BPPopupMenuTray;
import bp.ui.scomp.BPSplitPane;
import bp.ui.scomp.BPTextPane;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.sys.BlockPMenus;
import bp.ui.tree.BPPathTreeLocalFuncs;
import bp.ui.tree.BPPathTreePanel;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.tree.BPProjectsTreeFuncs;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.EditorUtil;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.ui.view.BPProjectsOverviewPanel;
import bp.util.FileUtil;
import bp.util.Std;
import bp.util.ThreadUtil;

public class BPMainFrame extends BPFrame implements WindowListener, BPMainFrameIFC
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3724270211798161047L;

	protected BorderLayout m_layout;
	protected BPEditors m_editors;

	protected BPMainFrameActions m_actmain;
	protected BPMainPathTreeActions m_acttree;
	protected BPSplitPane m_sp;
	protected BPSplitPane m_sp2;
	protected BPPathTreePanel m_ptree;

	protected JMenuBar m_mnubar;
	protected BPMenu m_mnuedit;
	protected JPanel m_mnuactbar;
	protected BPMenu m_mnushortcuts;
	protected BPTabBottom m_bottomtab;
	protected JMenu m_mnupopscs;
	protected BPPopupMenuTray m_mnutray;

	protected BPCommandPane m_cmdpan;

	protected BPGUIInfoPanel m_editorinfo;

	protected TrayIcon m_trayicon;

	protected Consumer<BPEventUIEditors> m_editorhandler;
	protected Consumer<BPEventUIPathTree> m_pathtreehandler;
	protected Consumer<BPEventUIResourceOperation> m_resophandler;
	protected Consumer<BPEventUIMainFrame> m_mainframehandler;
	protected Consumer<BPEventCoreUI> m_refreshprjtreehandler;

	public final static String COMP_KEY_MAINDOC = "maindoc";

	protected int m_channelid;

	public BPMainFrame()
	{
	}

	protected void initBPEvents()
	{
		BPEventChannelUI channelui = new BPEventChannelUI();
		m_editorhandler = this::onEditorEvent;
		m_pathtreehandler = this::onPathTreeEvent;
		m_resophandler = this::onResourceOperationEvent;
		m_refreshprjtreehandler = this::onRefreshPrjTreeEvent;
		m_mainframehandler = this::onMainFrameEvent;
		channelui.on(BPEventUIEditors.EVENTKEY_EDITORS, m_editorhandler);
		channelui.on(BPEventUIPathTree.EVENTKEY_PATHTREE, m_pathtreehandler);
		channelui.on(BPEventUIResourceOperation.EVENTKEY_RES_OP, m_resophandler);
		channelui.on(BPEventUIMainFrame.EVENTKEY_MAINFRAME, m_mainframehandler);
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPROJECTTREE, m_refreshprjtreehandler);
		m_channelid = BPGUICore.EVENTS_UI.addChannel(channelui);
	}

	protected void initUIComponents()
	{
		((JPanel) getContentPane()).setDoubleBuffered(UIConfigs.DOUBLE_BUFFER());
		m_actmain = new BPMainFrameActions(this);
		m_acttree = new BPMainPathTreeActions(this);

		m_mnubar = new JMenuBar();
		JMenu mnufile = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILE, null));
		JMenu mnufilenew = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUFILENEW, null));
		m_mnuedit = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUEDIT, null));
		JMenu mnuview = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUVIEW, null));
		JMenu mnutool = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUTOOL, null));
		JMenu mnumainui = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUMAINUI, null));
		JMenu mnunav = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUNAV, null));
		m_mnushortcuts = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSHORTCUTS, null));
		JMenu mnuhelp = new BPMenu(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUHELP, null));

		mnufile.setName("file");
		mnutool.setName("tool");
		mnunav.setName("nav");

		m_cmdpan = new BPCommandPane();
		m_cmdpan.setVisible(false);
		m_cmdpan.setBorder(new MatteBorder(0, 1, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		// m_cmdpan.setMinimumSize(new Dimension(4000, 0));
		m_cmdpan.setPreferredSize(new Dimension(4000, 0));

		m_mnuactbar = new JPanel();
		m_editors = new BPEditors();
		m_ptree = new BPPathTreePanel();
		m_ptree.setEventChannelID(m_channelid);
		m_editors.setEventChannelID(m_channelid);
		m_bottomtab = new BPTabBottom();
		m_editorinfo = new BPGUIInfoPanel(true);
		m_editorinfo.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_bottomtab.setInfoComp(m_editorinfo);

		m_sp = new BPSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		m_sp2 = new BPSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel pmain = new JPanel();
		m_layout = new BorderLayout();

		m_compmap.put(COMP_KEY_MAINDOC, m_editors);

		m_ptree.setToolBarActions(new Action[] { m_acttree.pathtree, m_acttree.prjstree, null, m_acttree.refresh });
		m_ptree.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_ptree.setMinimumSize(new Dimension(0, 0));
		m_ptree.setPathTreeFuncs(new BPPathTreeLocalFuncs(m_channelid));
		m_acttree.pathtree.putValue(Action.SELECTED_KEY, true);
		m_ptree.refreshContextPath();

		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_ptree.getCoreUIRefreshPathTreeHandler());
//
//		mnufile.setMnemonic(KeyEvent.VK_F);
//		mnunew.setMnemonic(KeyEvent.VK_N);
//		m_mnuedit.setMnemonic(KeyEvent.VK_E);
//		mnuview.setMnemonic(KeyEvent.VK_V);
//		mnumainui.setMnemonic(KeyEvent.VK_M);
//		mnunav.setMnemonic(KeyEvent.VK_N);
//		mnuhelp.setMnemonic(KeyEvent.VK_H);

		mnufilenew.add(m_actmain.filenewfile);
		mnufilenew.add(m_actmain.filenewproject);
		mnufilenew.add(m_actmain.fileneweditor);

		mnufile.add(mnufilenew);
		mnufile.add(m_actmain.fileopen);
		mnufile.add(m_actmain.fileopenas);
		mnufile.add(m_actmain.fileopenfolder);
		mnufile.addSeparator();
		mnufile.add(m_actmain.filesave);
		mnufile.add(m_actmain.filesaveas);
		mnufile.add(m_actmain.fileprop);
		mnufile.addSeparator();
		mnufile.add(m_actmain.filecfgs);
		mnufile.add(m_actmain.filereloadcontext);
		mnufile.addSeparator();
		mnufile.add(m_actmain.fileexit);

		UIUtil.rebuildMenu(m_mnuedit, null, true);

		mnuview.add(mnumainui);
		mnuview.add(m_actmain.viewfullscreen);
		mnumainui.add(m_actmain.viewtoggleleftpan);
		mnumainui.add(m_actmain.viewtogglebottompan);
		mnumainui.add(m_actmain.viewtogglerightpan);

		mnunav.add(m_actmain.navresource);
		mnunav.add(m_actmain.navprjitem);
		mnunav.add(m_actmain.navcmd);
		mnunav.addSeparator();
		mnunav.add(m_actmain.naveditor);
		mnunav.addSeparator();
		mnunav.add(m_actmain.navoverview);

		m_mnushortcuts.setMnemonic('S');

		mnutool.setMnemonic('T');
		initToolMenu(mnutool);

		mnuhelp.add(m_actmain.helpsysinfo);

		m_mnubar.add(mnufile);
		m_mnubar.add(m_mnuedit);
		m_mnubar.add(mnuview);
		m_mnubar.add(mnunav);
		m_mnubar.add(m_mnushortcuts);
		m_mnubar.add(mnutool);
		m_mnubar.add(mnuhelp);
		FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
		fl.setVgap(0);
		fl.setHgap(0);
		m_mnuactbar.setMinimumSize(new Dimension(0, 0));
		m_mnuactbar.setBorder(new EmptyBorder(2, 0, 2, 0));
		m_mnuactbar.setOpaque(false);
		m_mnuactbar.setLayout(fl);
		m_mnubar.add(m_cmdpan);
		m_mnubar.add(m_mnuactbar);
		setJMenuBar(m_mnubar);

		m_sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_sp.setLeftComponent(m_ptree);
		m_sp.setRightComponent(pmain);

		m_sp2.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_sp2.setTopComponent(m_editors);
		m_sp2.setBottomComponent(m_bottomtab);
		double uiscale = UIConfigs.UI_SCALE();
		m_sp2.setReservedSize((int) (18f * uiscale) - 1);
		if (UIConfigs.DIVIDER_SIZE() == 1)
			m_sp2.setDividerBorderColor(UIConfigs.COLOR_TEXTHALF(), true);

		pmain.setLayout(new BorderLayout());
		pmain.add(m_sp2, BorderLayout.CENTER);
		pmain.setBorder(new EmptyBorder(0, 0, 0, 0));

		setLayout(m_layout);
		getContentPane().add(m_sp, BorderLayout.CENTER);

		if (UIConfigs.SYSTEM_TRAY())
		{
			initSystemTray();
		}

		refreshShortCuts();

		addWindowListener(this);

		initActions();

		validate();
		invalidate();
	}

	protected void initSystemTray()
	{
		if (!SystemTray.isSupported())
			return;
		Dimension d = SystemTray.getSystemTray().getTrayIconSize();
		int dx = Math.max(d.width, d.height);
		BufferedImage img = new BufferedImage(dx, dx, BufferedImage.TYPE_INT_ARGB);
		BPIconResV.BP().doDraw(img.getGraphics(), 0, 0, dx, dx);
		BPPopupMenuTray mnutray = new BPPopupMenuTray();
		{
			m_mnupopscs = new BPMenu("Shortcut");
			BPMenuItemInTray mnulocres = new BPMenuItemInTray("Resource...");
			BPMenuItemInTray mnulocprjitem = new BPMenuItemInTray("Project Item...");

			mnulocres.addActionListener(e -> CommonUIOperations.showLocateResource());
			mnulocprjitem.addActionListener(e -> CommonUIOperations.showLocateProjectItem());
			mnutray.add(m_mnupopscs);
			mnutray.add(mnulocres);
			mnutray.add(mnulocprjitem);
		}
		m_mnutray = mnutray;
		mnutray.addSeparator();
		{
			BPMenuItemInTray mnuexit = new BPMenuItemInTray("Exit");
			mnuexit.addActionListener(e -> exit());
			mnutray.add(mnuexit);
		}
		TrayIcon ti = new TrayIcon(img, "BlockP", null);
		UIStd.wrapSegE(() -> SystemTray.getSystemTray().add(ti));
		ti.addMouseListener(new UIUtil.BPMouseListener(null, this::onSysTrayDown, null, null, null));
		m_trayicon = ti;
	}

	protected void onSysTrayDown(MouseEvent e)
	{
		int btn = e.getButton();
		switch (btn)
		{
			case MouseEvent.BUTTON1:
			{
				e.consume();
				if (!isVisible())
					setVisible(true);
				if (getExtendedState() == ICONIFIED)
					setState(NORMAL);
				toFront();
				break;
			}
			case MouseEvent.BUTTON3:
			{
				e.consume();
				Point pt = e.getLocationOnScreen();
				m_mnutray.showTray(pt.x, pt.y);
				break;
			}
		}
	}

	protected void removeSystemTray()
	{
		if (!SystemTray.isSupported())
			return;
		try
		{
			TrayIcon ti = m_trayicon;
			m_trayicon = null;
			if (ti != null)
			{
				SystemTray.getSystemTray().remove(ti);
			}
		}
		catch (Exception e)
		{
		}
	}

	public void toggleVisible()
	{
		setVisible(!isVisible());
		if (isVisible())
			setState(NORMAL);
	}

	public void refreshShortCuts()
	{
		BlockPMenus.refreshShortcuts(m_mnushortcuts, this::refreshShortCuts);
		refreshTrayMenus();
	}

	protected void refreshTrayMenus()
	{
		BlockPMenus.refreshPopupShortcuts(m_mnupopscs);
	}

	protected void initToolMenu(JMenu mnutool)
	{
		mnutool.add(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCRIPTS, e -> showScriptManager()));
		mnutool.add(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUEXTS, e -> showExtensionManager()));
		mnutool.add(BPActionHelpers.getAction(BPActionConstCommon.MF_MNUMODS, e -> showModuleManager()));

		Map<String, List<BPTool>> toolmap = new HashMap<String, List<BPTool>>(BPGUICore.TOOL_MAP);
		List<String> keys = new ArrayList<String>(toolmap.keySet());
		keys.sort((a, b) ->
		{
			return a.compareToIgnoreCase(b);
		});
		for (String key : keys)
		{
			JMenu mnu = new BPMenu(key);
			List<BPTool> tools = toolmap.get(key);
			tools.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
			for (BPTool tool : tools)
			{
				Action act = BPAction.build(tool.getName()).callback((e) -> tool.run()).getAction();
				mnu.add(act);
			}
			mnutool.add(mnu);
		}
	}

	protected void initActions()
	{
		JComponent comp = (JComponent) getContentPane();
		Action[] shortcuts = m_actmain.getShortCutActions();
		InputMap im = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap am = comp.getActionMap();
		for (Action sc : shortcuts)
		{
			am.put(sc.getValue(Action.NAME), sc);
			im.put((KeyStroke) sc.getValue(Action.ACCELERATOR_KEY), sc.getValue(Action.NAME));
		}
	}

	protected void onMainFrameEvent(BPEventUIMainFrame event)
	{
		switch (event.subkey)
		{
			case BPEventUIMainFrame.MAINFRAME_ENTERSTANDALONEMODE:
			{
				enterStandaloneMode();
				break;
			}
		}
	}

	protected void onEditorEvent(BPEventUIEditors event)
	{
		switch (event.subkey)
		{
			case BPEventUIEditors.EDITOR_SWITCH:
			{
				setTitle(BPGUICore.S_BP_TITLE + " - " + event.getTabID());
				BPComponent<?> comp = event.getBPComponent();
				setEditorDynamicInfo("");
				if (comp != null)
				{
					BPComponentType ct = event.getBPComponent().getComponentType();
					if (ct == BPComponentType.CODEEDITOR || ct == BPComponentType.TEXTEDITOR)
					{
						BPTextPane txt = ((BPTextPanel) comp).getTextPanel();
						int[] pos = txt.getPos(txt.getCaretPosition());
						setEditorDynamicInfo(pos[0] + ":" + pos[1]);
					}
					Action[] actbaracts = null;
					if (comp instanceof BPEditor)
					{
						m_editorinfo.setEditorInfo(((BPEditor<?>) comp).getEditorInfo());
						BPEditor<?> editor = (BPEditor<?>) comp;
						Action[] editmenuacts = editor.getEditMenuActions();
						UIUtil.rebuildMenu(m_mnuedit, editmenuacts, true);
						actbaracts = editor.getActBarActions();
					}
					setActBarActions(actbaracts);
				}
				else
				{
					m_editorinfo.setEditorInfo("");
				}
				break;
			}
			case BPEventUIEditors.EDITOR_CLOSED:
			{
				setTitle(BPGUICore.S_BP_TITLE);
				m_editorinfo.setEditorDynamicInfo("");
				m_editorinfo.setEditorInfo("");
				UIUtil.rebuildMenu(m_mnuedit, null, true);
				setActBarActions(null);
				break;
			}
			case BPEventUIEditors.EDITOR_DYNAMICINFO:
			{
				setEditorDynamicInfo((String) event.datas[0]);
				break;
			}
			case BPEventUIEditors.EDITOR_STATUS_CHANGED:
			{
				String tabid = event.getTabID();
				if (m_editors.isCurrent(tabid))
				{
					m_editorinfo.setEditorInfo(event.getTitle());
				}
				break;
			}
		}
	}

	protected void setActBarActions(Action[] actions)
	{
		m_mnuactbar.removeAll();
		if (actions != null && actions.length > 0)
		{
			m_mnuactbar.add(Box.createRigidArea(new Dimension((int) (2 * UIConfigs.UI_SCALE()), (int) (4 * UIConfigs.UI_SCALE()))));
			for (Action act : actions)
			{
				Object issp = act.getValue(BPAction.IS_SEPARATOR);
				if (issp != null && (Boolean) issp)
				{
					m_mnuactbar.add(Box.createRigidArea(new Dimension((int) (4 * UIConfigs.UI_SCALE()), (int) (4 * UIConfigs.UI_SCALE()))));
				}
				else
				{
					Object vicon = act.getValue("VICON");
					if (vicon != null)
					{
						BPToolVIconButton btn = new BPToolVIconButton(act);
						btn.setButtonSize((int) m_mnubar.getSize().getHeight() - 6);
						m_mnuactbar.add(btn);
					}
				}
			}
		}
		m_mnuactbar.updateUI();
	}

	protected void setEditorDynamicInfo(String info)
	{
		m_editorinfo.setEditorDynamicInfo(info == null ? "" : info);
	}

	protected void onRefreshPrjTreeEvent(BPEventCoreUI event)
	{
		if (m_ptree != null && m_ptree.getPathTreeFuncs() instanceof BPProjectsTreeFuncs)
		{
			if (event.datas != null && event.datas.length > 0)
				refreshPathTree((BPResource) event.datas[0]);
			else
				refreshProjectTree(BPCore.getProjectsContext().getProject(event.subkey));
		}
	}

	protected void onResourceOperationEvent(BPEventUIResourceOperation event)
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
						Object[] params = event.getActionParams();
						BPFormat format = null;
						BPEditorFactory fac = null;
						String rconid = event.getRoutableContainerID();
						if (params != null && params.length > 1)
						{
							format = (BPFormat) params[0];
							fac = (BPEditorFactory) params[1];
						}
						for (BPResource res : ress)
						{
							if (res.isFileSystem())
							{
								BPResourceFileSystem fres = ((BPResourceFileSystem) res);
								if (fres.isFile())
									openFile(fres.getFileFullName(), format, fac, false, rconid, null);
								else if (fres.isDirectory())
									openDir(fres.getFileFullName(), format, fac, false, rconid);
							}
							else if (res instanceof BPResourceHolder)
							{
								openResource(((BPResourceHolder) res), format, fac, false, rconid);
							}
						}
						break;
					}
					case BPFileActions.ACTION_OPENAS:
					{
						openResourcesAs(event.getSelectedResources());
						break;
					}
					default:
					{
						BPResourceOperationCommonHandler.onResourceOperationEvent(event);
						break;
					}
				}
				break;
			}
		}
	}

	protected void onPathTreeEvent(BPEventUIPathTree event)
	{
		switch (event.subkey)
		{
			case BPEventUIPathTree.NODE_OPEN:
			{
				BPResource res = event.getSelectedResource();
				Object[] opennodeps = event.getOpenNodeParams();
				if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
					openFile(((BPResourceFileLocal) res).getPathName(), (BPFormat) opennodeps[0], (BPEditorFactory) opennodeps[1], false, null, null);
				else if (res.canOpen())
				{
					openResource(res, (BPFormat) opennodeps[0], (BPEditorFactory) opennodeps[1], false, null);
				}
				break;
			}
			case BPEventUIPathTree.NODE_ACTION:
			{
				String actionname=event.getActionName();
				switch (actionname)
				{
					case BPPathTreeNodeActions.ACTION_NEWFILE:
					{
						showNewFile(event.getSelectedResource());
						break;
					}
					case BPPathTreeNodeActions.ACTION_NEWFILEUNSAVED:
					{
						newEditor((BPResourceFile) event.getSelectedResource(), event.getActionParams());
						break;
					}
					case BPPathTreeNodeActions.ACTION_NEWDIR:
					{
						showNewDir(event.getSelectedResource(), null);
						break;
					}
					case BPPathTreeNodeActions.ACTION_PROPERTIES:
					{
						showProperty(event.getSelectedResource());
						break;
					}
					case BPProjectTreeNodeActions.ACTION_PRJ_OVERVIEW:
					{
						openResource(event.getSelectedResource(), new BPFormatProject(), null, true, null);
						break;
					}
					case BPTreeNodeActions.ACTION_OPENRES:
					case BPPathTreeNodeActions.ACTION_OPENFILE:
					{
						BPResource res = event.getSelectedResource();
						Object[] params = event.getActionParams();
						BPFormat format = null;
						BPEditorFactory fac = null;
						if (params != null && params.length > 1)
						{
							format = (BPFormat) params[0];
							fac = (BPEditorFactory) params[1];
						}
						if (res instanceof BPResourceProject)
						{
							openResource(res, format, fac, false, null);
						}
						else if (res.isFileSystem())
						{
							BPResourceFileSystem fres = (BPResourceFileSystem) res;
							if (fres.isFile())
								openFile(((BPResourceFile) res).getFileFullName(), format, fac, false, null, null);
							else if (fres.isDirectory())
								openDir(((BPResourceDir) res).getFileFullName(), format, fac, false, null);
						}
						else if (res instanceof BPResourceHolder || BPTreeNodeActions.ACTION_OPENRES.equals(actionname))
						{
							openResource(((BPResourceHolder) res), format, fac, false, null);
						}
						break;
					}
					case BPTreeNodeActions.ACTION_OPENRESAS:
					case BPPathTreeNodeActions.ACTION_OPENFILEAS:
					{
						BPResource res = event.getSelectedResource();
						if (res.isFileSystem())
							openFileAs(((BPResourceFileSystem) res).getFileFullName(), ((BPResourceFileSystem) res).isDirectory());
						else if (res instanceof BPResourceHolder || BPTreeNodeActions.ACTION_OPENRES.equals(actionname))
							openResourcesAs(new BPResource[] { res });
						break;
					}
					case BPPathTreeNodeActions.ACTION_OPENEXTERNAL_SYSTEM:
					case BPPathTreeNodeActions.ACTION_EDITEXTERNAL_SYSTEM:
					case BPPathTreeNodeActions.ACTION_PRINTEXTERNAL_SYSTEM:
					{
						BPResource res = event.getSelectedResource();
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
							switch (actionname)
							{
								case BPPathTreeNodeActions.ACTION_OPENEXTERNAL_SYSTEM:
									CommonUIOperations.openExternal(fres);
									break;
								case BPPathTreeNodeActions.ACTION_EDITEXTERNAL_SYSTEM:
									CommonUIOperations.editExternal(fres);
									break;
								case BPPathTreeNodeActions.ACTION_PRINTEXTERNAL_SYSTEM:
									CommonUIOperations.printExternal(fres);
									break;
							}
						}
						break;
					}
					case BPPathTreeNodeActions.ACTION_OPENWITHTOOL:
					{
						BPResource[] ress = (BPResource[]) event.datas[0];
						CommonUIOperations.openWithTool(ress);
						break;
					}
					case BPPathTreeNodeActions.ACTION_DELETE:
					{
						deleteResource(event.getSelectedResource());
						break;
					}
					case BPPathTreeNodeActions.ACTION_DELETES:
					{
						deleteResources(event.getSelectedResourcesFromPaths());
						break;
					}
					case BPPathTreeNodeActions.ACTION_REFRESH:
					{
						refreshPathTree(event.getSelectedResource());
						break;
					}
					case BPPathTreeNodeActions.ACTION_RENAME:
					{
						CommonUIOperations.showRenameResource(event.getSelectedResource());
						break;
					}
					case BPPathTreeNodeActions.ACTION_COPY:
					{
						CommonUIOperations.copyResources(event.getSelectedResources());
						break;
					}
					case BPPathTreeNodeActions.ACTION_COPYTO:
					{
						CommonUIOperations.showCopyResourcesTo(event.getSelectedResources(), this);
						break;
					}
				}
				break;
			}
		}
	}

	protected void deleteResource(BPResource res)
	{
		BPResource par = res.getParentResource();
		if (res.delete())
		{
			CommonUIOperations.refreshPathTree(par, false);
		}
	}

	protected void deleteResources(BPResource[] resources)
	{
		boolean flag = false;
		List<BPResource> pars = new ArrayList<BPResource>();
		List<BPResource> dels = new ArrayList<BPResource>();
		boolean isdelroot = false;
		for (BPResource res : resources)
		{
			BPResource par = res.getParentResource();
			boolean f = res.delete();
			flag = flag | f;
			if (f)
			{
				if (par == null)
					isdelroot = true;
				else if (!pars.contains(par))
					pars.add(par);
				dels.add(res);
			}
		}
		if (flag)
		{
			if (isdelroot)
			{
				CommonUIOperations.refreshPathTree(null, false);
			}
			else
			{
				for (BPResource par : pars)
				{
					if (!dels.contains(par))
						CommonUIOperations.refreshPathTree(par, false);
				}
			}
		}
	}

	public void closeCurrentTab()
	{
		m_editors.removeCurrentTab();
	}

	public void switchTab(int delta)
	{
		m_editors.switchTab(delta);
	}

	public void refreshProjectTree(BPResourceProject prj)
	{
		if (m_ptree != null && m_ptree.getPathTreeFuncs() instanceof BPProjectsTreeFuncs)
		{
			if (prj == null)
				UIUtil.inUI(() -> m_ptree.refreshContextPath());
			else
				UIUtil.inUI(() -> m_ptree.refreshSubTree(prj));
		}
	}

	public void refreshPathTree(BPResource res)
	{
		if (res != null)
			UIUtil.inUI(() -> m_ptree.refreshTree(res, true));
		else
			UIUtil.inUI(() -> m_ptree.refreshContextPath());
	}

	public void switchPathTreeFunc(int func)
	{
		switch (func)
		{
			case 1:
			{
				m_ptree.setPathTreeFuncs(new BPPathTreeLocalFuncs(m_channelid));
				m_acttree.pathtree.putValue(Action.SELECTED_KEY, true);
				m_acttree.prjstree.putValue(Action.SELECTED_KEY, false);
				m_ptree.refreshContextPath();
				break;
			}
			case 2:
			{
				m_ptree.setPathTreeFuncs(new BPProjectsTreeFuncs(m_channelid));
				m_acttree.pathtree.putValue(Action.SELECTED_KEY, false);
				m_acttree.prjstree.putValue(Action.SELECTED_KEY, true);
				m_ptree.refreshContextPath();
				break;
			}
		}
	}

	protected void setPrefers()
	{
		setTitle(BPGUICore.S_BP_TITLE);
		int[] startsize = UIConfigs.START_SCREENSIZE();
		setPreferredSize(new Dimension(startsize[0], startsize[1]));

		m_sp.setDividerLocation(300);
		m_sp2.setResizeWeight(0.8);
		super.setPrefers();
		m_sp2.togglePanel(false);
	}

	protected void initDatas()
	{
	}

	public void enterStandaloneMode()
	{
		m_editors.getTabBar().setVisible(false);
		if (m_sp.getToggleState() == 0)
			toggleLeftPanel();
	}

	public void exitStandaloneMode()
	{
		m_editors.getTabBar().setVisible(true);
		if (m_sp.getToggleState() != 0)
			toggleLeftPanel();
	}

	public void clearSubComponents()
	{
		m_layout = null;
		m_editors = null;

		m_actmain = null;
		m_sp = null;
		m_ptree = null;
	}

	public void showOpenFile(boolean defaulteditor)
	{
		String f = CommonUIOperations.showOpenFileDialog(this, "");
		if (f != null)
		{
			BPFormat format = null;
			BPEditorFactory fac = null;
			boolean isselected = false;
			BPConfig options = null;
			if (!defaulteditor)
			{
				BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
				dlg.setVisible(true);
				format = dlg.getSelectedFormat();
				fac = dlg.getSelectedEditorFactory();
				if (format == null && fac == null)
					return;
				isselected = true;
				options = dlg.getEditorOptions();
			}
			openFile(f, format, fac, isselected, null, options);
		}
	}

	public void newEditor(BPResourceFile res, Object... params)
	{
		if (res != null)
		{
			m_editors.newEdtior(res, params);
		}
	}

	public void openFileAs(String f, boolean isdir)
	{
		if (f == null)
			return;
		BPResource fres = isdir ? new BPResourceDirLocal(f) : new BPResourceFileLocal(f);
		BPFormat format = BPFormatManager.getFormatByExt(fres.getExt());
		BPConfig options = null;
		BPEditorFactory fac = null;
		BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
		dlg.setFormat(format);
		dlg.setVisible(true);
		format = dlg.getSelectedFormat();
		fac = dlg.getSelectedEditorFactory();
		options = dlg.getEditorOptions();
		if (format == null && fac == null)
			return;
		m_editors.open(fres, format, fac, null, options);
	}

	public void openResourcesAs(BPResource[] ress)
	{
		if (ress != null && ress.length > 0)
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
			for (BPResource res : ress)
			{
				m_editors.open(res, format, fac, null, options);
			}
		}
	}

	public void registerMenu(String key, String title, Action[] actions)
	{
		Component[] subs = m_mnubar.getComponents();
		BPMenu mnupar = null;
		for (Component sub : subs)
		{
			if (sub instanceof JMenu)
			{
				JMenu mnu = (JMenu) sub;
				String name = mnu.getName();
				if (key.equals(name))
				{
					mnupar = (BPMenu) mnu;
					break;
				}
			}
		}
		if (mnupar == null)
		{
			mnupar = new BPMenu(title);
			mnupar.setName(key);
			m_mnubar.add(mnupar, m_mnubar.getComponentIndex(m_cmdpan) - 1);
		}
		JComponent[] comps = UIUtil.makeMenuItems(actions);
		for (JComponent comp : comps)
		{
			mnupar.add(comp);
		}
	}

	public void createEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params)
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
				}
				m_editors.newEditorCustom(res, nformat, fac, null, options, params);
			}
		}
		catch (Exception e)
		{
			UIStd.err(e);
		}
	}

	public void openEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params)
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
					m_editors.newEditorCustom(res, nformat, fac, null, options, params);
				}
				else
				{
					m_editors.open(res, nformat, fac, null, options);
				}
			}
		}
		catch (Exception e)
		{
			UIStd.err(e);
		}
	}

	public void openResource(BPResource res, BPFormat format, BPEditorFactory fac, boolean isselected, String rconid)
	{
		String ext = res.getExt();
		if (ext == null)
			return;
		BPFormat nformat = (format != null ? format : BPFormatManager.getFormatByExt(ext));
		BPEditorFactory nfac = (fac != null ? fac : (nformat == null ? null : BPEditorManager.getFactory(nformat.getName())));
		BPConfig options = null;
		if (nfac == null)
		{
			BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
			dlg.setVisible(true);
			nformat = dlg.getSelectedFormat();
			nfac = dlg.getSelectedEditorFactory();
			options = dlg.getEditorOptions();
			if (nformat == null && nfac == null)
				return;
		}
		m_editors.open(res, nformat, nfac, rconid, options);
	}

	public void openDir(String f, BPFormat format, BPEditorFactory fac, boolean isselected, String rconid)
	{
		if (f == null)
			return;
		BPResourceDirLocal file = new BPResourceDirLocal(f);
		BPFormat nformat = (format != null ? format : new BPFormatDir());
		BPEditorFactory nfac = null;
		BPConfig options = null;
		String facname = EditorUtil.getOverrideFacName(file);
		if (facname != null)
			nfac = BPEditorManager.getFactory(nformat.getName(), facname);
		if (nfac == null)
			nfac = (fac != null ? fac : (nformat == null ? null : BPEditorManager.getFactory(nformat.getName())));
		if (nfac == null)
		{
			BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
			dlg.setVisible(true);
			nformat = dlg.getSelectedFormat();
			nfac = dlg.getSelectedEditorFactory();
			options = dlg.getEditorOptions();
			if (nformat == null && nfac == null)
				return;
		}
		m_editors.open(file, nformat, nfac, rconid, options);
	}

	public void openFile(String f, BPFormat format, BPEditorFactory fac, boolean isselected, String rconid, BPConfig doptions)
	{
		if (f == null)
			return;
		BPResourceFileSystemLocal file = FileUtil.isDir(f) ? new BPResourceDirLocal(f) : new BPResourceFileLocal(f);
		String ext = file.getExt();
		BPFormat nformat = (format != null ? format : BPFormatManager.getFormatByExt(ext));
		BPEditorFactory nfac = null;
		BPConfig options = doptions;
		boolean isna = false;
		if (nformat != null && nformat.getName().equals(BPFormatUnknown.FORMAT_NA))
			isna = true;
		if (format == null && isna)
		{
			String tfacname = EditorUtil.getOverrideFacName(file);
			if (tfacname != null)
				nfac = BPEditorManager.getFactory(nformat.getName(), tfacname);
		}
		if (nfac == null && isna)
		{
			String tfacname = EditorUtil.getDefaultFileFacName();
			if (tfacname != null)
				nfac = BPEditorManager.getFactory(BPFormatUnknown.FORMAT_NA, tfacname);
		}
		if (nfac == null)
			nfac = (fac != null ? fac : (nformat == null ? null : BPEditorManager.getFactory(nformat.getName())));
		if (nfac == null)
		{
			BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
			dlg.setVisible(true);
			nformat = dlg.getSelectedFormat();
			nfac = dlg.getSelectedEditorFactory();
			options = dlg.getEditorOptions();
			if (nformat == null && nfac == null)
				return;
		}
		m_editors.open(file, nformat, nfac, null, options);
	}

	public void exit()
	{
		BPGUICore.closeSubWindows();
		dispose();
	}

	public BPTabBottom getBottomTab()
	{
		return m_bottomtab;
	}

	public BPComponent<?> getCurrentEditor()
	{
		BPComponent<?> comp = m_editors.getCurrent();
		if (comp != null && comp instanceof BPRoutableContainer)
			comp = ((BPRoutableContainer<?>) comp).getCurrent();
		return comp;
	}

	public BPComponent<?> getCurrentEditorRaw()
	{
		return m_editors.getCurrent();
	}

	public BPEditors getEditors()
	{
		return m_editors;
	}

	public List<BPComponent<?>> getEditorList()
	{
		return m_editors.getEditorList();
	}

	public BPResource getSelectedResource()
	{
		return m_ptree.getSelectedResource();
	}

	public void showNewFile(BPResource res)
	{
		CommonUIOperations.showNewFile(res, m_ptree.getTreeComponent());
	}

	public void showNewDir(BPResource res, BPFormat format)
	{
		CommonUIOperations.showNewDirectory(res, m_ptree.getTreeComponent());
	}

	public void showOpenWorkspace()
	{
		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int s = f.showOpenDialog(this);
		if (s == JFileChooser.APPROVE_OPTION)
		{
			BPCore.setLocalFileContext(f.getSelectedFile().getPath());
			m_ptree.refreshContextPath();
		}
	}

	public void showProperty(BPResource res)
	{
		CommonUIOperations.showProperty(res, m_ptree.getTreeComponent());
	}

	public void toggleLeftPanel()
	{
		m_sp.togglePanel(true);
	}

	public void toggleBottomPanel()
	{
		m_sp2.togglePanel(false);
	}

	public void toggleRightPanel()
	{
		BPComponent<?> comp = m_editors.getCurrent();
		if (comp != null)
		{
			if (comp instanceof BPEditor)
			{
				((BPEditor<?>) comp).toggleRightPanel();
			}
		}
	}

	public void showNewProject()
	{
		CommonUIOperations.showNewProject();
	}

	public void showNewEditor()
	{
		BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
		dlg.setCreateMode(true);
		dlg.setVisible(true);
		BPFormat nformat = dlg.getSelectedFormat();
		BPEditorFactory nfac = dlg.getSelectedEditorFactory();
		BPConfig options = dlg.getEditorOptions();
		if (nformat == null && nfac == null)
			return;
		String[] exts = nformat.getExts();
		String ext = exts != null && exts.length > 0 ? exts[0] : "";
		String extprefix = null;
		if (nfac != null)
			extprefix = nfac.getExtPrefix(ext);
		BPResourceFileLocal f = new BPResourceFileLocal("untitled" + (extprefix == null ? "" : extprefix) + ext);
		m_editors.newEditorCustom(f, nformat, nfac, null, options);
	}

	public void save()
	{
		m_editors.save();
	}

	public void saveAs()
	{
		m_editors.saveAs();
	}

	public void showLocateResource()
	{
		BPDialogLocateCachedResource dlg = new BPDialogLocateCachedResource();
		dlg.setVisible(true);
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			m_editors.open((BPResourceFile) res);
		}
	}

	public void showLocateProjectItem()
	{
		BPDialogLocateProjectItem dlg = new BPDialogLocateProjectItem();
		dlg.doSearch();
		dlg.setVisible(true);
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			openResource(res, null, null, false, null);
		}
	}

	public void showSwitchEditor()
	{
		m_editors.showSwitchEditor();
	}

	public void showOverview()
	{
		BPRoutableContainerBase par = new BPRoutableContainerBase();
		BPProjectsOverviewPanel pnl = new BPProjectsOverviewPanel();
		String newid = BPCore.genID(BPCore.getFileContext());
		par.addRoute(newid, "Overview:Projects", pnl);
		String parid = BPCore.genID(BPCore.getFileContext());
		par.setID(parid);
		m_editors.addBPTab(parid, null, "Overview:Projects", par, true);
	}

	public void showCommandPane()
	{
		m_cmdpan.setVisible(true);
		m_cmdpan.focus();
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		BPGUICore.closeSubWindows();
	}

	public void windowClosed(WindowEvent e)
	{
		if (!isVisible())
		{
			removeSystemTray();
			m_editorinfo.clearResource();
			ThreadGroup tg = ThreadUtil.exitCleanThreadGroup;
			Thread[] ts = new Thread[tg.activeCount()];
			tg.enumerate(ts);
			for (Thread t : ts)
			{
				if (t != null && t.isAlive())
				{
					if (t instanceof Closeable)
					{
						try
						{
							((Closeable) t).close();
						}
						catch (IOException e1)
						{
							Std.err(e1);
						}
					}
				}
			}
		}
	}

	public void windowIconified(WindowEvent e)
	{
		if (m_trayicon != null && UIConfigs.MIN_TO_TRAY())
		{
			setVisible(false);
		}
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void showScriptManager()
	{
		BPDialogScriptManager dlg = new BPDialogScriptManager();
		dlg.setVisible(true);
	}

	public void showExtensionManager()
	{
		BPExtensionLoader[] exts = BPExtensionManager.getExtensionLoaders();
		Arrays.sort(exts, (a, b) -> a.getName().compareTo(b.getName()));
		UIStd.viewList(Arrays.asList(exts), "BlockP - Extensions", (loader) -> ((BPExtensionLoader) loader).getInfo());
	}

	public void showModuleManager()
	{
		List<BPModule> mnames = BPModuleManager.getModules();
		UIStd.viewList(mnames, "BlockP - Modules", null);
	}

	public void showConfigs()
	{
		BPDialogConfigs dlg = new BPDialogConfigs();
		dlg.setVisible(true);
	}

	public void reloadContext()
	{
		BPCore.FS_CACHE.stop();
		BPCore.FS_CACHE.clearTasks();
		BPCore.FS_CACHE.clear();
		BPCore.setLocalFileContext(BPCore.getFileContext().getRootDir().getFileFullName());
		BPCore.getProjectsContext().initProjects();
		BPCore.loadSchedules();
		m_ptree.refreshContextPath();
	}
}
