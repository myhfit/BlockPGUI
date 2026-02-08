package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.BPCore;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;

public class BPDialogConfigs extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1857579460576615196L;

	protected BPList<BPConfig> m_lstcfgs;
	protected BPForm<?> m_form;

	public BPDialogConfigs()
	{

	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		super.setPrefers();
	}

	protected void initUIComponents()
	{
		m_lstcfgs = new BPList<BPConfig>();
		m_lstcfgs.setListFont();
		m_lstcfgs.setCellRenderer(new BPList.BPListRenderer(this::getConfigName));
		m_lstcfgs.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(160, 0)));
		m_lstcfgs.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		m_lstcfgs.addListSelectionListener(this::onListSelectionChange);

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(m_lstcfgs);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		setTitle(UIUtil.wrapBPTitle(BPActionConstCommon.TXT_CONFIGS));

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.WEST);
		setCommandBarMode(COMMANDBAR_OK_CANCEL_APPLY);
		setModal(true);
	}

	protected String getConfigName(Object config)
	{
		if (config instanceof BPConfig)
			return ((BPConfig) config).getConfigName();
		return config.getClass().getSimpleName();
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (m_form != null)
			{
				remove(m_form.getComponent());
			}
			BPConfig cfg = m_lstcfgs.getSelectedValue();
			if (cfg != null)
			{
				m_form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), cfg.getClass(), BPConfig.class);
				if (m_form != null)
				{
					add(m_form.getComponent(), BorderLayout.CENTER);
					m_form.showData(cfg.getMappedData());
				}
			}
			validate();
			repaint();
		}
	}

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				if (!m_form.validateForm())
					return true;
				BPConfig config = m_lstcfgs.getSelectedValue();
				config.setMappedData(m_form.getFormData());
				config.save();
				break;
			}
			case COMMAND_APPLY:
			{
				if (m_form.validateForm())
				{
					BPConfig config = m_lstcfgs.getSelectedValue();
					config.setMappedData(m_form.getFormData());
					config.save();
				}
				return true;
			}
			case COMMAND_CANCEL:
			{
				break;
			}
		}
		return false;
	}

	protected void initDatas()
	{
		BPList.BPListModel<BPConfig> model = new BPList.BPListModel<BPConfig>();
		m_lstcfgs.setModel(model);

		List<BPConfig> cfgs = BPCore.getConfigManager().getConfigs();
		List<BPConfig> fcfgs = new ArrayList<BPConfig>();
		for (BPConfig cfg : cfgs)
		{
			if (cfg.canUserConfig())
				fcfgs.add(cfg);
		}
		model.setDatas(fcfgs);
		m_lstcfgs.updateUI();

	}
}
