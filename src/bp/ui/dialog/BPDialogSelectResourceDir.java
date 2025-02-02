package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import bp.BPCore;
import bp.BPGUICore;
import bp.cache.BPCacheDataFileSystem;
import bp.config.UIConfigs;
import bp.event.BPEventChannelUI;
import bp.event.BPEventCoreUI;
import bp.res.BPResource;
import bp.ui.actions.BPCommonDialogActions;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.tree.BPPathTreeLocalFuncs;
import bp.ui.tree.BPPathTreePanel;
import bp.ui.tree.BPProjectsTreeFuncs;
import bp.ui.tree.BPTreeFuncs;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;

public class BPDialogSelectResourceDir extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3719870917192980179L;

	protected int m_channelid;

	protected BPResource m_res;

	protected BPPathTreePanel m_ptree;

	protected Consumer<BPEventUIPathTree> m_ptreehandler;

	protected void initBPEvents()
	{
		BPEventChannelUI channelui = new BPEventChannelUI();
		m_channelid = BPGUICore.EVENTS_UI.addChannel(channelui);
		initBPEventHandlers(channelui);
	}

	protected void initBPEventHandlers(BPEventChannelUI channelui)
	{
		m_ptreehandler = this::onPathTreeEvent;
		channelui.on(BPEventUIPathTree.EVENTKEY_PATHTREE, m_ptreehandler);
	}

	public void setFilter(Predicate<BPResource> filter)
	{
		BPTreeFuncs funcs = m_ptree.getPathTreeFuncs();
		funcs.setTreeFilter(filter);
		m_ptree.refreshContextPath();
	}

	public void setProjectResource(boolean flag)
	{
		if (flag)
		{
			m_ptree.setPathTreeFuncs(new BPProjectsTreeFuncs(m_channelid));
		}
		else
		{
			m_ptree.setPathTreeFuncs(new BPPathTreeLocalFuncs(m_channelid));
		}
		m_ptree.refreshContextPath();
	}

	protected void initUIComponents()
	{
		m_ptree = new BPPathTreePanel();
		BPPathTreeLocalFuncs funcs = BPPathTreeLocalFuncs.OnlySelect();
		funcs.setChannelID(m_channelid);
		funcs.setFilter((res) -> !res.isLeaf());
		m_ptree.setPathTreeFuncs(funcs);
		m_ptree.setEventChannelID(m_channelid);

		JPanel mainpan = new JPanel();
		mainpan.setLayout(new BorderLayout());
		mainpan.add(m_ptree, BorderLayout.CENTER);
		mainpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 0)));

		setLayout(new BorderLayout());
		add(mainpan, BorderLayout.CENTER);

		BPCommonDialogActions m_acts = new BPCommonDialogActions(this);
		m_acts.actionok.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setCommandBar(new Action[] { m_acts.actionok, m_acts.actioncancel });

		m_ptree.refreshContextPath();
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_ptree.getCoreUIRefreshPathTreeHandler());

		setTitle("Select Directory");
		setModal(true);
	}

	protected void onPathTreeEvent(BPEventUIPathTree event)
	{
		switch (event.subkey)
		{
			case BPEventUIPathTree.NODE_ACTION:
			{
				switch (event.getActionName())
				{
					case BPPathTreeNodeActions.ACTION_NEWDIR:
					{
						BPResource res = event.getSelectedResource();
						CommonUIOperations.showNewDirectory(res, m_ptree.getTreeComponent());
						break;
					}
				}
				break;
			}
		}
	}

	protected void onListMouseClick(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
		{
			callCommonAction(COMMAND_OK);
		}
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(500, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			m_res = m_ptree.getSelectedResource();
			if (m_res == null)
				return true;
		}
		return false;
	}

	public BPResource getSelectedResource()
	{
		return m_res;
	}

	@SuppressWarnings("serial")
	protected static class FSCacheDataListRenderer extends DefaultListCellRenderer
	{
		protected JLabel lbl;
		protected JLabel lbl2;
		protected JPanel pan;

		public FSCacheDataListRenderer()
		{
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			if (lbl == null)
			{
				lbl = new JLabel();
				lbl2 = new JLabel();
				pan = new JPanel();
				pan.setBorder(new EmptyBorder(0, 2, 0, 2));
				pan.setLayout(new BorderLayout());
				pan.add(lbl, BorderLayout.WEST);
				pan.add(lbl2, BorderLayout.EAST);
				lbl.setFont(list.getFont());
				lbl2.setFont(list.getFont());
			}
			if (isSelected)
			{
				Color bg = list.getSelectionBackground();
				Color fg = list.getSelectionForeground();
				pan.setBackground(bg);
				lbl.setBackground(bg);
				lbl.setForeground(fg);
				lbl2.setBackground(bg);
				lbl2.setForeground(fg);
			}
			else
			{
				Color bg = list.getBackground();
				Color fg = list.getForeground();
				pan.setBackground(bg);
				lbl.setBackground(bg);
				lbl.setForeground(fg);
				lbl2.setBackground(bg);
				lbl2.setForeground(UIConfigs.COLOR_TEXTHALF());
			}
			BPCacheDataFileSystem data = (BPCacheDataFileSystem) value;
			String p = data.getFullPath();
			String txt = data.getName();
			lbl.setText(txt);
			lbl2.setText((p.length() == 0 ? "" : " " + data.getFullPath() + ""));
			return pan;
		}
	}
}