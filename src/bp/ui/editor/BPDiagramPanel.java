package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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
import bp.data.BPDataContainerBase;
import bp.data.BPDiagram;
import bp.data.BPDiagram.BPDiagramElement;
import bp.data.BPDiagram.BPDiagramLink;
import bp.data.BPDiagram.BPDiagramNode;
import bp.data.BPJSONContainerBase;
import bp.data.BPMContainer;
import bp.res.BPResource;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPDiagramComponent;
import bp.ui.scomp.diagram.BPDiagramControllerRectangleSelect;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.DiagramUtil;

public class BPDiagramPanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPMContainer<BPDiagram>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5438174846930862232L;

	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;
	protected BPMContainer<BPDiagram> m_con;
	protected BPDiagramComponent m_dcomp;
	protected JScrollPane m_scroll;
	protected BPToolBarSQ m_toolbar;
	protected Action m_actclone;
	protected Action m_actcreate;
	protected Action m_actdelete;
	protected Action m_actrectsel;
	protected Action m_actlayout;
	protected BiFunction<BPDiagramElement, BPDiagramComponent, JPopupMenu> m_oncontextcb;

	public BPDiagramPanel()
	{
		m_oncontextcb = this::getElementContextMenu;

		init();
		initBPActions();
	}

	protected void init()
	{
		removeAll();
		m_scroll = new JScrollPane();
		m_dcomp = new BPDiagramComponent();
		m_toolbar = new BPToolBarSQ(true);
		m_actlayout = BPAction.build("Layout").callback(this::onShowLayout).tooltip("Layout").vIcon(BPIconResV.LAYOUT()).getAction();
		m_actcreate = BPAction.build("Create").callback(this::onCreate).tooltip("Create").vIcon(BPIconResV.ADD()).getAction();
		m_actdelete = BPAction.build("Delete").callback(this::onDelete).tooltip("Delete").vIcon(BPIconResV.DEL()).getAction();
		m_actrectsel = BPAction.build("Rectsel").callback(this::onEnterRectSelect).tooltip("Select Area").vIcon(BPIconResV.RECTSEL()).getAction();
		m_dcomp.setPreferredSize(new Dimension(2000, 2000));
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		m_toolbar.setActions(new Action[] { BPAction.separator(), m_actcreate, m_actdelete, BPAction.separator(), m_actrectsel, BPAction.separator(), m_actlayout });
		m_toolbar.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));

		setLayout(new BorderLayout());
		m_scroll.setViewportView(m_dcomp);
		add(m_scroll, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
	}

	protected void initBPActions()
	{
		m_dcomp.setContextCallback(m_oncontextcb);
	}

	protected JPopupMenu getElementContextMenu(BPDiagramElement ele, BPDiagramComponent dcomp)
	{
		JPopupMenu rc = null;
		if (ele.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
		{
			List<BPDiagramElement> eles = dcomp.getSelectedElements();
			int s = eles.size();
			if (s > 0)
			{
				rc = new JPopupMenu();
				BPDiagramElement ele0 = eles.get(s - 1);
				int eletype = ele0.getElementType();
				String ele0key = ele0.key;
				if (eletype == BPDiagramElement.ELEMENTTYPE_NODE)
				{
					if (s == 2)
					{
						BPAction act = BPAction.build("Create Link").callback(this::onCreateLink).getAction();
						rc.add(act);
					}
					{
						BPAction act = BPAction.build("Edit").callback(e -> editElement(ele0key)).getAction();
						rc.add(act);
					}
				}
			}
		}
		return rc;
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void bind(BPMContainer<BPDiagram> con, boolean noread)
	{
		m_con = con;

		BPDiagram d = null;
		if (!noread && m_con.canOpen())
		{
			m_con.open();
			d = m_con.readMData(false);
		}
		if (d == null)
		{
			d = new BPDiagram();
			DiagramUtil.initSimpleDiagram(d);
		}
		m_dcomp.bindDiagram(d);
	}

	public void unbind()
	{
		if (m_con != null)
			m_con.close();
		m_con = null;
	}

	public BPMContainer<BPDiagram> getDataContainer()
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

	protected void onCreate(ActionEvent e)
	{
		Action actcreatenode = BPAction.build("Node").name("Node").callback(this::onCreateNode).getAction();
		Action actcreatelink = BPAction.build("Link").name("Link").callback(this::onCreateLink).getAction();
		JPopupMenu pop = new JPopupMenu();
		JComponent[] comps = UIUtil.makeMenuItems(new Action[] { actcreatenode, actcreatelink });
		for (JComponent comp : comps)
		{
			pop.add(comp);
		}
		JComponent source = (JComponent) e.getSource();
		JComponent par = (JComponent) source.getParent();
		pop.show(par, source.getX() + source.getWidth(), source.getY());
	}

	protected void onCreateNode(ActionEvent e)
	{
		BPDiagram d = m_dcomp.getDiagram();
		BPDiagramNode n = new BPDiagramNode();
		n.x = 50 + (Math.random() * 100f);
		n.y = 50 + (Math.random() * 100f);
		n.setRandomKey();
		d.getLayer("node").addElement(n);
		m_dcomp.refresh();
	}

	protected void onCreateLink(ActionEvent e)
	{
		List<BPDiagramElement> eles = m_dcomp.getSelectedElements();
		if (eles.size() == 2)
		{
			BPDiagramElement ele0 = eles.get(0);
			BPDiagramElement ele1 = eles.get(1);
			if (ele0.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE && ele1.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
			{
				BPDiagramLink newlink = new BPDiagramLink();
				newlink.setRandomKey();
				newlink.n1 = (BPDiagramNode) ele0;
				newlink.n2 = (BPDiagramNode) ele1;
				BPDiagram d = m_dcomp.getDiagram();
				d.getLayer("link").addElement(newlink);
				m_dcomp.refresh();
			}
		}
	}

	protected void editElement(String elekey)
	{
		BPDiagramElement ele = m_dcomp.getDiagram().findElement(elekey);
		if (ele != null)
		{
			String newlabel = UIStd.input(ele.label, "Input New Label:", "Edit Element");
			if (newlabel != null)
			{
				ele.label = newlabel;
				ele.measuresize = null;
				m_dcomp.refresh();
			}
		}
	}

	protected void onDelete(ActionEvent e)
	{
		m_dcomp.deleteSelectedElement();
	}

	protected void onEnterRectSelect(ActionEvent e)
	{
		m_dcomp.setController(new BPDiagramControllerRectangleSelect());
	}

	protected void onShowLayout(ActionEvent e)
	{
		showLayout();
	}

	public void showLayout()
	{
		doLayout(null);
	}

	public void doLayout(Map<String, Object> params)
	{
		DiagramUtil.layout(m_dcomp.getDiagram(), params);
		m_dcomp.setupBounds();
		m_dcomp.invalidate();
		m_dcomp.repaint();
	}

	public void save()
	{
		m_con.open();
		m_con.writeMData(m_dcomp.getDiagram(), true);
		m_con.close();
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

	public BPDataContainer createDataContainer(BPResource res)
	{
		if (res != null && res.isFileSystem())
		{
			BPJSONContainerBase<BPDiagram> con = new BPJSONContainerBase<BPDiagram>();
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
