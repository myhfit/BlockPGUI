package bp.ui.form;

import java.util.Map;

import bp.BPGUICore;
import bp.ui.scomp.BPCheckBox;

@SuppressWarnings("serial")
public class BPFormPanelUIConfigs extends BPFormPanelDynamicByConfig
{
	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		BPCheckBox chk = (BPCheckBox) m_formcontext.findItem("SHOW_LAUNCHER").getComponent();
		if (!chk.isEnabled())
			rc.remove("SHOW_LAUNCHER");
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		BPCheckBox chk = (BPCheckBox) m_formcontext.findItem("SHOW_LAUNCHER").getComponent();
		Boolean showlauncher = BPGUICore.LAUNCHER_FLAG;
		if (showlauncher != null)
			chk.setSelected(showlauncher);
		else
			chk.setEnabled(false);
	}
}