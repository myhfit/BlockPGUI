package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.config.BPSetting;
import bp.ui.scomp.BPTableSetting;
import bp.ui.util.UIUtil;

public class BPDialogSetting extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8201917687341092548L;

	protected BPSetting m_setting;
	protected BPTableSetting m_tbsetting;

	public boolean doCallCommonAction(int command)
	{
		if (m_tbsetting != null)
			m_tbsetting.editingStopped(null);
		if (command == COMMAND_CANCEL)
			m_setting = null;
		return false;
	}

	protected void initUIComponents()
	{
		m_tbsetting = new BPTableSetting();
		m_tbsetting.setMonoFont();
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(m_tbsetting);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		setTitle("BlockP - Setting");

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setModal(true);
	}

	protected void initDatas()
	{
		m_tbsetting.setSetting(m_setting);
	}

	public void setSetting(BPSetting setting)
	{
		m_setting = setting;
		initDatas();
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		super.setPrefers();
	}

	public BPSetting getSetting()
	{
		return m_setting;
	}
}
