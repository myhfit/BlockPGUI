package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.dialog.BPDialogSelectResource2.SELECTTYPE;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;

public class BPFormPanelTaskExec extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8707699494362589542L;

	protected BPTextField m_txttar;
	protected BPTextField m_txtparams;
	protected BPTextField m_txtworkdir;
	protected BPTextFieldPane m_pantar;
	protected BPTextFieldPane m_panworkdir;
	protected BPCheckBox m_chkwait;
	protected BPCheckBox m_chksyskill;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("target", m_txttar.getText());
		rc.put("params", m_txtparams.getNotEmptyText());
		rc.put("workdir", m_txtworkdir.getNotEmptyText());
		rc.put("wait", m_chkwait.isSelected());
		rc.put("syskill", m_chksyskill.isSelected());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_pantar = makeSingleLineTextFieldPanel(this::onSelectFile);
		m_panworkdir = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txttar = m_pantar.getTextComponent();
		m_txtparams = makeSingleLineTextField();
		m_txtworkdir = m_panworkdir.getTextComponent();
		m_chkwait = makeCheckBox();
		m_chksyskill = makeCheckBox();

		addLine(new String[] { "Target" }, new Component[] { m_pantar }, () -> !m_txttar.isEmpty());
		addLine(new String[] { "Parameters" }, new Component[] { m_txtparams });
		addLine(new String[] { "Working Directory" }, new Component[] { m_panworkdir });
		addLine(new String[] { "Wait Target" }, new Component[] { wrapSingleLineComponent(m_chkwait) });
		addLine(new String[] { "System Kill" }, new Component[] { wrapSingleLineComponent(m_chksyskill) });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txttar, data, "target", editable);
		setComponentValue(m_txtparams, data, "params", editable);
		setComponentValue(m_txtworkdir, data, "workdir", editable);
		setComponentValue(m_chkwait, data, "wait", editable);
		setComponentValue(m_chksyskill, data, "syskill", editable);
	}

	protected String onSelectFile(String oldpath)
	{
		String rc = null;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
		dlg.setSelectType(SELECTTYPE.FILE);
		dlg.showOpen();
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		}
		return rc;
	}

	protected String onSelectDir(String oldpath)
	{
		String rc = null;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
		dlg.setSelectType(SELECTTYPE.DIR);
		dlg.showOpen();
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		}
		return rc;
	}
}