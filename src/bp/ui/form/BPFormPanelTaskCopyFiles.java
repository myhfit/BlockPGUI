package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.ui.dialog.BPDialogSelectResourceDir;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;

public class BPFormPanelTaskCopyFiles extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtsrc;
	protected BPTextField m_txttar;
	protected BPTextFieldPane m_pansrc;
	protected BPTextFieldPane m_pantar;
	protected BPCheckBox m_chkcreatedir;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("source", m_txtsrc.getText());
		rc.put("target", m_txttar.getText());
		rc.put("createdir", m_chkcreatedir.isSelected());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_pansrc = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txtsrc = m_pansrc.getTextComponent();

		m_pantar = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txttar = m_pantar.getTextComponent();

		m_chkcreatedir = makeCheckBox();

		addLine(new String[] { "Source" }, new Component[] { m_pansrc }, () -> !m_txtsrc.isEmpty());
		addLine(new String[] { "Target" }, new Component[] { m_pantar }, () -> !m_txttar.isEmpty());
		addLine(new String[] { "Create Dir" }, new Component[] { m_chkcreatedir });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtsrc, data, "source", editable);
		setComponentValue(m_txttar, data, "target", editable);
		setComponentValue(m_chkcreatedir, data, "createdir", editable);
	}

	protected String onSelectDir(String oldpath)
	{
		String rc = null;
		BPDialogSelectResourceDir dlg = new BPDialogSelectResourceDir();
		dlg.setVisible(true);
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			rc = BPCore.getFileContext().comparePath(((BPResourceDir) res).getFileFullName());
		}
		return rc;
	}
}