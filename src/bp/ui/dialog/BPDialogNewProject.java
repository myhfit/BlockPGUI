package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.BPCore;
import bp.config.UIConfigs;
import bp.locale.BPLocaleConstProjectDict;
import bp.locale.BPLocaleHelpers;
import bp.project.BPProjectFactory;
import bp.project.BPResourceProject;
import bp.res.BPResourceDirLocal;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;

public class BPDialogNewProject extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3556973484245269229L;

	protected BPResourceProject m_project;
	protected BPList<BPProjectFactory> m_lstfacs;
	protected BPForm<?> m_form;

	protected void initUIComponents()
	{
		m_lstfacs = new BPList<BPProjectFactory>();
		m_lstfacs.setModel(new BPList.BPListModel<BPProjectFactory>());
		m_lstfacs.setCellRenderer(new BPList.BPListRendererT<>(BPDialogNewProject::transFacName));
		m_lstfacs.setListFont();

		JPanel leftpan = new JPanel();
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(m_lstfacs, BorderLayout.CENTER);
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(150, 0)));

		setLayout(new BorderLayout());
		add(leftpan, BorderLayout.WEST);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle(UIUtil.wrapBPTitle(BPActionConstCommon.TXT_NEWPRJ));
		setModal(true);
	}

	private static Object transFacName(Object facobj)
	{
		BPProjectFactory fac = (BPProjectFactory) facobj;
		String facname = fac == null ? "" : fac.getName();
		return BPLocaleHelpers.translate(BPLocaleConstProjectDict.S, facname, "PRJNAME_");
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		ServiceLoader<BPProjectFactory> facs = ClassUtil.getExtensionServices(BPProjectFactory.class);
		List<BPProjectFactory> datas = new ArrayList<BPProjectFactory>();
		for (BPProjectFactory fac : facs)
			datas.add(fac);
		((BPList.BPListModel<BPProjectFactory>) m_lstfacs.getModel()).setDatas(datas);
		m_lstfacs.addListSelectionListener(this::onListSelectionChange);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting() && m_lstfacs.getSelectedIndex() != -1)
		{
			if (m_form != null)
				remove(m_form.getComponent());
			m_form = BPFormManager.getFormByClassTree(m_lstfacs.getSelectedValue().getProjectClass(), BPResourceProject.class);
			if (m_form != null)
				add(m_form.getComponent(), BorderLayout.CENTER);
			validate();
			repaint();
		}
	}
	
	protected String getSelectedProjectType()
	{
		return m_lstfacs.getSelectedValue().getProjectTypes().get(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected BPResourceProject createProject(Map<String, Object> data)
	{
		BPResourceProject rc = null;
		String path = (String) data.get("path");
		BPResourceDirLocal dir = ((path == null) ? null : (BPResourceDirLocal) BPCore.getFileContext().getDir(path));
		rc = m_lstfacs.getSelectedValue().create(getSelectedProjectType(), dir, (Map) data);
		rc.initProjectDatas();
		if (rc.canCache())
			rc.startCache();
		return rc;
	}

	public BPResourceProject getProject()
	{
		return m_project;
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			if (m_form.validateForm())
			{
				Map<String, Object> data = m_form.getFormData();
				m_project = createProject(data);
			}
			else
			{
				return true;
			}
		}
		return false;
	}
}