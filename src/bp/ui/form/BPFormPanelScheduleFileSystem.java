package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;

public class BPFormPanelScheduleFileSystem extends BPFormPanelSchedule
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6580446399734250031L;

	protected BPTextField m_txtwatchfile;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("watchfile", m_txtwatchfile.getNotEmptyText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();
		m_txtwatchfile = makeSingleLineTextField();

		addSeparator("Condition");
		addLine(new String[] { "Watch File" }, new Component[] { m_txtwatchfile }, () -> !m_txtwatchfile.isEmpty());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtwatchfile, data, "watchfile", editable);
	}
}