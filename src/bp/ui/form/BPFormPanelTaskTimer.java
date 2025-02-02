package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;
import bp.util.ObjUtil;

public class BPFormPanelTaskTimer extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtduration;
	protected BPTextField m_txtinterval;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("duration", Long.parseLong(m_txtduration.getText()));
		String interval = m_txtinterval.getText();
		if (interval != null && interval.trim().length() > 0)
			rc.put("interval", Long.parseLong(interval));
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_txtduration = makeSingleLineTextField();
		m_txtinterval = makeSingleLineTextField();

		addLine(new String[] { "Duration(ms)" }, new Component[] { m_txtduration }, () -> m_txtduration.isInt());
		addLine(new String[] { "Interval(ms)" }, new Component[] { m_txtinterval }, false, () -> m_txtinterval.isEmpty() || m_txtinterval.isLong());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		m_txtduration.setText(ObjUtil.toString(data.get("duration"), ""));
		m_txtinterval.setText(ObjUtil.toString(data.get("interval"), ""));
		m_txtduration.setEditable(editable);
		m_txtinterval.setEditable(editable);
	}
}
