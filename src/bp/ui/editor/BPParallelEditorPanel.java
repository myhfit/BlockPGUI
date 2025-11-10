package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.BPGUICore;
import bp.cache.BPCacheDataFileSystem;
import bp.cache.BPCacheDataResource;
import bp.cache.BPTreeCacheNode;
import bp.compare.BPDataComparator;
import bp.compare.BPDataComparator.BPDataCompareResult;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPDataContainer;
import bp.event.BPEventChannelUI;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.res.BPResourceDirLocal;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceFileSystemLocal;
import bp.tool.BPToolGUI;
import bp.tool.BPToolGUIParallelEditor;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.compare.BPComparableGUI;
import bp.ui.container.BPEditors.BPEventUIEditors;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSelectFormatEditor;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.event.BPResourceOperationCommonHandler;
import bp.ui.parallel.BPSyncGUI;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPGUIInfoPanel;
import bp.ui.scomp.BPPopupComboList;
import bp.ui.scomp.BPPopupComboList.BPPopupComboController;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIStd;
import bp.util.FileUtil;
import bp.util.LogicUtil;
import bp.util.ObjUtil;

public class BPParallelEditorPanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1832483509111282377L;

	protected String m_id;
	protected int m_channelid;
	protected int m_pchid;
	protected BPEventChannelUI m_pch;
	protected boolean m_needsave;
	protected boolean m_editable;

	protected JPanel m_mainp;
	protected List<BPEditorSubPanel> m_eps;
	protected BPToolBarSQ m_toolbar;
	protected Action m_actsync;

	public BPParallelEditorPanel()
	{
		m_eps = new ArrayList<BPEditorSubPanel>();
		initBPEvents();
		initUI();
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	protected void initBPEvents()
	{
		BPEventChannelUI channelui = new BPEventChannelUI();
		m_pchid = BPGUICore.EVENTS_UI.addChannel(channelui);
		m_pch = channelui;
	}

	protected void initUI()
	{
		m_toolbar = new BPToolBarSQ();
		m_mainp = new JPanel();
		BPAction actadd = BPAction.build("add").tooltip("Add").vIcon(BPIconResV.ADD()).callback(this::onAdd).getAction();
		BPAction actcompare = BPAction.build("compare").tooltip("Compare").vIcon(BPIconResV.LEFTRIGHT()).callback(this::onCompare).getAction();
		m_actsync = BPAction.build("sync").tooltip("Toggle Sync Status").vIcon(BPIconResV.REFRESH()).callback(this::onToggleSync).getAction();
		m_actsync.putValue(Action.SELECTED_KEY, true);
		Action[] acts = new Action[] { actadd, actcompare, m_actsync };
		m_toolbar.setActions(acts);

		m_toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 0)));
		setEditable(m_editable);
		GridLayout gl = new GridLayout(1, 2, 0, 0);
		m_mainp.setLayout(gl);
		setLayout(new BorderLayout());
		add(m_toolbar, BorderLayout.NORTH);
		add(m_mainp, BorderLayout.CENTER);
	}

	public void setEditable(boolean flag)
	{
		m_editable = flag;
		m_toolbar.setVisible(flag);
		if (flag)
		{
			m_mainp.setBorder(null);
		}
		else
		{
			m_mainp.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		}
	}

	public JPanel getComponent()
	{
		return this;
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
		return m_needsave;
	}

	public void setNeedSave(boolean needsave)
	{
		m_needsave = needsave;
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

	public void init2Editor()
	{
		addEditor(null);
		addEditor(null);
	}

	public BPEditorSubPanel addEditor(BPEditor<?> editor)
	{
		int newindex = m_eps.size();
		BPEditorSubPanel ep = new BPEditorSubPanel();
		ep.setEditable(m_editable);
		ep.setEditorIndex(newindex);
		m_eps.add(ep);

		changeGridSize(m_eps.size());
		if (m_eps.size() > 1)
			ep.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_TEXTQUARTER()));
		if (editor != null)
			ep.setEditor(editor);
		m_mainp.add(ep);
		m_mainp.updateUI();
		return ep;
	}

	public void batchAdd(BPResource[] ress, Function<BPResource, BPEditor<?>> editorcb)
	{
		if (editorcb != null)
		{
			for (BPResource res : ress)
				addEditor(editorcb.apply(res));
		}
		else
		{
			BPResource res0 = ress[0];
			String ext = res0.getExt();
			BPFormat format = ext == null ? null : BPFormatManager.getFormatByExt(ext);
			BPEditorFactory fac = null;
			BPConfig options = null;
			BPDialogSelectFormatEditor dlg2 = new BPDialogSelectFormatEditor();
			if (format != null)
				dlg2.setFormat(format);
			dlg2.setVisible(true);
			format = dlg2.getSelectedFormat();
			fac = dlg2.getSelectedEditorFactory();
			if (format == null && fac == null)
				return;
			options = dlg2.getEditorOptions();

			for (BPResource res : ress)
			{
				addEditor(null).loadEditorByResource(res, format, fac, options);
			}
		}
	}

	public void removeEditor(int index)
	{
		m_eps.get(index).clearEditor();
		int s = m_eps.size();
		if (s > 2)
		{
			m_eps.remove(index);
			m_mainp.remove(index);
			changeGridSize(m_eps.size());
			if (index < s - 1)
			{
				for (int i = index; i < s - 1; i++)
				{
					m_eps.get(i).setEditorIndex(i);
				}
			}
		}
		updateUI();
	}

	protected void changeGridSize(int s)
	{
		if (s < 2)
			s = 2;
		GridLayout gl = (GridLayout) m_mainp.getLayout();
		gl.setColumns(s);
	}

	public void clearResource()
	{
		List<BPEditorSubPanel> eps = m_eps;
		for (BPEditorSubPanel ep : eps)
			ep.clearResource();

		removeAll();

		BPGUICore.EVENTS_UI.removeChannel(m_pchid);
	}

	protected void onAdd(ActionEvent e)
	{
		addEditor(null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void onCompare(ActionEvent e)
	{
		int s = m_eps.size();
		BPEditor<?>[] es = new BPEditor<?>[s];
		for (int i = 0; i < s; i++)
		{
			es[i] = m_eps.get(i).getEditor();
		}
		BPDataComparator<?, ?> cp = null;
		BPComparableGUI<?, ?>[] cgs = new BPComparableGUI<?, ?>[s];
		for (int i = 0; i < s; i++)
		{
			if (es[i] instanceof BPComparableGUI)
			{
				BPComparableGUI<?, ?> cg = (BPComparableGUI<?, ?>) es[i];
				if (cp == null)
					cp = cg.getComparator();
				cgs[i] = cg;
			}
		}
		if (cp != null && cgs.length == s)
		{
			Object carr = null;
			{
				Object c0 = cgs[0].getCompareData();
				Class<?> c0cls = c0.getClass();
				carr = Array.newInstance(c0cls, cgs.length);
				for (int i = 0; i < cgs.length; i++)
				{
					Array.set(carr, i, i == 0 ? c0 : cgs[i].getCompareData());
				}
			}

			BPDataCompareResult[] results = ((BPDataComparator) cp).compare((Object[]) carr);
			List<BPEditor<?>> editors = new ArrayList<BPEditor<?>>();
			for (BPDataCompareResult r : results)
			{
				BPEditor<?> editor = makeResultEditor(r);
				editors.add(editor);
			}
			BPToolGUI tool = new BPToolGUIParallelEditor();
			tool.showTool(new Object[] { editors.toArray(new BPEditor<?>[0]) });
		}
	}

	protected BPEditor<?> makeResultEditor(BPDataCompareResult r)
	{
		BPCodePanel rc = new BPCodePanel();
		rc.setID(BPCore.genID(BPCore.getFileContext()));
		return rc;
	}

	protected void onToggleSync(ActionEvent e)
	{
		boolean v = !ObjUtil.toBool(m_actsync.getValue(Action.SELECTED_KEY), false);
		m_actsync.putValue(Action.SELECTED_KEY, v);
	}

	protected class BPEditorSubPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4500388618382478188L;

		protected BPEditor<?> m_editor;
		protected int m_editorindex;

		protected BPTextField m_txttitle;
		protected BPGUIInfoPanel m_paninfo;
		protected BPPopupComboController m_editorc;

		protected Consumer<String> m_dyncinfocb;
		protected BiConsumer<String, Boolean> m_statechangecb;

		protected Consumer<BPEventUIEditors> m_editorcb;
		protected Action m_actsave;
		protected JPanel m_pnltb;

		public BPEditorSubPanel()
		{
			m_txttitle = new BPTextField();
			m_paninfo = new BPGUIInfoPanel(false);
			m_editorc = new BPPopupComboController(this::listRes, this::getResName, (Consumer<BPCacheDataResource>) this::selectResource);

			m_dyncinfocb = this::onDynamicInfo;
			m_statechangecb = this::onEditorStateChanged;

			BPToolBarSQ tbctrl = new BPToolBarSQ();
			JPanel pnltxt = new JPanel();
			m_pnltb = new JPanel();
			JPanel pnlbottom = new JPanel();
			BPPopupComboList pop = new BPPopupComboList();

			m_txttitle.setMonoFont();
			m_paninfo.setEditorDynamicInfo(" ");

			pnltxt.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_TEXTQUARTER()));
			m_pnltb.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			pnlbottom.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
			tbctrl.setMinimumSize(new Dimension(0, UIConfigs.BAR_HEIGHT_VICON()));
			tbctrl.setPreferredSize(null);
			tbctrl.setActions(getEditorAction(null), this);

			pop.bind(m_txttitle, m_editorc);

			setLayout(new BorderLayout());
			pnltxt.setLayout(new BorderLayout());
			m_pnltb.setLayout(new BorderLayout());
			pnlbottom.setLayout(new BorderLayout());

			pnltxt.add(m_txttitle, BorderLayout.CENTER);
			m_pnltb.add(pnltxt, BorderLayout.CENTER);
			m_pnltb.add(tbctrl, BorderLayout.EAST);
			pnlbottom.add(m_paninfo, BorderLayout.EAST);
			add(m_pnltb, BorderLayout.NORTH);
			add(pnlbottom, BorderLayout.SOUTH);

			m_editorcb = this::onEditorStatusChanged;
			m_pch.on(BPEventUIResourceOperation.EVENTKEY_RES_OP, (Consumer<BPEventUIResourceOperation>) BPResourceOperationCommonHandler::onResourceOperationEvent);
			m_pch.on(BPEventUIEditors.EVENTKEY_EDITORS, m_editorcb);
		}

		public void clearEditor()
		{
			BPEditor<?> editor = m_editor;
			clearResource();
			if (editor != null)
				remove(editor.getComponent());
			setTitle("");
			m_paninfo.setEditorDynamicInfo(" ");
			m_paninfo.setEditorInfo(" ");
		}

		public void setEditable(boolean flag)
		{
			m_pnltb.setVisible(flag);
		}

		public void clearResource()
		{
			BPEditor<?> editor = m_editor;
			if (editor != null)
			{
				if (editor instanceof BPSyncGUI)
					((BPSyncGUI) editor).stopSync();
				editor.clearResource();
			}
		}

		public BPEditor<?> getEditor()
		{
			return m_editor;
		}

		protected Action[] getEditorAction(BPEditor<?> editor)
		{
			BPAction actcreate = BPAction.build("create").tooltip("Create Editor").vIcon(BPIconResV.ADD()).callback(this::createEditor).getAction();
			BPAction actopen = BPAction.build("open").tooltip("Open").vIcon(BPIconResV.DOC()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)).callback(this::loadEditor).getAction();
			m_actsave = BPAction.build("save").tooltip("Save").vIcon(BPIconResV.SAVE()).callback(this::onSave).getAction();
			BPAction actclose = BPAction.build("close").tooltip("Close").vIcon(BPIconResV.KILL()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK)).callback(e -> removeEditor(m_editorindex)).getAction();
			return new Action[] { actcreate, actopen, m_actsave, actclose };
		}

		protected void selectResource(BPCacheDataResource rescache)
		{
			String filename = rescache.getFullName();
			BPResourceFileSystemLocal res = FileUtil.isDir(filename) ? new BPResourceDirLocal(filename) : new BPResourceFileLocal(filename);
			loadEditorByResource(res, null, null, null);
		}

		public void loadEditorByResource(BPResource res, BPFormat format, BPEditorFactory fac, BPConfig options)
		{
			if (res == null)
				return;
			String ext = res.getExt();
			String resid = res.openWithTempID() ? BPCore.genID(BPCore.getFileContext()) : res.getID();
			format = (format != null ? format : BPFormatManager.getFormatByExt(ext));
			fac = (fac != null ? fac : BPEditorManager.getFactory(format.getName()));
			if (fac == null)
			{
				UIStd.info("No Editor for " + format.getName());
				return;
			}
			BPEditor<?> editor = fac.createEditor(format, res, null);
			if (editor == null)
				return;
			editor.setChannelID(m_channelid);
			editor.setID(BPCore.genID(BPCore.getFileContext()) + ":" + resid);
			editor.setOnStateChanged(m_statechangecb);
			editor.setOnDynamicInfo(m_dyncinfocb);
			fac.initEditor(editor, format, res, options);
			if (editor instanceof BPTextEditor)
			{
				BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
				teditor.getTextPanel().resizeDoc();
			}
			setEditor(editor);
		}

		public void createEditor(ActionEvent e)
		{
			BPDialogSelectFormatEditor dlg = new BPDialogSelectFormatEditor();
			dlg.setVisible(true);
			BPFormat format = dlg.getSelectedFormat();
			BPEditorFactory fac = dlg.getSelectedEditorFactory();
			if (format == null && fac == null)
				return;
			BPConfig options = dlg.getEditorOptions();
			BPEditor<?> editor = fac.createEditor(format, null, options);
			if (editor == null)
				return;
			editor.setChannelID(m_channelid);
			editor.setID(BPCore.genID(BPCore.getFileContext()));
			editor.setOnStateChanged(m_statechangecb);
			editor.setOnDynamicInfo(m_dyncinfocb);
			fac.initEditor(editor, format, null, options);

			if (editor instanceof BPTextEditor)
			{
				BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
				teditor.getTextPanel().resizeDoc();
			}
			editor.focusEditor();
			setEditor(editor);
		}

		public void loadEditor(ActionEvent e)
		{
			BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
			dlg.showOpen();
			BPResource res = dlg.getSelectedResource();
			if (res != null)
			{
				String ext = res.getExt();
				BPFormat format = ext == null ? null : BPFormatManager.getFormatByExt(ext);
				BPEditorFactory fac = null;
				BPConfig options = null;
				BPDialogSelectFormatEditor dlg2 = new BPDialogSelectFormatEditor();
				if (format != null)
					dlg2.setFormat(format);
				dlg2.setVisible(true);
				format = dlg2.getSelectedFormat();
				fac = dlg2.getSelectedEditorFactory();
				if (format == null && fac == null)
					return;
				options = dlg2.getEditorOptions();

				loadEditorByResource(res, format, fac, options);
			}
		}

		public void onSave(ActionEvent e)
		{
			m_editor.save();
		}

		protected List<BPCacheDataResource> listRes(String txt)
		{
			List<BPCacheDataResource> rc = new ArrayList<BPCacheDataResource>();
			List<BPTreeCacheNode<BPCacheDataFileSystem>> resnodes = BPCore.FS_CACHE.searchFileByName(txt, null, 10);
			for (BPTreeCacheNode<BPCacheDataFileSystem> node : resnodes)
			{
				rc.add(node.getValue());
			}
			return rc;
		}

		protected String getResName(Object res)
		{
			return ((BPCacheDataResource) res).getName();
		}

		protected void onDynamicInfo(String info)
		{
			m_paninfo.setEditorDynamicInfo(info);
			if (m_editor != null)
				m_paninfo.setEditorInfo(m_editor.getEditorInfo());
		}

		protected void onEditorStateChanged(String innereditorid, boolean needsave)
		{

		}

		protected void onEditorStatusChanged(BPEventUIEditors e)
		{
			m_paninfo.setEditorInfo(e.getTitle());
		}

		public void setEditorIndex(int editorindex)
		{
			m_editorindex = editorindex;
		}

		public void setEditor(BPEditor<?> editor)
		{
			BPEditor<?> oldeditor = m_editor;
			if (oldeditor != null)
			{
				if (oldeditor instanceof BPSyncGUI)
					((BPSyncGUI) oldeditor).stopSync();
				oldeditor.clearResource();
				oldeditor.setOnDynamicInfo(null);
				oldeditor.setOnStateChanged(null);
				remove(oldeditor.getComponent());
				m_paninfo.setEditorDynamicInfo(" ");
				m_paninfo.setEditorInfo(" ");
			}
			m_editor = editor;

			setTitle(getEditorName(editor));

			if (editor != null)
			{
				editor.setChannelID(m_pchid);
				editor.setOnDynamicInfo(m_dyncinfocb);
				editor.setOnStateChanged(m_statechangecb);
				add(editor.getComponent(), BorderLayout.CENTER);
				m_paninfo.setEditorInfo(editor.getEditorInfo());
				if (editor instanceof BPSyncGUI)
					((BPSyncGUI) editor).startSync();
			}
			updateUI();
		}

		public void setTitle(String text)
		{
			m_editorc.blockpopup = true;
			try
			{
				m_txttitle.setText(text);
			}
			finally
			{
				m_editorc.blockpopup = false;
			}
		}

		protected String getEditorName(BPEditor<?> editor)
		{
			String rc = null;
			if (editor != null)
			{
				if (editor instanceof BPViewer)
				{
					BPViewer<?> v = (BPViewer<?>) editor;
					String resname = LogicUtil.CHAIN_NN(v, v2 -> ((BPViewer<?>) v2).getDataContainer(), dc -> ((BPDataContainer) dc).getResource(), res -> ((BPResource) res).getName());
					if (resname == null)
						rc = "untitled";
					else
						rc = resname;
				}
			}
			return rc;
		}
	}
}
