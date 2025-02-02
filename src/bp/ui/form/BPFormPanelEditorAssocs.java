package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.config.UIConfigs;
import bp.ui.editor.BPEditorFactory;
import bp.ui.scomp.BPTextField;

public class BPFormPanelEditorAssocs extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8202174958499437282L;

	public BPFormPanelEditorAssocs()
	{
		m_labelwidth = (int) (120 * UIConfigs.UI_SCALE());
	}

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		return rc;
	}

	protected void initForm()
	{
	}

	@SuppressWarnings("unchecked")
	public void showData(Map<String, ?> data, boolean editable)
	{
		m_form.removeAll();
		List<String> keys = new ArrayList<String>(data.keySet());
		keys.sort((a, b) -> a.compareToIgnoreCase(b));
		for (String key : keys)
		{
			BPTextField comp = makeSingleLineTextField();
			List<BPEditorFactory> facs = (List<BPEditorFactory>) data.get(key);
			comp.setEditable(false);
			StringBuilder sb = new StringBuilder();
			for (int i = facs.size() - 1; i >= 0; i--)
			{
				BPEditorFactory fac = facs.get(i);
				if (sb.length() > 0)
					sb.append(">");
				sb.append(fac.getName());
			}
			comp.setText(sb.toString());
			addLine(new String[] { key }, new Component[] { comp });
		}
	}
}