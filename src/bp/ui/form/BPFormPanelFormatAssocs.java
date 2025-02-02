package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.format.BPFormat;
import bp.ui.scomp.BPTextField;

public class BPFormPanelFormatAssocs extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7984345736223929757L;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		return rc;
	}

	protected void initForm()
	{
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		List<String> keys = new ArrayList<String>(data.keySet());
		keys.sort((a, b) -> a.compareToIgnoreCase(b));
		for (String key : keys)
		{
			BPTextField comp = makeSingleLineTextField();
			BPFormat format = (BPFormat) data.get(key);
			comp.setEditable(false);
			comp.setText("[" + format.getClass().getSimpleName() + "]" + format.getName());
			addLine(new String[] { key }, new Component[] { comp });
		}
	}
}