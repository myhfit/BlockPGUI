package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;

import bp.data.BPMData;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.util.UIUtil;

public class BPDialogForm extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1922742535586884177L;

	protected String m_key;
	protected Map<String, Object> m_data;
	protected Map<String, Object> m_result;
	protected BPForm<?> m_form;

	protected boolean m_editable = true;

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				if (!m_form.validateForm())
					return true;
				m_result = m_form.getFormData();
				break;
			}
			case COMMAND_CANCEL:
			{
				break;
			}
		}
		return false;
	}

	protected void initUIComponents()
	{
		setLayout(new BorderLayout());
		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setModal(true);
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(500, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		if (m_form != null)
			remove(m_form.getComponent());
		if (m_key != null)
			m_form = BPFormManager.getForm(m_key);
		if (m_form != null)
		{
			m_form.showData(m_data, m_editable);
			add(m_form.getComponent(), BorderLayout.CENTER);
		}
	}

	public void setup(String key, BPMData data)
	{
		setup(key, data.getMappedData());
	}

	public void setup(String key, Map<String, Object> data)
	{
		m_key = key;
		m_data = data;
		initDatas();
	}

	public Map<String, Object> getFormData()
	{
		return m_result;
	}

	public void setEditable(boolean flag)
	{
		m_editable = flag;
	}
}
