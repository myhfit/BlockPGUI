package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import bp.ui.editor.BPCodePanel;
import bp.ui.scomp.BPTextPane;

public class BPFormPanelString extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4839897203256029140L;

	protected BPCodePanel m_txt;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		String txt = m_txt.getTextPanel().getText();
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("_value", txt);
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		String v = (String) data.get("_value");
		{
			BPTextPane txt = m_txt.getTextPanel();
			txt.setText(v);
			if (v != null)
				txt.setCaretPosition(0);
			if (!editable)
			{
			}
			txt.setEditable(editable);
		}
	}

	protected void initForm()
	{
		m_txt = new BPCodePanel();
		JPanel pnl = new JPanel();

		pnl.setLayout(new BorderLayout());
		pnl.add(m_txt, BorderLayout.CENTER);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}
}