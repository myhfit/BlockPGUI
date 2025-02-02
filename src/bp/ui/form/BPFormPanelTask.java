package bp.ui.form;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import bp.ui.scomp.BPTextField;

public class BPFormPanelTask extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtname;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_txtname.getText());
		return rc;
	}

	protected void initForm()
	{
		m_txtname = makeSingleLineTextField();

		addLine(new String[] { "Name" }, new Component[] { m_txtname }, () -> !m_txtname.isEmpty());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		m_txtname.setText((String) data.get("name"));
		m_txtname.setEditable(editable);
	}
}