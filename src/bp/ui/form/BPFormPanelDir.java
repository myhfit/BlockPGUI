package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;
import bp.util.DateUtil;

public class BPFormPanelDir extends BPFormPanelFileSystem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6203891780748439834L;
	
	protected BPTextField m_txtlastmodified;

	protected void initForm()
	{
		super.initForm();

		m_txtlastmodified = makeSingleLineTextField();

		addLine(new String[] { "Last Modified" }, new Component[] { m_txtlastmodified });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		
		Long l = (Long) data.get("lastmodified");
		if (l != null)
			m_txtlastmodified.setText(DateUtil.formatTime(l));
		m_txtlastmodified.setEditable(false);
	}
}