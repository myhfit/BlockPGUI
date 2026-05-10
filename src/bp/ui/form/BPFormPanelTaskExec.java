package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.BPCore;
import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleConstCoreDict;
import bp.locale.BPLocaleHelpers;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.dialog.BPDialogSelectResource.SELECTTYPE;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.util.CommonUIOperations;

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

		addLine(new String[] { BPLocaleConstCC.TARGET.text() }, new Component[] { m_pantar }, () -> !m_txttar.isEmpty());
		addLine(new String[] { BPLocaleConstCC.PARAMETERS.text() }, new Component[] { m_txtparams });
		addLine(new String[] { BPLocaleHelpers.translate(BPLocaleConstCoreDict.S, "Working Directory") }, new Component[] { m_panworkdir });
		addLine(new String[] { BPLocaleHelpers.translateByClass(BPFormPanelTask.class, "Wait Target") }, new Component[] { wrapSingleLineComponent(m_chkwait) });
		addLine(new String[] { BPLocaleHelpers.translateByClass(BPFormPanelTask.class, "System Kill") }, new Component[] { wrapSingleLineComponent(m_chksyskill) });
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
		BPResource res = CommonUIOperations.showSelectResource(null, cb -> cb.setSelectType(SELECTTYPE.FILE));
		if (res != null)
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		return rc;
	}

	protected String onSelectDir(String oldpath)
	{
		String rc = null;
		BPResource res = CommonUIOperations.showSelectResource(null, cb -> cb.setSelectType(SELECTTYPE.DIR));
		if (res != null)
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		return rc;
	}
}