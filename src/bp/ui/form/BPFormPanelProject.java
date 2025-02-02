package bp.ui.form;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import bp.ui.scomp.BPTextField;

public class BPFormPanelProject extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4744021138970526384L;

	protected BPTextField m_txtname;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_txtname.getNotEmptyText());
		return rc;
	}

	protected void initForm()
	{
		m_txtname = makeSingleLineTextField();

		addLine(new String[] { "Name" }, new Component[] { m_txtname }, () -> !m_txtname.isEmpty() && m_txtname.checkSTName());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		setComponentValue(m_txtname, data, "name", false);
	}
}