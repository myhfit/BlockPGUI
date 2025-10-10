package bp.ui.form;

import java.awt.Component;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.task.BPTaskUserInput;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPTextField;

public class BPFormPanelTaskUserInput extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 968007907142406287L;

	protected BPComboBox<String> m_cmbvtype;
	protected BPComboBox<String> m_cmbvcast;
	protected BPTextField m_txtdefault;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("vtype", m_cmbvtype.getSelectedItem());
		rc.put("vcast", m_cmbvcast.getSelectedItem());
		rc.put("vdefault", m_txtdefault.getText());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_cmbvtype = makeComboBox(null);
		m_cmbvcast = makeComboBox(null);
		m_txtdefault = makeSingleLineTextField();

		m_cmbvtype.getBPModel().setDatas(new CopyOnWriteArrayList<>(new String[] { BPTaskUserInput.S_VTYPE_SV, BPTaskUserInput.S_VTYPE_JSON }));
		m_cmbvcast.getBPModel().setDatas(new CopyOnWriteArrayList<>(new String[] { "", Integer.class.getName(), Double.class.getName() }));

		addLine(new String[] { "Value Type" }, new Component[] { m_cmbvtype }, () -> m_cmbvtype.getSelectedIndex() > -1);
		addLine(new String[] { "Cast" }, new Component[] { m_cmbvcast });
		addLine(new String[] { "Default value" }, new Component[] { m_txtdefault });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_cmbvtype, data, "vtype", editable);
		setComponentValue(m_cmbvcast, data, "vcast", editable);
		setComponentValue(m_txtdefault, data, "vdefault", editable);
	}
}