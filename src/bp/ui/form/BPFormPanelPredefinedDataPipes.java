package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.dialog.BPDialogSelectResource.SELECTTYPE;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.util.CommonUIOperations;

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
		BPAction actedit = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNEDIT, this::onEdit);
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
			BPResource res = CommonUIOperations.showSelectResource(SwingUtilities.getWindowAncestor(this), cb -> cb.setSelectType(SELECTTYPE.FILE).switchPathTreeFunc(1).setFilter(r -> r.isLeaf() ? r.getName().endsWith(".json") : true));
			if (res != null)
				kv.value = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
			m_tabkvs.getBPTableModel().fireTableDataChanged();
		}
	}
}