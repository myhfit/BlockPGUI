package bp.ui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.data.BPDataContainer;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.event.BPEventChannelUI;
import bp.format.BPFormat;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.BPComponent;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.container.BPEditors.BPEventUIEditors;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPTextEditor;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.event.BPResourceOperationCommonHandler;
import bp.ui.scomp.BPGUIInfoPanel;
import bp.ui.scomp.BPMenu;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil;

public class BPFrameComponent extends BPFrame implements WindowListener, BPFrameHostIFC
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4214702754144970453L;

	protected BPComponent<?> m_comp;
	protected BPGUIInfoPanel m_paninfo;
	protected Consumer<String> m_dinfocb;
	protected BiConsumer<String, Boolean> m_statecb;
	protected Consumer<BPEventUIEditors> m_editorcb;

	protected int m_channelid;

	protected JMenuBar m_mnubar;
	protected JPanel m_mnuactbar;

	protected BPMenu m_mnuedit;
	protected BPAction m_actsave;

	public BPFrameComponent()
	{
		m_dinfocb = this::onDInfo;
		m_statecb = this::onSInfo;
	}

	public void setPrefers()
	{
		setPreferredSize(UIUtil.getPercentDimension(0.8f, 0.8f));
		super.setPrefers();
	}

	protected void initUIComponents()
	{
		JPanel pbtn = new JPanel();
		m_paninfo = new BPGUIInfoPanel(false);
		pbtn.setPreferredSize(new Dimension(10, UIUtil.scale(18)));

		m_mnubar = new JMenuBar();
		BPMenu mnufile = new BPMenu("File");
		m_mnuedit = new BPMenu("Edit");
		BPMenu mnuview = new BPMenu("View");
		m_mnuactbar = new JPanel();

		mnufile.setMnemonic(KeyEvent.VK_F);
		m_actsave = BPAction.build("Save").callback(e -> save()).mnemonicKey(KeyEvent.VK_S).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)).getAction();
		BPAction actclose = BPAction.build("Close").callback(e -> dispose()).mnemonicKey(KeyEvent.VK_X).getAction();
		BPAction acttogglerightpan = BPAction.build("Toggle Right Panel").callback(e -> toggleRightPanel()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK)).getAction();
		BPAction actfullscreen = BPAction.build("FullScreen").callback(e -> fullScreen()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0)).getAction();

		FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
		fl.setVgap(0);
		fl.setHgap(0);
		m_mnuactbar.setMinimumSize(new Dimension(0, 0));
		m_mnuactbar.setBorder(new EmptyBorder(2, 0, 2, 0));
		m_mnuactbar.setOpaque(false);
		m_mnuactbar.setLayout(fl);

		mnufile.add(m_actsave);
		mnufile.addSeparator();
		mnufile.add(actclose);

		mnuview.add(acttogglerightpan);
		mnuview.add(actfullscreen);

		m_mnubar.add(mnufile);
		m_mnubar.add(m_mnuedit);
		m_mnubar.add(mnuview);
		m_mnubar.add(m_mnuactbar);
		m_mnubar.setVisible(false);

		setJMenuBar(m_mnubar);

		pbtn.setLayout(new BorderLayout());
		pbtn.add(m_paninfo, BorderLayout.EAST);
		pbtn.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_TEXTHALF()));

		setLayout(new BorderLayout());
		add(pbtn, BorderLayout.SOUTH);
		addWindowListener(this);
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
		m_editorcb = this::onEditorStatusChanged;
		channelui.on(BPEventUIResourceOperation.EVENTKEY_RES_OP, (Consumer<BPEventUIResourceOperation>) BPResourceOperationCommonHandler::onResourceOperationEvent);
		channelui.on(BPEventUIEditors.EVENTKEY_EDITORS, m_editorcb);
	}

	protected void onEditorStatusChanged(BPEventUIEditors e)
	{
		m_paninfo.setEditorInfo(e.getTitle());
	}

	public void setComponent(BPComponent<?> comp)
	{
		m_comp = comp;
		add(comp.getComponent(), BorderLayout.CENTER);
		if (comp instanceof BPEditor)
		{
			BPEditor<?> editor = (BPEditor<?>) comp;
			editor.setChannelID(m_channelid);

			m_mnubar.setVisible(true);
			m_actsave.setEnabled(false);

			if (comp instanceof BPViewer)
			{
				BPViewer<? extends BPDataContainer> viewer = (BPViewer<?>) comp;
				BPDataContainer con = viewer.getDataContainer();
				String title;
				if (con != null)
				{
					BPResource res = con.getResource();
					if (res != null)
						m_actsave.setEnabled(res.isIO());
					title = con.getTitle();
					if (res != null && res.isFileSystem() && ((BPResourceFileSystem) res).getTempID() != null)
						title = "*" + title;
				}
				else
				{
					String editorname = editor.getEditorName();
					title = editorname == null ? "*" : editorname;
				}
				setTitle(BPGUICore.S_BP_TITLE + " - " + title);
			}
			else
			{
				String editorname = editor.getEditorName();
				if (editorname != null)
					setTitle(BPGUICore.S_BP_TITLE + " - " + editorname);
			}

			validate();

			Action[] editmenuacts = editor.getEditMenuActions();
			UIUtil.rebuildMenu(m_mnuedit, editmenuacts, true);
			Action[] actbaracts = editor.getActBarActions();
			setActBarActions(actbaracts);

			m_paninfo.setEditorInfo(editor.getEditorInfo());
			editor.setOnStateChanged(m_statecb);
			editor.setOnDynamicInfo(m_dinfocb);
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

	protected void onSInfo(String id, boolean flag)
	{
		BPEditor<?> editor = (BPEditor<?>) m_comp;
		if (editor instanceof BPViewer)
		{
			BPDataContainer con = ((BPViewer<?>) editor).getDataContainer();
			if (con != null)
				setTitle(BPGUICore.S_BP_TITLE + " - " + (flag ? "*" : "") + (con == null ? "" : con.getTitle()));
		}
	}

	protected void save()
	{
		BPComponent<?> comp = m_comp;
		if (comp instanceof BPEditor)
		{
			BPEditor<?> editor = (BPEditor<?>) comp;
			if (!(editor instanceof BPViewer))
				return;
			BPDataContainer con = ((BPViewer<?>) editor).getDataContainer();
			if (con == null)
				return;
			BPResource res = con.getResource();
			if (res == null || res.isFileSystem() && ((BPResourceFileSystem) res).getTempID() != null)
				saveAs();
			else
			{
				editor.save();
				BPResource respar = res.getParentResource();
				CommonUIOperations.refreshPathTree(respar, false);
				CommonUIOperations.refreshResourceCache(respar);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void saveAs()
	{
		BPComponent<?> comp = m_comp;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
		String[] exts = null;
		String rext = LogicUtil.CHAIN_NN(comp, c -> ((BPViewer<?>) c).getDataContainer(), con -> ((BPDataContainer) con).getResource(), r -> ((BPResource) r).getExt());
		if (rext != null)
			exts = new String[] { rext };
		else if (comp instanceof BPEditor)
			exts = ((BPEditor<?>) comp).getExts();
		dlg.showSave(exts);
		BPResource file = dlg.getSelectedResource();
		if (file != null)
		{
			boolean success = false;
			String newid = file.getID();
			if (comp instanceof BPTextEditor)
			{
				BPTextContainer con = new BPTextContainerBase();
				con.bind(file);
				((BPViewer<BPTextContainer>) comp).bind(con, true);
				BPEditor<?> editor = (BPEditor<?>) comp;
				editor.setID(newid);
				try
				{
					editor.save();
					success = true;
				}
				finally
				{
				}
			}
			else if (comp instanceof BPEditor)
			{
				BPDataContainer con = ((BPEditor<?>) comp).createDataContainer(file);
				if (con == null)
				{
					UIStd.info("This Editor can't Save as");
					return;
				}
				((BPViewer<BPDataContainer>) comp).bind(con, true);
				BPEditor<?> editor = (BPEditor<?>) comp;
				editor.setID(newid);
				try
				{
					editor.save();
					success = true;
				}
				finally
				{
				}
			}
			if (success)
			{
				BPResource respar = file.getParentResource();
				CommonUIOperations.refreshPathTree(respar, false);
				CommonUIOperations.refreshResourceCache(respar);
			}
		}
	}

	protected void onDInfo(String info)
	{
		BPEditor<?> editor = (BPEditor<?>) m_comp;
		m_paninfo.setEditorDynamicInfo(info);
		m_paninfo.setEditorInfo(editor.getEditorInfo());
	}

	protected void initDatas()
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
		if (!isVisible())
		{
			BPComponent<?> comp = m_comp;
			m_comp = null;
			if (comp != null)
				comp.clearResource();
		}
	}

	public void windowIconified(WindowEvent e)
	{
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

	public void createEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params)
	{
		CommonUIOperations.openFileNewWindow(filename, format, facname, optionsdata, params);
	}

	public void openEditorByFileSystem(String filename, String format, String facname, Map<String, Object> optionsdata, Object... params)
	{
		CommonUIOperations.openFileNewWindow(filename, format, facname, optionsdata, params);
	}

	public void openResource(BPResource res, BPFormat format, BPEditorFactory fac, boolean isselected, String rconid)
	{
		CommonUIOperations.openResourceNewWindow(res, format, fac, rconid, null);
	}

	public void toggleRightPanel()
	{
		if (m_comp instanceof BPEditor)
		{
			BPEditor<?> editor = (BPEditor<?>) m_comp;
			editor.toggleRightPanel();
		}
	}

	public List<BPComponent<?>> getEditorList()
	{
		List<BPComponent<?>> rc = new ArrayList<BPComponent<?>>();
		rc.add(m_comp);
		return rc;
	}
}