package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfigSimple;
import bp.config.UIConfigs;
import bp.event.BPEventChannelUI;
import bp.event.BPEventCoreUI;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFactory;
import bp.res.BPResourceFileSystem;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.tree.BPPathTreeComputerFuncs;
import bp.ui.tree.BPPathTreeFuncs;
import bp.ui.tree.BPPathTreeLocalFuncs;
import bp.ui.tree.BPPathTreeNodeCommonHandler;
import bp.ui.tree.BPPathTreePanel;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.tree.BPPathTreeSpecialFuncs;
import bp.ui.tree.BPProjectsTreeFuncs;
import bp.ui.tree.BPTreeFuncs;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPDialogSelectResource2 extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1678566416264039115L;

	protected Consumer<BPEventUIPathTree> m_pathtreehandler;
	protected BPPathTreePanel m_ptree;
	protected BPTextField m_filebox;
	protected SELECTTYPE m_selecttype = SELECTTYPE.ALL;
	protected WeakReference<Predicate<?>> m_customfilter;
	protected int m_channelid;
	protected BPResource m_result;
	protected BPResource[] m_files;
	protected BPPathTreeNodeCommonHandler m_ptreehandler;
	protected JPanel m_filenamep;
	protected Action[] m_acts;
	protected BPAction m_actprjres;
	protected BPAction m_actfileres;
	protected BPAction m_actcfileres;
	protected BPAction m_actspres;
	protected BPAction m_actlocate;
	protected CHECKEXITFLAG m_checkexist;
	protected String[] m_exts;

	protected WeakReference<Predicate<BPResource>> m_filterref;
	protected WeakReference<Predicate<BPResource>> m_targetfilterref;

	public BPDialogSelectResource2()
	{
	}

	public BPDialogSelectResource2(Window owner)
	{
		super(owner);
	}

	protected void initBPEvents()
	{
		super.initBPEvents();
		BPEventChannelUI channelui = new BPEventChannelUI();
		m_channelid = BPGUICore.EVENTS_UI.addChannel(channelui);
		initBPEventHandlers(channelui);
	}

	protected void initBPEventHandlers(BPEventChannelUI channelui)
	{
		m_pathtreehandler = (event) ->
		{
			switch (event.subkey)
			{
				case BPEventUIPathTree.NODE_SELECT:
				{
					BPResource res = event.getSelectedResource();
					if (!checkTarget(res))
						m_filebox.setText("");
					else
						m_filebox.setText(res.getName());
					break;
				}
				case BPEventUIPathTree.NODE_ACTION:
				{
					if (event.getActionName().equals(BPPathTreeNodeActions.ACTION_OPENFILE))
						callCommonAction(COMMAND_OK);
					else
						m_ptreehandler.onPathTreeEvent(event);
					break;
				}
				case BPEventUIPathTree.NODE_OPEN:
				{
					BPResource res = event.getSelectedResource();
					if (res.isLeaf())
						callCommonAction(COMMAND_OK);
					break;
				}
			}
		};
		channelui.on(BPEventUIPathTree.EVENTKEY_PATHTREE, m_pathtreehandler);
	}

	protected boolean checkTarget(BPResource res)
	{
		WeakReference<Predicate<BPResource>> fref = m_targetfilterref;
		if (fref != null)
		{
			Predicate<BPResource> cb = fref.get();
			if (cb != null)
			{
				return cb.test(res);
			}
		}
		if (m_selecttype == SELECTTYPE.DIR)
			return res.isFileSystem() && !res.isLeaf();
		else if (m_selecttype == SELECTTYPE.FILE)
			return res.isFileSystem() && res.isLeaf();
		return true;
	}

	protected void initUIComponents()
	{
		m_ptree = new BPPathTreePanel();
		m_ptree.setEventChannelID(m_channelid);
		m_ptree.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
		m_ptree.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
		BPPathTreeFuncs treefuncs = new BPProjectsTreeFuncs(m_channelid);
		treefuncs.setTreeFilter(this::filterTreeItem);
		m_ptree.setPathTreeFuncs(treefuncs);
		BPLabel lblfilename = new BPLabel();
		m_filebox = new BPTextField();
		m_ptreehandler = new BPPathTreeNodeCommonHandler(m_ptree.getTreeComponent());

		m_actprjres = BPAction.build("Project Tree").callback(e -> switchPathTreeFunc(2)).tooltip("Project Tree").vIcon(BPIconResV.PRJSTREE()).getAction();
		m_actfileres = BPAction.build("Path Tree").callback(e -> switchPathTreeFunc(1)).tooltip("Path Tree").vIcon(BPIconResV.PATHTREE()).getAction();
		m_actcfileres = BPAction.build("Computer Tree").callback(e -> switchPathTreeFunc(3)).tooltip("Computer Path Tree").vIcon(BPIconResV.PATHTREE_COMPUTER()).getAction();
		m_actspres = BPAction.build("Special").callback(e -> switchPathTreeFunc(4)).tooltip("Special").vIcon(BPIconResV.PATHTREE_SPECIAL()).getAction();
		m_actlocate = BPAction.build("Goto").callback(e -> showLocate()).tooltip("Goto(F6)").vIcon(BPIconResV.TORIGHT()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)).getAction();
		m_acts = new Action[] { m_actprjres, m_actfileres, m_actcfileres, m_actspres, null, m_actlocate };
		m_ptree.setToolBarActions(m_acts);
		m_actprjres.putValue(Action.SELECTED_KEY, true);
		m_actlocate.setEnabled(treefuncs.canLocatePath());

		m_filebox.setMonoFont();
		lblfilename.setLabelFont();
		lblfilename.setText("Filename:");
		lblfilename.setOpaque(false);
		lblfilename.setBackground(UIConfigs.COLOR_TEXTBG());
		lblfilename.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 2)));

		JPanel mainp = new JPanel();
		JPanel filenamep = new JPanel();
		m_filenamep = filenamep;

		filenamep.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		mainp.setLayout(new BorderLayout());
		filenamep.setLayout(new BorderLayout());
		filenamep.add(m_filebox, BorderLayout.CENTER);
		filenamep.add(lblfilename, BorderLayout.WEST);

		mainp.add(m_ptree, BorderLayout.CENTER);
		mainp.add(filenamep, BorderLayout.SOUTH);

		setLayout(new BorderLayout());

		add(mainp, BorderLayout.CENTER);

		setCommandBarMode(COMMANDBAR_OKENTER_CANCEL);

		m_ptree.refreshContextPath();
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_ptree.getCoreUIRefreshPathTreeHandler());

		setTitle("BlockP - Select Resource");
		setModal(true);
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
	}

	protected void showLocate()
	{
		String p = UIStd.inputPath("", "Path:", "Input path");
		if (p != null)
		{
			BPPathTreeFuncs funcs = (BPPathTreeFuncs) m_ptree.getPathTreeFuncs();
			funcs.locatePath(m_ptree.getTreeComponent(), p);
		}
	}

	public void switchPathTreeFunc(int func)
	{
		BPPathTreeFuncs funcs = null;
		switch (func)
		{
			case 1:
			{
				funcs = new BPPathTreeLocalFuncs(m_channelid);
				m_ptree.setPathTreeFuncs(funcs);
				m_actprjres.putValue(Action.SELECTED_KEY, false);
				m_actfileres.putValue(Action.SELECTED_KEY, true);
				m_actcfileres.putValue(Action.SELECTED_KEY, false);
				m_actspres.putValue(Action.SELECTED_KEY, false);
				m_ptree.refreshContextPath();
				break;
			}
			case 2:
			{
				funcs = new BPProjectsTreeFuncs(m_channelid);
				m_ptree.setPathTreeFuncs(funcs);
				m_actprjres.putValue(Action.SELECTED_KEY, true);
				m_actfileres.putValue(Action.SELECTED_KEY, false);
				m_actcfileres.putValue(Action.SELECTED_KEY, false);
				m_actspres.putValue(Action.SELECTED_KEY, false);
				m_ptree.refreshContextPath();
				break;
			}
			case 3:
			{
				funcs = new BPPathTreeComputerFuncs(m_channelid);
				m_ptree.setPathTreeFuncs(funcs);
				m_actprjres.putValue(Action.SELECTED_KEY, false);
				m_actfileres.putValue(Action.SELECTED_KEY, false);
				m_actcfileres.putValue(Action.SELECTED_KEY, true);
				m_actspres.putValue(Action.SELECTED_KEY, false);
				break;
			}
			case 4:
			{
				BPPathTreeSpecialFuncs f = new BPPathTreeSpecialFuncs(m_channelid);
				f.setNeedExist(m_checkexist == CHECKEXITFLAG.BLOCKNOTEXIST);
				funcs = f;
				m_ptree.setPathTreeFuncs(funcs);
				m_actprjres.putValue(Action.SELECTED_KEY, false);
				m_actfileres.putValue(Action.SELECTED_KEY, false);
				m_actcfileres.putValue(Action.SELECTED_KEY, false);
				m_actspres.putValue(Action.SELECTED_KEY, true);
				break;
			}
		}
		m_actlocate.setEnabled(funcs != null ? funcs.canLocatePath() : false);
		WeakReference<Predicate<BPResource>> filterref = m_filterref;
		if (filterref != null)
		{
			Predicate<BPResource> filter = m_filterref.get();
			if (filter != null)
				funcs.setTreeFilter(filter);
		}
	}

	public void setSelectType(SELECTTYPE flag)
	{
		m_selecttype = flag;
		m_ptree.refreshContextPath();
	}

	public void setScope(SELECTSCOPE scope)
	{
		switch (scope)
		{
			case WORKSPACE:
			{
				m_actprjres.setEnabled(false);
				m_actfileres.setEnabled(true);
				m_actcfileres.setEnabled(false);
				m_actspres.setEnabled(false);
				switchPathTreeFunc(1);
				break;
			}
			case PROJECT:
			{
				m_actprjres.setEnabled(true);
				m_actfileres.setEnabled(false);
				m_actcfileres.setEnabled(false);
				m_actspres.setEnabled(false);
				switchPathTreeFunc(2);
				break;
			}
			case COMPUTER:
			{
				m_actprjres.setEnabled(false);
				m_actfileres.setEnabled(false);
				m_actcfileres.setEnabled(true);
				m_actspres.setEnabled(false);
				switchPathTreeFunc(3);
				break;
			}
			case SPECIAL:
			{
				m_actprjres.setEnabled(false);
				m_actfileres.setEnabled(false);
				m_actcfileres.setEnabled(false);
				m_actspres.setEnabled(true);
				switchPathTreeFunc(4);
				break;
			}
		}
		m_ptree.refreshContextPath();
	}

	protected boolean filterTreeItem(Object obj)
	{
		BPResource res = (BPResource) obj;
		if (m_selecttype == SELECTTYPE.DIR && res.isLeaf())
			return false;
		return true;
	}

	public void clearSubComponents()
	{
		m_ptree = null;
	}

	public void showSave()
	{
		showSave(null);
	}

	public void showSave(String[] exts)
	{
		m_exts = exts;
		m_checkexist = CHECKEXITFLAG.CONFIRMOVERWRITE;
		setVisible(true);
	}

	public void showOpen()
	{
		m_checkexist = CHECKEXITFLAG.BLOCKNOTEXIST;
		m_filenamep.setVisible(false);
		setVisible(true);
	}

	public void setVisible(boolean flag)
	{
		super.setVisible(flag);
	}

	public void setCheckExist(CHECKEXITFLAG flag)
	{
		m_checkexist = flag;
	}

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				BPResource res = m_ptree.getSelectedResource();
				if (res != null)
				{
					Object[][] respaths = m_ptree.getTreeComponent().getSelectedNodeUserObjectPaths();
					BPResource[] resarr = new BPResource[respaths.length];
					for (int i = 0; i < respaths.length; i++)
					{
						Object[] respath = respaths[i];
						resarr[i] = (BPResource) respath[respath.length - 1];
					}
					m_files = resarr;
				}

				BPResource rc = null;
				CHECKEXITFLAG checkexist = m_checkexist;
				if (checkexist == CHECKEXITFLAG.BLOCKNOTEXIST)
				{
					if (checkTarget(res))
						rc = res;
					else
						return true;
				}
				else
				{
					if (checkexist != CHECKEXITFLAG.BLOCKNOTEXIST)
					{
						if (!res.isLeaf())
						{
							String filename = m_filebox.getText().trim();
							if (filename.isEmpty())
							{
								m_filebox.setBorder(new MatteBorder(1, 1, 1, 1, Color.RED));
								return true;
							}
							else
							{
								rc = ((BPResourceDir) res).getChild(filename, false);
							}
						}
						else
						{
							String filename = m_filebox.getText().trim();
							if (filename.isEmpty())
								rc = res;
							else
							{
								if (filename.equals(res.getName()))
								{
									rc = res;
								}
								else
								{
									BPResourceDir dir = (BPResourceDir) res.getParentResource();
									if (dir != null)
										rc = ((BPResourceDir) res.getParentResource()).getChild(filename, false);
									else
										rc = res;
								}
							}
						}
					}
					if (rc != null && rc.isFileSystem() && ((BPResourceFileSystem) rc).exists() && checkexist == CHECKEXITFLAG.CONFIRMOVERWRITE)
					{
						if (!UIStd.confirm(this, res.toString() + " exists, \nOverwrite?", "Confirm"))
							return true;
					}
				}
				if (rc != null)
				{
					m_result = rc;
					return false;
				}
				else
					return true;
			}
			case COMMAND_CANCEL:
			{
				break;
			}
		}
		return false;
	}

	public void setFilter(Predicate<BPResource> filter)
	{
		m_filterref = new WeakReference<Predicate<BPResource>>(filter);
		BPTreeFuncs funcs = m_ptree.getPathTreeFuncs();
		funcs.setTreeFilter(filter);
		m_ptree.refreshContextPath();
	}

	public void setTargetFilter(Predicate<BPResource> filter)
	{
		m_targetfilterref = new WeakReference<Predicate<BPResource>>(filter);
	}

	public BPResource getSelectedResource()
	{
		BPResource rc = m_result;
		if (rc != null && rc.isFactory() && m_exts != null && m_exts.length > 0)
		{
			BPResourceFactory fac = (BPResourceFactory) rc;
			rc = fac.makeResource(BPConfigSimple.fromData(ObjUtil.makeMap("ext", m_exts[0])));
		}
		return rc;
	}

	public BPResource[] getSelectedResources()
	{
		return m_files;
	}

	public static enum SELECTTYPE
	{
		FILE, DIR, ALL
	}

	public static enum SELECTSCOPE
	{
		WORKSPACE, PROJECT, COMPUTER, SPECIAL
	}

	public static enum CHECKEXITFLAG
	{
		DONOTHING, CONFIRMOVERWRITE, BLOCKNOTEXIST
	}
}
