package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;

public class BPFormPanelTaskRemind extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3536351804878303821L;
	
	protected BPTextField m_txtcontent;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("content", m_txtcontent.getText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_txtcontent = makeSingleLineTextField();

		addLine(new String[] { "Content" }, new Component[] { m_txtcontent }, () -> !m_txtcontent.isEmpty());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtcontent, data, "content", editable);
	}
}
