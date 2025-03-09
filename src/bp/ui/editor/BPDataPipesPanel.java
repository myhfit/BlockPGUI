package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.UIConfigs;
import bp.data.BPDataConsumer;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.data.BPDataEndpointFactory;
import bp.data.BPDataPipes;
import bp.data.BPDiagram;
import bp.data.BPDiagram.BPDiagramElement;
import bp.data.BPDiagram.BPDiagramNode;
import bp.data.BPJSONContainerBase;
import bp.data.BPMContainer;
import bp.id.SerialIDGenerator;
import bp.res.BPResource;
import bp.transform.BPTransformer;
import bp.transform.BPTransformerFactory;
import bp.transform.BPTransformerManager;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPDiagramComponent;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.scomp.BPSplitPane;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.DiagramUtil;
import bp.util.ObjUtil;

public class BPDataPipesPanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPMContainer<BPDataPipes>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6062356793841361L;

	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;
	protected BPMContainer<BPDataPipes> m_con;
	protected JScrollPane m_scroll;
	protected JScrollPane m_scroll1;
	protected BPSplitPane m_sp;
	protected BPToolBarSQ m_toolbar;
	protected BPDiagramComponent m_dcomp;

	protected BPAction m_actaddtf;
	protected BPAction m_actaddep;
	protected BPAction m_actaddlink;
	protected BPAction m_actdel;
	protected BPAction m_actlayout;
	protected BPAction m_actconfig;
	protected BPAction m_actrun;

	protected BPDataPipes m_dps;
	protected BPList<BPDataConsumer<?>> m_lstcons;

	protected Map<BPDataConsumer<?>, String> m_rccmap;
	protected SerialIDGenerator m_idgen;
	protected BiConsumer<BPDiagramElement, BPDiagramComponent> m_clickref;

	protected boolean m_blockloopselect = false;

	protected WeakReference<BiConsumer<String, Boolean>> m_statehandler;

	public BPDataPipesPanel()
	{
		init();
		initBPActions();
	}

	protected void init()
	{
		removeAll();

		m_rccmap = new HashMap<BPDataConsumer<?>, String>();
		m_idgen = new SerialIDGenerator();
		m_dcomp = new BPDiagramComponent();
		m_sp = new BPSplitPane(BPSplitPane.HORIZONTAL_SPLIT);
		m_scroll = new JScrollPane();
		m_scroll1 = new JScrollPane();
		m_toolbar = new BPToolBarSQ(true);
		m_lstcons = new BPList<BPDataConsumer<?>>();
		m_lstcons.setCellRenderer(new BPList.BPListRenderer(obj -> ((BPDataConsumer<?>) obj).getInfo()));
		m_actrun = BPAction.build("run").tooltip("Run").vIcon(BPIconResV.START()).callback(this::onRunPipe).getAction();
		m_actaddtf = BPAction.build("add tf").tooltip("Add Transformer").vIcon(BPIconResV.ADD()).callback(this::onCreateTransformer).getAction();
		m_actaddep = BPAction.build("add ep").tooltip("Add Endpoint").vIcon(BPIconResV.ADD()).callback(this::onCreateEndpoint).getAction();
		m_actaddlink = BPAction.build("add link").tooltip("Add Link").vIcon(BPIconResV.RELATION()).callback(this::onCreateLink).getAction();
		m_actconfig = BPAction.build("config").tooltip("Config").vIcon(BPIconResV.EDIT()).callback(this::onConfigConsumer).getAction();
		m_actdel = BPAction.build("del").tooltip("Remove").vIcon(BPIconResV.DEL()).callback(this::onDelItem).getAction();
		m_actlayout = BPAction.build("layout").tooltip("Layout").vIcon(BPIconResV.LAYOUT()).callback(this::doLayout).getAction();
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_scroll1.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_lstcons.setBorder(null);
		m_lstcons.setListFont();
		m_sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_sp.setWeakDividerBorder();

		m_toolbar.setActions(new Action[] { BPAction.separator(), m_actaddtf, m_actaddep, m_actaddlink, m_actdel, BPAction.separator(), m_actconfig, m_actrun, m_actlayout });
		m_toolbar.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));

		setLayout(new BorderLayout());
		m_sp.add(m_scroll);
		m_sp.add(m_scroll1);
		m_scroll.setViewportView(m_lstcons);
		m_scroll1.setViewportView(m_dcomp);
		add(m_sp, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);

		initList();
		m_sp.setDividerLocation(200);

		m_clickref = this::onElementClick;
		m_dcomp.setClickNodeCallback(m_clickref);
	}

	protected void initBPActions()
	{
	}

	protected void onElementClick(BPDiagramElement e, BPDiagramComponent c)
	{
		int[] selitems = null;
		{
			List<BPDataConsumer<?>> selcs = new ArrayList<BPDataConsumer<?>>();
			{
				List<BPDiagramElement> sels = c.getSelectedElements();
				for (BPDiagramElement sele : sels)
				{
					if (sele.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
					{
						Object uo = sele.userdata;
						if (uo != null && uo instanceof BPDataConsumer)
						{
							selcs.add((BPDataConsumer<?>) uo);
						}
					}
				}
			}
			selitems = new int[selcs.size()];
			for (int i = 0; i < selcs.size(); i++)
			{
				BPDataConsumer<?> selc = selcs.get(i);
				selitems[i] = m_dps.getRawChildren().indexOf(selc);
			}
		}
		if (selitems != null)
		{
			m_blockloopselect = true;
			m_lstcons.setSelectedIndices(selitems);
			m_blockloopselect = false;
		}
	}

	protected void initList()
	{
		BPDataPipes dps = m_dps;
		if (dps != null)
		{
			BPListModel<BPDataConsumer<?>> model = new BPListModel<BPDataConsumer<?>>();
			model.setDatas(m_dps.getRawChildren());
			m_lstcons.setModel(model);
			m_lstcons.addListSelectionListener(this::onListSelectionChanged);
			m_lstcons.addMouseListener(new UIUtil.BPMouseListener(this::onListClick, null, null, null, null));
		}
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void bind(BPMContainer<BPDataPipes> con, boolean noread)
	{
		m_con = con;

		if (!noread && m_con.canOpen())
		{
			m_con.open();
			m_dps = m_con.readMData(false);
			m_rccmap.clear();
			initList();
			m_dcomp.bindDiagram(makeDiagram());
		}
		else
		{
			if (m_dps == null)
			{
				m_dps = new BPDataPipes.BPDataPipesDirect();
				m_rccmap.clear();
				initList();
				m_dcomp.bindDiagram(makeDiagram());
			}
		}
	}

	public void unbind()
	{
		if (m_con != null)
			m_con.close();
		m_con = null;
	}

	protected BPDiagram makeDiagram()
	{
		BPDataPipes dps = m_dps;
		BPDiagram d = new BPDiagram();
		DiagramUtil.initSimpleDiagram(d);
		List<BPDataConsumer<?>> chs = dps.getChildren();
		long maxid = 0;
		for (BPDataConsumer<?> c : chs)
		{
			String id = c.getID();
			if (id != null)
				maxid = Math.max(maxid, ObjUtil.toLong(id, 0));
		}
		m_idgen.setValue(maxid + 1);

		for (BPDataConsumer<?> c : chs)
		{
			addNodeToDiagram(c, d);
		}
		return d;
	}

	public BPMContainer<BPDataPipes> getDataContainer()
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

	protected void onRunPipe(ActionEvent e)
	{

	}

	protected void onConfigConsumer(ActionEvent e)
	{
		BPDataConsumer<?> c = m_lstcons.getSelectedValue();
		if (c == null)
			return;
		BPSetting setting = c.getSetting();
		if (setting != null)
		{
			BPDialogSetting dlg = new BPDialogSetting();
			dlg.setSetting(setting);
			dlg.setVisible(true);
			setting = dlg.getSetting();
			if (setting != null)
				c.setSetting(setting);
			m_lstcons.updateUI();

			String key = m_rccmap.get(c);
			BPDiagramElement ele = m_dcomp.getDiagram().findElement(key);
			if (ele != null)
			{
				ele.label = c.getInfo();
				ele.measuresize = null;
				m_dcomp.refresh();
			}
			m_needsave = true;
			dispatchStateChanged();
		}
	}

	protected void onListClick(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			onConfigConsumer(null);
		}
	}

	protected void onListSelectionChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;
		if (m_blockloopselect)
			return;
		List<BPDataConsumer<?>> cons = m_lstcons.getSelectedValuesList();
		Map<BPDataConsumer<?>, String> rccmap = m_rccmap;
		List<String> keys = new ArrayList<String>();
		for (BPDataConsumer<?> con : cons)
		{
			String key = rccmap.get(con);
			if (key != null)
			{
				if (!keys.contains(key))
					keys.add(key);
			}
		}
		m_dcomp.getRawSelection().setKeys(keys);
		m_dcomp.refresh();
	}

	protected void addNodeToDiagram(BPDataConsumer<?> c, BPDiagram d)
	{
		String key = c.getID();
		if (key == null)
		{
			key = m_idgen.genID();
			c.setID(key);
		}
		BPDiagramNode node = new BPDiagramNode(key);
		node.userdata = c;
		node.label = c.getInfo();
		node.x = Math.random() * 200d + 100d;
		node.y = Math.random() * 200d + 50d;
		node.layerid = 2;
		d.getLayer("node").addElement(node);
		m_rccmap.put(c, key);
	}

	protected void addNode(BPDataConsumer<?> c)
	{
		addNodeToDiagram(c, m_dcomp.getDiagram());
		m_dcomp.refresh();
		m_needsave = true;
		dispatchStateChanged();
	}

	protected void onCreateTransformer(ActionEvent e)
	{
		List<BPTransformerFactory> facs = BPTransformerManager.getTransformerFacs(null);
		BPTransformerFactory fac = UIStd.select(facs, BPGUICore.S_BP_TITLE + " - Select Transformer", obj -> ((BPTransformerFactory) obj).getName());
		if (fac != null)
		{
			List<String> fts = new ArrayList<String>(fac.getFunctionTypes());
			if (fts.size() > 0)
			{
				String ft = (fts.size() == 1) ? fts.get(0) : UIStd.select(fts, BPGUICore.S_BP_TITLE + " - Select Function", null);
				if (ft != null)
				{
					BPTransformer<?> tf = fac.createTransformer(ft);
					if (tf != null)
					{
						tf.setID(m_idgen.genID());
						m_dps.addChild(tf);
						m_lstcons.updateUI();
						addNode(tf);
					}
				}
			}
		}
	}

	protected void onCreateEndpoint(ActionEvent e)
	{
		ServiceLoader<BPDataEndpointFactory> loader = ClassUtil.getServices(BPDataEndpointFactory.class);
		List<BPDataEndpointFactory> facs = new ArrayList<BPDataEndpointFactory>();
		for (BPDataEndpointFactory fac : loader)
			facs.add(fac);
		BPDataEndpointFactory fac = UIStd.select(facs, BPGUICore.S_BP_TITLE + " - Select Transformer", obj -> ((BPDataEndpointFactory) obj).getName());
		if (fac != null)
		{
			List<String> fts = fac.getSupportedFormats();
			String ft = UIStd.select(fts, BPGUICore.S_BP_TITLE + " - Select Format", null);
			if (ft != null)
			{
				BPDataConsumer<?> dc = fac.create(ft);
				if (dc != null)
				{
					dc.setID(m_idgen.genID());
					m_dps.addChild(dc);
					m_lstcons.updateUI();
					addNode(dc);
				}
			}
		}
	}

	protected void onCreateLink(ActionEvent e)
	{

	}

	protected void doLayout(ActionEvent e)
	{
		DiagramUtil.layout(m_dcomp.getDiagram(), null);
		m_dcomp.setupBounds();
		m_dcomp.invalidate();
		m_dcomp.repaint();
	}

	protected void onDelItem(ActionEvent e)
	{
		List<BPDataConsumer<?>> sels = m_lstcons.getSelectedValuesList();
		if (sels.size() == 0)
			return;
		sels = m_dps.removeChildren(sels);
		m_lstcons.updateUI();
		List<String> delkeys = new ArrayList<String>();
		Map<BPDataConsumer<?>, String> rccmap = m_rccmap;
		for (BPDataConsumer<?> c : sels)
		{
			String key = rccmap.get(c);
			if (key != null && !delkeys.contains(key))
			{
				delkeys.add(key);
			}
		}
		m_dcomp.deleteElements(delkeys);
		m_needsave = true;
		dispatchStateChanged();
	}

	public void save()
	{
		BPMContainer<BPDataPipes> con = m_con;
		if (con != null)
		{
			con.open();
			con.writeMData(m_dps, true);
			con.close();
		}
		m_needsave = false;
		dispatchStateChanged();
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

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_statehandler = new WeakReference<BiConsumer<String, Boolean>>(handler);
	}

	public void dispatchStateChanged()
	{
		WeakReference<BiConsumer<String, Boolean>> ref = m_statehandler;
		if (ref != null)
		{
			BiConsumer<String, Boolean> handler = ref.get();
			if (handler != null)
			{
				handler.accept(m_id, m_needsave);
			}
		}
	}

	public BPDataContainer createDataContainer(BPResource res)
	{
		if (res != null && res.isFileSystem())
		{
			BPJSONContainerBase<BPDataPipes> con = new BPJSONContainerBase<BPDataPipes>();
			con.bind(res);
			return con;
		}
		else
		{
			BPDataContainer con = new BPDataContainerBase();
			con.bind(res);
			return con;
		}
	}
}