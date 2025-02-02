package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import bp.schedule.BPScheduleTargetFactory;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPTextField;
import bp.util.ClassUtil;

public class BPFormPanelSchedule extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3277012396854479475L;

	protected BPTextField m_txtname;
	protected BPCheckBox m_chkenabled;
	protected BPComboBox<String> m_cmbtargetfac;
	protected BPTextField m_txttargetparams;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_txtname.getNotEmptyText());
		rc.put("enabled", m_chkenabled.isSelected());
		rc.put("targetfac", m_cmbtargetfac.getSelectedItem());
		rc.put("targetparams", m_txttargetparams.getNotEmptyText());
		return rc;
	}

	protected void initForm()
	{
		m_txtname = makeSingleLineTextField();
		m_chkenabled = makeCheckBox();
		m_cmbtargetfac = makeComboBox(null);
		m_txttargetparams = makeSingleLineTextField();

		m_chkenabled.setSelected(true);

		addLine(new String[] { "Name" }, new Component[] { m_txtname }, () -> !m_txtname.isEmpty() && m_txtname.checkSTName());
		addLine(new String[] { "Enabled" }, new Component[] { m_chkenabled }, true, null);
		addSeparator("Target");
		addLine(new String[] { "Target Type" }, new Component[] { m_cmbtargetfac }, () -> m_cmbtargetfac.getSelectedItem() != null);
		addLine(new String[] { "Target Params" }, new Component[] { m_txttargetparams });
		initTargetFacs();
	}

	protected void initTargetFacs()
	{
		List<String> facnames = new ArrayList<String>();
		ServiceLoader<BPScheduleTargetFactory> facs = ClassUtil.getServices(BPScheduleTargetFactory.class);
		for (BPScheduleTargetFactory fac : facs)
		{
			facnames.add(fac.getName());
		}
		m_cmbtargetfac.getBPModel().setDatas(facnames);
		m_cmbtargetfac.setSelectedIndex(-1);
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		setComponentValue(m_txtname, data, "name", editable);
		setComponentValue(m_chkenabled, data, "enabled", editable);
		setComponentValue(m_cmbtargetfac, data, "targetfac", editable);
		setComponentValue(m_txttargetparams, data, "targetparams", editable);
	}
}