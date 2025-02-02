package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.event.BPEventChannelUI;
import bp.event.BPEventCoreUI;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPPathTreeFuncs;
import bp.ui.tree.BPPathTreeLocalFuncs;
import bp.ui.tree.BPPathTreeNodeCommonHandler;
import bp.ui.tree.BPPathTreePanel;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.tree.BPProjectsTreeFuncs;
import bp.ui.tree.BPTreeComponent;
import bp.ui.util.UIUtil;

public class BPDialogSelectResourceList extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Consumer<BPEventUIPathTree> m_pathtreehandler;
	protected BPPathTreePanel m_ptree;
	protected BPList<BPResource> m_lstres;
	protected int m_channelid;
	protected BPPathTreeNodeCommonHandler m_ptreehandler;
	protected WeakReference<Predicate<BPResource>> m_customfilter;
	protected List<BPResource> m_datas;
	protected List<BPResource> m_result;

	protected final static String RES_ADD = "listres_add";

	public BPDialogSelectResourceList()
	{
	}

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				m_result = m_datas;
				break;
			}
		}
		return false;
	}

	public List<BPResource> getResult()
	{
		return m_result;
	}

	public void setResourceList(List<BPResource> ress)
	{
		m_datas.addAll(ress);
		m_lstres.updateUI();
	}

	protected void initBPEvents()
	{
		super.initBPEvents();
		BPEventChannelUI channelui = new BPEventChannelUI();
		m_channelid = BPGUICore.EVENTS_UI.addChannel(channelui);
		initBPEventHandlers(channelui);
	}

	public void setFilter(Predicate<BPResource> filter)
	{
		m_customfilter = new WeakReference<Predicate<BPResource>>(filter);
		m_ptree.refreshContextPath();
	}

	protected void initBPEventHandlers(BPEventChannelUI channelui)
	{
		m_pathtreehandler = (event) ->
		{
			switch (event.subkey)
			{
				case RES_ADD:
				{
					addResource(event.getSelectedResource());
					break;
				}
			}
		};
		channelui.on(BPEventUIPathTree.EVENTKEY_PATHTREE, m_pathtreehandler);
	}

	protected void initUIComponents()
	{
		JPanel mainp = new JPanel();
		m_lstres = new BPList<BPResource>();
		m_lstres.setCellRenderer(new BPList.BPListRenderer(this::onRenderResource));
		m_lstres.setMonoFont();
		m_ptree = new BPPathTreePanel();
		m_ptree.setEventChannelID(m_channelid);
		m_ptree.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
		m_ptree.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
		m_ptreehandler = new BPPathTreeNodeCommonHandler(m_ptree.getTreeComponent());
		BPPathTreeFuncs treefuncs = new BPProjectsTreeFuncsManList(m_channelid);
		treefuncs.setTreeFilter(this::filterTreeItem);
		m_ptree.setPathTreeFuncs(treefuncs);

		JPanel leftpan = new JPanel();
		BPToolBarSQ toollst = new BPToolBarSQ();
		BPAction actdelete = BPAction.build("del").vIcon(BPIconResV.DEL()).callback(this::onDelete).getAction();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.CENTER);
		fl.setVgap(0);
		toollst.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		toollst.setBarHeight(18);
		toollst.setLayout(fl);
		toollst.setActions(new Action[] { actdelete });
		JScrollPane scrollleft = new JScrollPane();
		scrollleft.setViewportView(m_lstres);
		scrollleft.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));

		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(300, 0)));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(scrollleft, BorderLayout.CENTER);
		leftpan.add(toollst, BorderLayout.SOUTH);

		mainp.setLayout(new BorderLayout());
		mainp.add(leftpan, BorderLayout.WEST);
		mainp.add(m_ptree, BorderLayout.CENTER);

		add(mainp, BorderLayout.CENTER);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);

		m_ptree.refreshContextPath();
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_ptree.getCoreUIRefreshPathTreeHandler());

		setTitle("BlockP - Select Resource List");
		setModal(true);
	}

	protected String onRenderResource(Object res)
	{
		return ((BPResourceFileSystem) res).getFileFullName();
	}

	public void setTreeFuncs(BPPathTreeFuncs funcs)
	{
		m_ptree.setPathTreeFuncs(funcs);
		m_ptree.refreshContextPath();
	}
	
	public void switchProjectTreeFuncs()
	{
		BPPathTreeFuncs treefuncs = new BPProjectsTreeFuncsManList(m_channelid);
		treefuncs.setTreeFilter(this::filterTreeItem);
		m_ptree.setPathTreeFuncs(treefuncs);
		m_ptree.refreshContextPath();
	}
	
	public void switchPathTreeFuncs()
	{
		BPPathTreeFuncs treefuncs = new BPPathTreeLocalFuncsManList(m_channelid);
		treefuncs.setTreeFilter(this::filterTreeItem);
		m_ptree.setPathTreeFuncs(treefuncs);
		m_ptree.refreshContextPath();
	}

	protected boolean filterTreeItem(Object obj)
	{
		BPResource res = (BPResource) obj;
		WeakReference<Predicate<BPResource>> filterref = m_customfilter;
		if (filterref != null)
		{
			Predicate<BPResource> filter = filterref.get();
			if (filter != null)
				return filter.test(res);
		}
		return true;
	}

	protected void onDelete(ActionEvent e)
	{
		m_datas.removeAll(m_lstres.getSelectedValuesList());
		m_lstres.updateUI();
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		m_datas = new ArrayList<BPResource>();
		BPListModel<BPResource> model = new BPListModel<BPResource>();
		model.setDatas(m_datas);
		m_lstres.setModel(model);
	}

	protected void addResource(BPResource res)
	{
		m_datas.add(res);
		m_lstres.updateUI();
	}
	
	protected static class BPPathTreeLocalFuncsManList extends BPPathTreeLocalFuncs
	{
		public BPPathTreeLocalFuncsManList(int channelid)
		{
			super(channelid);
		}

		public List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
		{
			List<Action> rc = new ArrayList<Action>();
			if (node != null)
			{
				BPResource res = (BPResource) node.getUserObject();
				BPAction actadd = BPAction.build("Add").callback((e) -> BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIPathTree(RES_ADD, res))).getAction();
				rc.add(actadd);
			}
			return rc;
		}
	}

	protected static class BPProjectsTreeFuncsManList extends BPProjectsTreeFuncs
	{
		public BPProjectsTreeFuncsManList(int channelid)
		{
			super(channelid);
		}

		public List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
		{
			List<Action> rc = new ArrayList<Action>();
			if (node != null)
			{
				BPResource res = (BPResource) node.getUserObject();
				BPAction actadd = BPAction.build("Add").callback((e) -> BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIPathTree(RES_ADD, res))).getAction();
				rc.add(actadd);
			}
			return rc;
		}
	}
}
