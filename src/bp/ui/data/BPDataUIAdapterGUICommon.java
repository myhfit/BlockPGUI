package bp.ui.data;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.data.BPXData;
import bp.data.BPXYData;
import bp.ui.editor.BPCodePanel;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPTable;
import bp.ui.table.BPTableFuncsXY;
import bp.ui.tree.BPTreeCellRendererObject;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncsObject;
import bp.util.ObjUtil;

public class BPDataUIAdapterGUICommon implements BPDataUIAdapter
{
	@SuppressWarnings("unchecked")
	public <C> C getUIForData(Object data)
	{
		if (data instanceof Map)
		{
			return (C) getTree((Map<String, ?>) data);
		}
		else if (data instanceof List)
		{
			return (C) getList((List<?>) data);
		}
		// else if (data instanceof BPYData)
		// {
		// }
		else if (data instanceof BPXYData)
		{
			return (C) getTable((BPXYData) data);
		}
		return (C) getTextPanel(data);
	}

	@SuppressWarnings("unchecked")
	protected JComponent getList(List<?> data)
	{
		JScrollPane scroll = new JScrollPane();
		BPList<Object> list = new BPList<>();
		BPList.BPListModel<Object> m = new BPList.BPListModel<Object>();
		list.setListFont();
		m.setDatas((List<Object>) data);
		list.setModel(m);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(list);
		return scroll;
	}

	protected JComponent getTree(Map<String, ?> data)
	{
		JScrollPane scroll = new JScrollPane();
		BPTreeComponentBase tree = new BPTreeComponentBase();
		tree.setRootVisible(false);
		tree.setTreeFont();
		tree.setTreeFuncs(new BPTreeFuncsObject(data));
		tree.setCellRenderer(new BPTreeCellRendererObject());
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(tree);
		return scroll;
	}

	protected JComponent getTable(BPXYData data)
	{
		JScrollPane scroll = new JScrollPane();
		BPTable<BPXData> table = new BPTable<BPXData>(new BPTableFuncsXY(data));
		table.setTableFont();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(table);
		return scroll;
	}

	protected JComponent getTextPanel(Object data)
	{
		BPCodePanel rc = new BPCodePanel();
		rc.getTextPanel().setText(ObjUtil.toString(data));
		return rc;
	}

	public boolean canHandle(Object data, Class<?> cls)
	{
		if (data instanceof Map || data instanceof List || data instanceof String)
			return true;
		if (data instanceof BPXYData)
			return true;
		return false;
	}

	public boolean canDeal(Object data, Class<?> cls)
	{
		return true;
	}

}
