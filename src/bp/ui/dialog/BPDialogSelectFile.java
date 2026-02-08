package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.event.BPEventChannelUI;
import bp.event.BPEventCoreUI;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.tree.BPPathTreeLocalFuncs;
import bp.ui.tree.BPPathTreeNodeCommonHandler;
import bp.ui.tree.BPPathTreePanel;
import bp.ui.tree.BPProjectsTreeFuncs;
import bp.ui.tree.BPTreeFuncs;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.util.UIUtil;

public class BPDialogSelectFile extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3556973484245269229L;

	protected Consumer<BPEventUIPathTree> m_pathtreehandler;
	protected BPPathTreePanel m_ptree;
	protected BPTextField m_filebox;
	protected int m_channelid;
	protected BPResourceFile m_file;
	protected BPPathTreeNodeCommonHandler m_ptreehandler;

	public BPDialogSelectFile()
	{

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
					if (!event.getSelectedResource().isLeaf())
						m_filebox.setText("");
					else
						m_filebox.setText(event.getSelectedResource().getName());
					break;
				}
				case BPEventUIPathTree.NODE_ACTION:
				{
					m_ptreehandler.onPathTreeEvent(event);
					break;
				}
			}
		};
		channelui.on(BPEventUIPathTree.EVENTKEY_PATHTREE, m_pathtreehandler);
	}

	protected void initUIComponents()
	{
		m_ptree = new BPPathTreePanel();
		m_ptree.setEventChannelID(m_channelid);
		m_ptree.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
		m_ptree.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
		m_ptree.setPathTreeFuncs(new BPPathTreeLocalFuncs(m_channelid));
		BPLabel lblfilename = new BPLabel();
		m_filebox = new BPTextField();
		m_ptreehandler = new BPPathTreeNodeCommonHandler(m_ptree.getTreeComponent());

		m_filebox.setMonoFont();
		lblfilename.setLabelFont();
		lblfilename.setText("Filename:");
		lblfilename.setOpaque(false);
		lblfilename.setBackground(UIConfigs.COLOR_TEXTBG());
		lblfilename.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 2)));

		JPanel mainp = new JPanel();
		JPanel filenamep = new JPanel();

		filenamep.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		mainp.setLayout(new BorderLayout());
		filenamep.setLayout(new BorderLayout());
		filenamep.add(m_filebox, BorderLayout.CENTER);
		filenamep.add(lblfilename, BorderLayout.WEST);

		mainp.add(m_ptree, BorderLayout.CENTER);
		mainp.add(filenamep, BorderLayout.SOUTH);

		setLayout(new BorderLayout());

		add(mainp, BorderLayout.CENTER);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);

		m_ptree.refreshContextPath();
		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_ptree.getCoreUIRefreshPathTreeHandler());

		setTitle(UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_FILE));
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

	public void clearSubComponents()
	{
		m_ptree = null;
	}

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				BPResource res = m_ptree.getSelectedResource();
				if (res == null)
					return true;
				BPResource rc = null;
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
							rc = ((BPResourceDir) res.getParentResource()).getChild(filename, false);
						}
					}
				}
				if (rc != null)
				{
					m_file = (BPResourceFile) rc;
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

	public BPResourceFile getSelectedFile()
	{
		return m_file;
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
}
