package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.data.BPDataSource;
import bp.res.BPResource;
import bp.ui.BPComponent;
import bp.ui.container.BPTabbedContainerBase;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncs.BPTreeActionEventHandler;
import bp.ui.tree.BPTreeFuncs.BPTreeActionType;
import bp.ui.tree.BPTreeFuncsResource;
import bp.ui.util.CommonUIOperations;
import bp.util.Std;

public abstract class BPDataSourcePanel<DS extends BPDataSource> extends BPAbstractEditorPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2651118551737120606L;

	protected DS m_ds;
	protected BPTreeComponentBase m_tree;
	protected BPTreeFuncsResource m_tf;
	protected BPTabbedContainerBase m_tab;
	protected BPTreeActionEventHandler m_treeactcb;

	public BPDataSourcePanel()
	{
		init();
	}

	protected void init()
	{
		m_tab = new BPTabbedContainerBase();
		m_tree = new BPTreeComponentBase();
		m_tree.setRootVisible(false);
		initDSStruct();
		initTreeEvents();
		JScrollPane scroll = new JScrollPane();
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setViewportView(m_tree);
		scroll.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));

		setLayout(new BorderLayout());

		add(scroll, BorderLayout.WEST);
		add(m_tab, BorderLayout.CENTER);
	}

	protected void initDSStruct()
	{
		BPTreeFuncsResource funcs = new BPTreeFuncsResource();
		funcs.setRoots(new ArrayList<>());
		m_tree.setTreeFuncs(funcs);
		m_tf = funcs;
	}
	
	protected void initTreeEvents()
	{
		m_treeactcb = this::onTreeEvent;
		m_tf.installTreeActionHandler(m_treeactcb);
	}

	protected void onTreeEvent(BPTreeActionType actiontype, BPTree tree, BPTreeNode node, String extact)
	{
		switch (actiontype)
		{
			case OPEN:
				openStructure((BPResource) node.getUserObject());
				break;
			default:
		}
	}

	protected void openStructure(BPResource res)
	{
		String resid = res.getID();
		if (m_tab.getComponentMap().containsKey(resid))
			m_tab.switchTab(resid);
		else
		{
			BPComponent<?> c = CommonUIOperations.makeComponentByResource(res, null, null, null, null);
			m_tab.addBPTab(res.getID(), (Component) null, res.getName(), c);
		}
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.PANEL;
	}

	public String getEditorInfo()
	{
		return null;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public void setDataSource(DS ds)
	{
		m_ds = ds;
		initByDataSource(ds);
	}

	public DS getDataSource()
	{
		return m_ds;
	}

	public void clearResource()
	{
		DS ds = m_ds;
		m_ds = null;
		if (ds != null)
		{
			try
			{
				ds.close();
			}
			catch (IOException e)
			{
				Std.debug(e.getMessage());
			}
		}
		super.clearResource();
	}

	protected void initByDataSource(DS ds)
	{
		m_tf.setRoots(getStructureRoots(ds));
		m_tree.setTreeFuncs(m_tf);
		m_tree.updateUI();
	}

	protected List<BPResource> getStructureRoots(DS ds)
	{
		return new ArrayList<BPResource>(Arrays.asList(ds.getStructureResource().listResources()));
	}
}
