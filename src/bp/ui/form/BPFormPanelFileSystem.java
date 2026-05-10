package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.locale.BPLocaleConstCC;
import bp.ui.scomp.BPTextField;
import bp.util.DateUtil;

public class BPFormPanelFileSystem extends BPFormPanelResourceBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtfullname;
	protected BPTextField m_txtattrib;
	protected BPTextField m_txtcreationtime;
	protected BPTextField m_txtlastmodified;
	protected BPTextField m_txtaccesstime;

	protected void initForm()
	{
		super.initForm();

		m_txtfullname = makeSingleLineTextField();
		m_txtattrib = makeSingleLineTextField();
		m_txtcreationtime = makeSingleLineTextField();
		m_txtlastmodified = makeSingleLineTextField();
		m_txtaccesstime = makeSingleLineTextField();

		addLine(new String[] { BPLocaleConstCC.FULLNAME.text() }, new Component[] { m_txtfullname });
		addLine(new String[] { BPLocaleConstCC.ATTRIB.text() }, new Component[] { m_txtattrib });
		addLine(new String[] { BPLocaleConstCC.CREATION_TIME.text() }, new Component[] { m_txtcreationtime });
		addLine(new String[] { BPLocaleConstCC.LAST_MODIFIED.text() }, new Component[] { m_txtlastmodified });
		addLine(new String[] { BPLocaleConstCC.LAST_ACCESS.text() }, new Component[] { m_txtaccesstime });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);

		setComponentValue(m_txtfullname, data, "fullname", false);
		setComponentValue(m_txtattrib, data, "attrib", false);

		Long ltime = (Long) data.get("lastmodified");
		Long ctime = (Long) data.get("creationtime");
		Long atime = (Long) data.get("accesstime");
		if (ctime != null)
			m_txtcreationtime.setText(DateUtil.formatTime(ctime));
		m_txtcreationtime.setEditable(false);
		if (ltime != null)
			m_txtlastmodified.setText(DateUtil.formatTime(ltime));
		m_txtlastmodified.setEditable(false);
		if (atime != null)
			m_txtaccesstime.setText(DateUtil.formatTime(atime));
		m_txtaccesstime.setEditable(false);
	}
}