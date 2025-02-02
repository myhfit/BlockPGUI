package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;

public class BPFormPanelTaskReadTextFile extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5351434475352923859L;

	protected BPTextField m_txtfilename;
	protected BPTextField m_txtencoding;
	protected BPTextFieldPane m_panfilename;
	protected BPTextFieldPane m_panencoding;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("filename", m_txtfilename.getText());
		rc.put("encoding", m_txtencoding.getNotEmptyText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_panfilename = makeSingleLineTextFieldPanel(this::onSelectFile);
		m_panencoding = makeSingleLineTextFieldPanel(this::onSelectEncoding);
		m_txtfilename = m_panfilename.getTextComponent();
		m_txtencoding = m_panencoding.getTextComponent();

		addLine(new String[] { "Filename" }, new Component[] { m_panfilename }, () -> !m_txtfilename.isEmpty());
		addLine(new String[] { "Encoding" }, new Component[] { m_txtencoding });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtfilename, data, "filename", editable);
		setComponentValue(m_txtencoding, data, "encoding", editable);
	}

	protected String onSelectEncoding(String oldencoding)
	{
		return "";
	}

	protected String onSelectFile(String oldpath)
	{
		String rc = null;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
		dlg.setVisible(true);
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		}
		return rc;
	}
}
