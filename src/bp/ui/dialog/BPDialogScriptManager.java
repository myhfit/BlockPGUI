package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.BPCore;
import bp.config.UIConfigs;
import bp.context.BPWorkspaceContext;
import bp.script.BPScript;
import bp.script.BPScriptBase;
import bp.script.BPScriptManager;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;

public class BPDialogScriptManager extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4462914198318332570L;

	protected BPList<BPScript> m_lstscripts;
	protected BPForm<?> m_form;
	protected BPToolBarSQ m_lstbar;
	protected boolean m_isnewsc = false;
	protected List<BPScript> m_scs;
	protected JPanel m_rightpan;

	protected void initUIComponents()
	{
		m_lstscripts = new BPList<BPScript>();
		m_lstscripts.setListFont();
		m_lstscripts.setCellRenderer(new BPList.BPListRenderer((script) -> ((BPScript) script).getName()));
		m_lstscripts.addListSelectionListener(this::onListSelectionChange);

		m_lstbar = new BPToolBarSQ();
		m_lstbar.setBarHeight(20);
		Action newscaction = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNADD, this::onCreateScript);
		Action delscaction = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, this::onRemoveScript);
		m_lstbar.setActions(new Action[] { newscaction, delscaction });
		m_lstbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(m_lstscripts);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel leftpan = new JPanel(new BorderLayout());
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(160, 0)));
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));

		BPToolBarSQ tbright = new BPToolBarSQ();
		tbright.setBarHeight(20);
		Action actsave = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNSAVE, BPActionConstCommon.ACT_BTNSAVE_ACC, this::onSaveScript);
		Action actrun = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNRUN, BPActionConstCommon.ACT_BTNRUN_ACC, this::onRunScript);
		tbright.setActions(new Action[] { actsave, actrun }, this.getRootPane());
		tbright.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));
		m_rightpan = new JPanel(new BorderLayout());
		m_rightpan.add(tbright, BorderLayout.NORTH);

		setTitle("BlockP - Scripts");

		setLayout(new BorderLayout());

		leftpan.add(scroll, BorderLayout.CENTER);
		leftpan.add(m_lstbar, BorderLayout.NORTH);
		add(leftpan, BorderLayout.WEST);
		add(m_rightpan, BorderLayout.CENTER);

		setModal(true);
	}

	protected void onSaveScript(ActionEvent e)
	{
		BPScript sc = saveCurrentScript();
		if (m_isnewsc)
		{
			m_isnewsc = false;
			initDatas();
			m_lstscripts.setSelectedValue(sc, true);

		}
		else
		{
			m_lstscripts.updateUI();
		}
	}

	protected void removeForm()
	{
		if (m_form != null)
		{
			m_rightpan.remove(m_form.getComponent());
		}
	}

	protected void onRunScript(ActionEvent e)
	{
		BPScript script = getSelectedScript();
		script = script.clone();
		script.setMappedData(m_form.getFormData());
		if (script != null)
			BPCore.getWorkspaceContext().getScriptManager().runScripts(new BPScript[] { script }, null, null, true);
	}

	protected void onRemoveScript(ActionEvent e)
	{
		BPScript script = m_lstscripts.getSelectedValue();
		if (script != null)
		{
			BPWorkspaceContext context = BPCore.getWorkspaceContext();
			context.saveScript(script, null);
			initDatas();
		}
	}

	protected void onCreateScript(ActionEvent e)
	{
		m_isnewsc = true;
		BPScript script = new BPScriptBase();
		removeForm();
		m_lstscripts.setSelectedIndices(new int[] {});
		m_form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), script.getClass(), BPScript.class);
		if (m_form != null)
		{
			m_rightpan.add(m_form.getComponent(), BorderLayout.CENTER, 0);
			m_form.showData(script.getMappedData());
		}
		validate();
		repaint();
	}

	protected void initDatas()
	{
		BPWorkspaceContext context = BPCore.getWorkspaceContext();
		BPScriptManager scman = context.getScriptManager();
		m_scs = scman.listScripts();
		BPList.BPListModel<BPScript> model = new BPList.BPListModel<BPScript>();
		model.setDatas(m_scs);
		m_lstscripts.setModel(model);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			removeForm();
			BPScript script = m_lstscripts.getSelectedValue();
			if (script != null)
			{
				m_form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), script.getClass(), BPScript.class);
				if (m_form != null)
				{
					m_rightpan.add(m_form.getComponent(), BorderLayout.CENTER, 0);
					m_form.showData(script.getMappedData());
				}
			}
			validate();
			repaint();
		}
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	protected BPScript getSelectedScript()
	{
		if (m_isnewsc)
		{
			return null;
		}
		return m_lstscripts.getSelectedValue();
	}

	protected BPScript saveCurrentScript()
	{
		BPScript script = getSelectedScript();
		return BPCore.getWorkspaceContext().saveScript(script, m_form.getFormData());
	}

	public boolean doCallCommonAction(int command)
	{
		return false;
	}
}
