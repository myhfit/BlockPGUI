package bp.ui.form;

import java.awt.Component;
import java.util.Map;

import bp.ui.actions.BPActionConstCommon;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;

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
		
//		m_txtstates = makeSingleLineTextField();
		BPTextFieldPane pnlstates = makeSingleLineTextFieldPanel(this::onShowStates);
		m_txtstates=pnlstates.getTextComponent();

		addSeparator("States");
//		addLine(new String[] { "States" }, new Component[] { m_txtstates }, () -> !m_txtstates.isEmpty());
		addLine(new String[] { "States" }, new Component[] { pnlstates }, () -> !m_txtstates.isEmpty());
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtstates, data, "states", editable);
	}

	private String onShowStates(String str)
	{
		return UIStd.textarea(str, UIUtil.wrapBPTitle(BPActionConstCommon.TXT_EDIT));
	}
}
