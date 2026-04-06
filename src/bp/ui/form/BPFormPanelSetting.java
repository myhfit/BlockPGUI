package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.config.BPSetting;
import bp.ui.scomp.BPTableSetting;

public class BPFormPanelSetting extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6004775172036361788L;
	
	protected BPTableSetting m_tab;
	protected BPSetting m_setting;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.putAll(m_setting.getMappedData());
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		m_setting=(BPSetting) data.get("_setting");
		m_tab.setSetting(m_setting);
	}

	protected void initForm()
	{
		m_tab = new BPTableSetting();
		JScrollPane scroll = new JScrollPane(m_tab);
		JPanel pnl = new JPanel();

		m_tab.setMonoFont();
		m_tab.getColumnModel().getColumn(0).setPreferredWidth(100);
		m_tab.getColumnModel().getColumn(1).setPreferredWidth(300);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		doAddLineComponents(null, false, 0, new Component[] { pnl });
	}
}
