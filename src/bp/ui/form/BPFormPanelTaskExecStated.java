package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.scomp.BPTextField;

public class BPFormPanelTaskExecStated extends BPFormPanelTaskExec
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1986211674357037958L;
	
	protected BPTextField m_txtstates;


	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("states", m_txtstates.getText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();
		
		m_txtstates = makeSingleLineTextField();

		addSeparator("States");
		addLine(new String[] { "States" }, new Component[] { m_txtstates }, () -> !m_txtstates.isEmpty());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtstates, data, "states", editable);
	}
}
