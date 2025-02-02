package bp.ui.form;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.script.BPScriptManager;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPTextField;

public class BPFormPanelScript extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3717765027670714659L;

	protected BPTextField m_txtname;
	protected BPComboBox<String> m_cmblanguage;
	protected BPCodePane m_script;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_txtname.getText().trim());
		rc.put("language", m_cmblanguage.getSelectedItem());
		rc.put("scripttext", m_script.getText());
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		m_txtname.setText((String) data.get("name"));
		m_cmblanguage.setSelectedItem((String) data.get("language"));
		m_script.setText((String) data.get("scripttext"));
		m_script.resizeDoc();
	}

	protected void initForm()
	{
		m_txtname = makeSingleLineTextField();
		m_cmblanguage = makeComboBox(null);
		JScrollPane tpan = new JScrollPane();
		m_script = new BPCodePane();
		tpan.setViewportView(m_script);
		tpan.setBorder(new EmptyBorder(0, 0, 0, 0));

		((BPComboBox.BPComboBoxModel<String>) m_cmblanguage.getModel()).setDatas(BPScriptManager.getLanguages());

		addLine(new String[] { "Name" }, new Component[] { m_txtname }, () -> !m_txtname.isEmpty() && m_txtname.checkSTName());
		addLine(new String[] { "Language" }, new Component[] { m_cmblanguage }, () -> m_cmblanguage.getSelectedItem() != null);
		doAddLineComponents(null, false, 0, new Component[] { tpan });
	}

}
