package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPKVTable.KV;

public class BPFormPanelMapOrdered extends BPFormPanelMap
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1364370730558039593L;

	protected Map<String, Object> createMap()
	{
		return new LinkedHashMap<String, Object>();
	}

	protected List<Action> makeToolBarActions()
	{
		List<Action> rc = super.makeToolBarActions();
		BPAction actadd2 = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_INSERT, this::onInsert, b -> b.vIcon(BPIconResV.TORIGHT()));
		BPAction actup = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNUP, this::onMoveUp);
		BPAction actdown = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDOWN, this::onMoveDown);
		rc.add(0, actadd2);
		rc.add(BPAction.separator());
		rc.add(BPAction.separator());
		rc.add(actup);
		rc.add(BPAction.separator());
		rc.add(actdown);
		return rc;
	}
	
	protected void onInsert(ActionEvent e)
	{
		int si = m_tabkvs.getSelectedRow();
		if (si < 0)
		{
			onAdd(e);
			return;
		}
		m_tabkvs.getBPTableModel().getDatas().add(si, new KV());
		m_tabkvs.getBPTableModel().fireTableDataChanged();
	}

	protected void onMoveUp(ActionEvent e)
	{
		int r = m_tabkvs.getSelectedModelRow();
		if (r == -1)
			return;
		moveData(r, -1);
	}

	protected void onMoveDown(ActionEvent e)
	{
		int r = m_tabkvs.getSelectedModelRow();
		if (r == -1)
			return;
		moveData(r, 1);
	}

	protected void moveData(int r, int delta)
	{
		List<KV> datas = m_tabkvs.getBPTableModel().getDatas();
		int newi = r + delta;
		if (newi < 0 || newi >= datas.size())
			return;
		KV kv = datas.remove(r);
		datas.add(r + delta, kv);
		m_tabkvs.getBPTableModel().fireTableDataChanged();
		m_tabkvs.getSelectionModel().setSelectionInterval(newi, newi);
	}
}
