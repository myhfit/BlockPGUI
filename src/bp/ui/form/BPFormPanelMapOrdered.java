package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import bp.ui.actions.BPAction;
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
		BPAction actup = BPAction.build("Move Up").callback(this::onMoveUp).vIcon(BPIconResV.TOUP()).getAction();
		BPAction actdown = BPAction.build("Move Down").callback(this::onMoveDown).vIcon(BPIconResV.TODOWN()).getAction();
		rc.add(BPAction.separator());
		rc.add(BPAction.separator());
		rc.add(actup);
		rc.add(BPAction.separator());
		rc.add(actdown);
		return rc;
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
