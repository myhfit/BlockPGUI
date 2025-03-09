package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;

public class BPFormPanelFile extends BPFormPanelFileSystem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtlen;

	protected void initForm()
	{
		super.initForm();

		m_txtlen = makeSingleLineTextField();

		addLine(new String[] { "Length" }, new Component[] { m_txtlen });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		m_txtlen.setText(Long.toString((long) data.get("len")));
		m_txtlen.setEditable(false);
	}
}