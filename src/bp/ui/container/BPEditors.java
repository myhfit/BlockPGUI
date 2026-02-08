package bp.ui.container;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.Icon;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfig;
import bp.data.BPDataContainer;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.event.BPEventUI;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.ui.BPComponent;
import bp.ui.BPViewer;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.editor.BPTextEditor;
import bp.ui.frame.BPFrameComponent;
import bp.ui.scomp.BPTabBar;
import bp.ui.scomp.BPTabBar.Tab;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil;

public class BPEditors extends BPTabbedContainerBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8394178581468798190L;

	protected int m_channelid;
	protected Consumer<String> m_dynainfocb = this::onEditorDynamicInfo;
	protected BiConsumer<String, Boolean> m_statecb = this::onEditorStateChanged;
	protected Consumer<ActionEvent> m_morecb = this::onMore;
	protected BiConsumer<String, String> m_mnucb = this::onMenuAction;

	public BPEditors()
	{
		getTabBar().initMoreButton(m_morecb);
	}

	protected void onEditorStateChanged(String id, boolean flag)
	{
		if (id == null)
			return;
		BPComponent<?> comp = m_compmap.get(id);
		if (comp instanceof BPViewer)
		{
			BPDataContainer con = ((BPViewer<?>) comp).getDataContainer();
			if (con != null)
				m_tabbar.setTitle(id, (flag ? "*" : "") + (con == null ? "" : con.getTitle()));
		}
	}

	protected void onEditorDynamicInfo(String info)
	{
		BPEventUIEditors event = new BPEventUIEditors(BPEventUIEditors.EDITOR_DYNAMICINFO);
		event.datas = new Object[] { info };
		BPGUICore.EVENTS_UI.trigger(m_channelid, event);
	}

	public void newEditorCustom(BPResourceFileSystem file, BPFormat _format, BPEditorFactory _fac, String routecontainerid, BPConfig options, Object... params)
	{
		String id = file.getTempID();
		if (id == null)
		{
			id = BPCore.genID(BPCore.getFileContext());
			file.setTempID(id);
		}
		BPComponent<?> comp = m_compmap.get(id);
		if (comp == null)
		{
			String ext = file.getExt();
			BPFormat format = _format == null ? BPFormatManager.getFormatByExt(ext) : _format;
			BPEditorFactory fac = _fac == null ? BPEditorManager.getFactory(format.getName()) : _fac;
			BPEditor<?> editor = fac.createEditor(format, file, options, params);
			if (editor == null)
				return;
			editor.setChannelID(m_channelid);
			editor.setID(id);
			editor.setOnStateChanged(m_statecb);
			editor.setOnDynamicInfo(m_dynainfocb);
			fac.initEditor(editor, format, file, options);
			comp = editor;
			m_compmap.put(id, comp);
			if (comp instanceof BPViewer)
			{
				BPDataContainer con = ((BPViewer<?>) comp).getDataContainer();
				String title;
				if (con != null)
				{
					title = "*" + con.getTitle();
				}
				else
				{
					String editorname = editor.getEditorName();
					title = editorname == null ? "*" : editorname;
				}
				addTab(id, title, (Icon) null, editor.getComponent());
			}
			else
			{
				String editorname = editor.getEditorName();
				if (editorname == null)
					editorname = "New " + format.getName();
				addTab(id, editorname, (Icon) null, editor.getComponent());
			}

			switchTab(id);
			editor.setNeedSave(true);
			if (editor instanceof BPTextEditor)
			{
				BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
				teditor.getTextPanel().resizeDoc();
			}
			editor.focusEditor();
		}
	}

	public void newEdtior(BPResourceFile file, Object... params)
	{
		newEditorCustom(file, null, null, null, null, params);
	}

	public void open(BPResource file)
	{
		open(file, null, null, null, null);
	}

	public void open(BPResource res, BPFormat fformat, BPEditorFactory ffac, String routecontainerid, BPConfig config)
	{
		String id = res.openWithTempID() ? BPCore.genID(BPCore.getFileContext()) : res.getID();
		BPComponent<?> comp = m_compmap.get(id);
		if (comp == null || (ffac != null && !ffac.checkSameTab()))
		{
			String ext = res.getExt();
			BPFormat format = (fformat != null ? fformat : BPFormatManager.getFormatByExt(ext));
			BPEditorFactory fac = (ffac != null ? ffac : BPEditorManager.getFactory(format.getName()));
			if (fac == null)
			{
				UIStd.info("No Editor for " + format.getName());
				return;
			}
			BPEditor<?> editor = fac.createEditor(format, res, null);
			if (editor == null)
				return;
			editor.setChannelID(m_channelid);
			editor.setID(id);
			editor.setOnStateChanged(m_statecb);
			editor.setOnDynamicInfo(m_dynainfocb);
			fac.initEditor(editor, format, res, config);
			if (res.isRoutable() && editor.isRoutable())
			{
				BPComponent<?> cur = getCurrent();
				if (routecontainerid != null && cur != null && cur.isRoutableContainer() && routecontainerid.equals(((BPRoutableContainer<?>) cur).getID()))
				{
					BPRoutableContainer<?> par = (BPRoutableContainer<?>) cur;
					par.addRoute(id, res.getName(), editor);
				}
				else
				{
					BPRoutableContainerBase par = new BPRoutableContainerBase();
					par.addRoute(id, res.getName(), editor);
					String parid = BPCore.genID(BPCore.getFileContext());
					par.setID(parid);
					m_compmap.put(parid, par);
					addTab(parid, res.getName(), (Icon) null, par.getComponent());
					switchTab(parid);
				}
			}
			else
			{
				comp = editor;
				m_compmap.put(id, comp);
				addTab(id, res.getName(), (Icon) null, editor.getComponent());
				switchTab(id);
			}
			if (editor instanceof BPTextEditor)
			{
				BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
				teditor.getTextPanel().resizeDoc();
			}
			// if (editor.needActiveOnStart())
			// {
			// editor.activeEditor();
			// }
			editor.focusEditor();
		}
		else
		{
			switchTab(id);
		}
	}

	public void save()
	{
		String selid = m_tabbar.getSelectedID();
		if (selid != null)
		{
			BPComponent<?> comp = m_compmap.get(selid);
			if (comp != null && comp instanceof BPEditor)
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
	}

	public BPComponent<?> getCurrent()
	{
		String selid = m_tabbar.getSelectedID();
		if (selid != null)
			return m_compmap.get(selid);
		return null;
	}

	public void showSwitchEditor()
	{
		List<BPTabBar.Tab> tabs = m_tabbar.getTabs();
		BPTabBar.Tab seltab = UIStd.select(tabs, UIUtil.wrapBPTitle(BPActionConstCommon.TXT_SELEDITOR), tab -> ((BPTabBar.Tab) tab).title + " (" + ((BPTabBar.Tab) tab).id + ")");
		if (seltab != null)
		{
			switchTab(seltab.id);
		}
	}

	protected void onMore(ActionEvent e)
	{
		showSwitchEditor();
	}

	@SuppressWarnings("unchecked")
	public void saveAs()
	{
		String selid = m_tabbar.getSelectedID();
		if (selid != null)
		{
			BPComponent<?> comp = m_compmap.get(selid);
			if (comp != null && comp instanceof BPViewer)
			{
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
						((BPViewer<BPTextContainer>) comp).rebind(con);
						BPEditor<?> editor = (BPEditor<?>) comp;
						editor.setID(newid);
						m_compmap.remove(selid);
						m_compmap.put(newid, editor);
						m_cps.remove(selid);
						m_cps.put(newid, editor.getComponent());
						m_tabbar.updateTitle(selid, newid, con.getTitle());
						m_tabbar.getSelectedTab().id = newid;
						try
						{
							editor.save();
							success = true;
						}
						finally
						{
							tabSwitched(newid);
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
						((BPViewer<BPDataContainer>) comp).rebind(con);
						BPEditor<?> editor = (BPEditor<?>) comp;
						editor.setID(newid);
						m_compmap.remove(selid);
						m_compmap.put(newid, editor);
						m_cps.remove(selid);
						m_cps.put(newid, editor.getComponent());
						m_tabbar.updateTitle(selid, newid, con.getTitle());
						m_tabbar.getSelectedTab().id = newid;
						try
						{
							editor.save();
							success = true;
						}
						finally
						{
							tabSwitched(newid);
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
		}
	}

	public void addComponentTab(String id, String title, BPComponent<?> comp)
	{
		addTab(id, title, (Icon) null, comp.getComponent());
		m_compmap.put(id, comp);
	}

	public void setEventChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	protected void tabSwitched(String id)
	{
		BPComponent<?> comp = null;
		String title = null;
		if (id != null)
		{
			comp = m_compmap.get(id);
			if (comp instanceof BPEditor)
			{
				((BPEditor<?>) comp).activeEditor();
			}
			title = m_tabbar.getTitle(id);
		}
		BPEventUIEditors event = new BPEventUIEditors(BPEventUIEditors.EDITOR_SWITCH, id, comp, title);
		BPGUICore.EVENTS_UI.trigger(m_channelid, event);
	}

	protected void tabRemoved(String id)
	{
		BPEventUIEditors event = new BPEventUIEditors(BPEventUIEditors.EDITOR_CLOSED, id, null, null);
		BPGUICore.EVENTS_UI.trigger(m_channelid, event);
	}

	public boolean isCurrent(String tabid)
	{
		if (tabid == null)
			return false;
		return tabid.equals(m_tabbar.getSelectedID());
	}

	protected void initTab(Tab tab)
	{
		tab.pan.setMenu(new Object[][] { new Object[] { "Close", "close" }, new Object[] { "Close All", "closeall" }, new Object[] { "Close Others", "closeother" }, new Object[] { "-", null, (Predicate<String>) this::canSplit },
				new Object[] { "Split(New Window)", "split", (Predicate<String>) this::canSplit } }, m_mnucb);
	}

	protected boolean canSplit(String id)
	{
		BPComponent<?> comp = m_compmap.get(id);
		if (comp != null)
		{
			return true;
		}
		return false;
	}

	protected void splitEditor(String id)
	{
		Map<String, BPComponent<?>> cm = getComponentMap();
		BPComponent<?> comp = (BPComponent<?>) cm.remove(id);
		doRemoveTab(id);
		if (comp.isRoutableContainer())
		{
			BPRoutableContainer<?> rcomp = (BPRoutableContainer<?>) comp;
			comp = rcomp.getCurrent();
		}
		BPFrameComponent fe = new BPFrameComponent();
		fe.setComponent(comp);
		fe.setVisible(true);
	}

	public List<BPComponent<?>> getEditorList()
	{
		List<BPComponent<?>> rc = new ArrayList<BPComponent<?>>();
		List<Tab> tabs = m_tabbar.getTabs();
		for (Tab t : tabs)
		{
			String id = t.id;
			if (id != null)
				rc.add(m_compmap.get(t.id));
		}
		return rc;
	}

	protected void onMenuAction(String id, String key)
	{
		switch (key)
		{
			case "close":
			{
				closeBPTab(id);
				break;
			}
			case "closeall":
			{
				closeAllTabs();
				break;
			}
			case "closeother":
			{
				closeOther(id);
				break;
			}
			case "split":
			{
				splitEditor(id);
				break;
			}
		}
	}

	public static class BPEventUIEditors extends BPEventUI
	{
		public final static String EVENTKEY_EDITORS = "E_UI_EDITORS";

		public final static String EDITOR_SWITCH = "EDITOR_SWTICH";
		public final static String EDITOR_DYNAMICINFO = "EDITOR_DYNAINFO";
		public final static String EDITOR_CLOSED = "EDITOR_CLOSED";

		public final static String EDITOR_STATUS_CHANGED = "EDITOR_STATUS_CHANGED";

		public BPEventUIEditors(String subkey)
		{
			this.key = EVENTKEY_EDITORS;
			this.subkey = subkey;
		}

		public BPEventUIEditors(String subkey, String id, BPComponent<?> comp, String title)
		{
			this.key = EVENTKEY_EDITORS;
			this.subkey = subkey;
			this.datas = new Object[] { id, comp, title };
		}

		public BPComponent<?> getBPComponent()
		{
			return (BPComponent<?>) datas[1];
		}

		public String getTabID()
		{
			return (String) datas[0];
		}

		public String getTitle()
		{
			return (String) datas[2];
		}
	}
}
