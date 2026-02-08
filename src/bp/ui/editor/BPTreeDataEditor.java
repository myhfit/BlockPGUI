package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerFactory;
import bp.data.BPMData.BPMDataWMapOrdered;
import bp.data.BPTreeData;
import bp.data.BPTreeData.BPTreeDataObj;
import bp.data.BPTreeDataContainer;
import bp.data.BPTreeDataHolder;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.ui.BPViewer;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHolder;
import bp.ui.actions.BPTreeDataCloneActions;
import bp.ui.actions.BPTreeDataEditorActions;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogForm;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.tree.BPTreeCellRendererObject;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncsObject;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.JSONUtil;

public class BPTreeDataEditor<CON extends BPTreeDataContainer> extends JPanel implements BPEditor<JPanel>, BPViewer<CON>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6643778268900534375L;

	protected String m_id;
	protected int m_channelid;
	protected CON m_con;

	protected WeakReference<Consumer<String>> m_dynainfo;

	protected JScrollPane m_scroll;
	protected BPToolBarSQ m_toolbar;
	protected BPTreeComponentBase m_tree;
	protected BPTreeData m_treedata;

	protected BPActionHolder m_acts;

	protected boolean m_needsave;

	private WeakReference<BiConsumer<String, Boolean>> m_statehandler;

	public BPTreeDataEditor()
	{
		init();
	}

	protected void init()
	{
		m_acts = new BPTreeDataEditorActions(this);
		m_scroll = new JScrollPane();
		m_tree = new BPTreeComponentBase();
		m_tree.setRootVisible(false);
		m_tree.setTreeFont();
		m_tree.setCellRenderer(new BPTreeCellRendererObject());
		m_toolbar = new BPToolBarSQ();
		JPanel toppnl = new JPanel();

		m_toolbar.setBorder(new EmptyBorder(1, 1, 2, 1));
		m_toolbar.setActions(m_acts.getActions());
		m_scroll.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_scroll.setViewportView(m_tree);
		m_tree.setMonoFont();

		setLayout(new BorderLayout());
		toppnl.setLayout(new BorderLayout());
		toppnl.add(m_toolbar, BorderLayout.CENTER);
		add(m_scroll, BorderLayout.CENTER);
		add(toppnl, BorderLayout.NORTH);
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.TABLE;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public BPDataContainer createDataContainer(BPResource res)
	{
		BPFormat format = BPFormatManager.getFormatByExt(res.getExt());
		BPTreeDataContainer con = null;
		if (format != null)
		{
			BPDataContainerFactory fac = ClassUtil.findService(BPDataContainerFactory.class, f -> f.canHandle(format.getName()));
			con = fac.createContainer(null);
			con.bind(res);
		}
		return con;
	}

	@SuppressWarnings("unchecked")
	public void bind(CON con, boolean noread)
	{
		boolean hasold = m_con != null;
		m_con = con;
		if (con == null)
		{
			BPTreeDataHolder holder = new BPTreeDataHolder();
			holder.setTitle("temptree");
			BPTreeDataObj treedata = new BPTreeDataObj();
			treedata.setRoot(new HashMap<String, Object>());
			holder.setData(treedata);
			m_con = (CON) holder;
			holder.open();
			setTreeData(treedata);
		}
		else if (!noread && con.canOpen())
		{
			con.open();
			setTreeData(con.readTreeData());
		}
		else if (!hasold)
		{
			BPTreeDataObj treedata = new BPTreeDataObj();
			treedata.setRoot(new HashMap<String, Object>());
			setTreeData(treedata);
		}
	}

	public void unbind()
	{
		CON con = m_con;
		m_con = null;
		if (con != null)
			con.close();
	}

	public void setTreeData(BPTreeData treedata)
	{
		m_treedata = treedata;
		m_tree.setModel(new BPTreeModel(new BPTreeFuncsObject(treedata.getRoot())));
	}

	public CON getDataContainer()
	{
		return m_con;
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
		m_con.open();
		try
		{
			if (m_con.writeTreeData(m_treedata))
			{
				setSaved();
				m_needsave = false;
			}
		}
		finally
		{
			m_con.close();
		}
	}

	protected void setSaved()
	{
		WeakReference<BiConsumer<String, Boolean>> statehandlerref = m_statehandler;
		if (statehandlerref != null)
		{
			BiConsumer<String, Boolean> statehandler = statehandlerref.get();
			if (statehandler != null)
			{
				statehandler.accept(m_id, false);
			}
		}
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
		m_dynainfo = new WeakReference<Consumer<String>>(info);
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_statehandler = new WeakReference<BiConsumer<String, Boolean>>(handler);
	}

	public void clearResource()
	{
		if (m_con != null)
			unbind();
		m_tree.setModel(new BPTreeModel(new BPTreeFuncsObject(new HashMap<String, Object>())));
		m_treedata = null;
		removeAll();
	}

	public void delete()
	{
	}

	@SuppressWarnings("unchecked")
	public void showEditKV(ActionEvent e)
	{
		Object obj=m_treedata.getRoot();
		if(obj==null||!(obj instanceof Map))
		{
			return;
		}
		Map<String,Object> kv=(Map<String, Object>) obj;
		BPDialogForm dlg = new BPDialogForm();
		dlg.setEditable(true);
		dlg.setTitle(UIUtil.wrapBPTitle(BPActionConstCommon.TXT_EDIT));
		BPMDataWMapOrdered w = new BPMDataWMapOrdered(kv);
		dlg.setup(w.getClass().getName(), w);
		dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
		if (dlg.getActionResult() == BPDialogForm.COMMAND_OK)
		{
			swapData(null, null, dlg.getFormData());
			m_tree.setModel(new BPTreeModel(new BPTreeFuncsObject(m_treedata.getRoot())));
		}
	}

	@SuppressWarnings("unchecked")
	public void grabKeys(ActionEvent e)
	{
		Object obj = m_treedata.getRoot();
		if (obj == null || !(obj instanceof Map))
		{
			return;
		}
		Map<String, Object> kv = (Map<String, Object>) obj;
		List<String> keys = new ArrayList<String>(kv.keySet());
		UIStd.info(JSONUtil.encode(keys));
	}

	protected void swapData(Object par, Object key, Object newdata)
	{
		if (par == null)
		{
			m_treedata.setRoot(newdata);
		}
	}

	public void showClone(ActionEvent e)
	{
		BPTreeData treedata = m_treedata;
		Action[] acts = BPTreeDataCloneActions.getActions(treedata, null);
		if (acts != null && acts.length > 0)
		{
			JPopupMenu pop = new JPopupMenu();
			JComponent[] comps = UIUtil.makeMenuItems(acts);
			for (JComponent comp : comps)
			{
				pop.add(comp);
			}
			JComponent source = (JComponent) e.getSource();
			JComponent par = (JComponent) source.getParent();
			pop.show(par, source.getX(), source.getY() + source.getHeight());
		}
	}
}
