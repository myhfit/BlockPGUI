package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;
import bp.util.DateUtil;

public class BPFormPanelFile extends BPFormPanelFileSystem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtlen;
	protected BPTextField m_txtlastmodified;

	protected void initForm()
	{
		super.initForm();

		m_txtlen = makeSingleLineTextField();
		m_txtlastmodified = makeSingleLineTextField();

		addLine(new String[] { "Length" }, new Component[] { m_txtlen });
		addLine(new String[] { "Last Modified" }, new Component[] { m_txtlastmodified });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		m_txtlen.setText(Long.toString((long) data.get("len")));
		m_txtlen.setEditable(false);

		Long l = (Long) data.get("lastmodified");
		if (l != null)
			m_txtlastmodified.setText(DateUtil.formatTime(l));
		m_txtlastmodified.setEditable(false);
	}
}