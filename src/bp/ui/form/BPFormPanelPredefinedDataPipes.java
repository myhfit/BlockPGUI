package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.dialog.BPDialogSelectResource2.SELECTTYPE;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPKVTable.KV;

public class BPFormPanelPredefinedDataPipes extends BPFormPanelMapOrdered
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4891410124214947199L;

	protected void initForm()
	{
		super.initForm();
	}

	protected List<Action> makeToolBarActions()
	{
		List<Action> rc = super.makeToolBarActions();
		BPAction actedit = BPAction.build("Edit").callback(this::onEdit).vIcon(BPIconResV.EDIT()).getAction();
		rc.add(BPAction.separator());
		rc.add(BPAction.separator());
		rc.add(actedit);
		return rc;
	}

	protected void onEdit(ActionEvent e)
	{
		m_tabkvs.editingCanceled(null);
		List<KV> kvs = m_tabkvs.getSelectedDatas();
		if (kvs.size() == 1)
		{
			KV kv = kvs.get(0);
			String v = (String) kv.value;
			if (v == null)
				v = "";
			BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
			dlg.setSelectType(SELECTTYPE.FILE);
			dlg.switchPathTreeFunc(1);
			dlg.setFilter(res -> res.isLeaf() ? res.getName().endsWith(".json") : true);
			dlg.showOpen();
			BPResource res = dlg.getSelectedResource();
			if (res != null)
				kv.value = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
			m_tabkvs.getBPTableModel().fireTableDataChanged();
		}
	}
}