package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import bp.BPGUICore;
import bp.data.BPMData;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;

public class BPDialogForm extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1922742535586884177L;

	protected String m_key;
	protected String m_leafclsname;
	protected String m_rootclsname;
	protected Map<String, Object> m_data;
	protected Map<String, Object> m_result;
	protected BPForm<?> m_form;

	protected boolean m_editable = true;

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				if (!m_form.validateForm())
					return true;
				m_result = m_form.getFormData();
				break;
			}
			case COMMAND_CANCEL:
			{
				break;
			}
		}
		return false;
	}

	protected void initUIComponents()
	{
		setLayout(new BorderLayout());
		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setModal(true);
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(640, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		if (m_form != null)
			remove(m_form.getComponent());
		if (m_key != null)
		{
			m_form = BPFormManager.getForm(m_key);
			if (m_form == null)
				m_form = BPFormManager.getFormByClassTree(ClassUtil.getEClass(m_key), null);
		}
		if (m_form == null && m_leafclsname != null)
			m_form = BPFormManager.getFormByClassTree(ClassUtil.getEClass(m_leafclsname), m_rootclsname != null ? ClassUtil.getEClass(m_rootclsname) : null);
		if (m_form != null)
		{
			m_form.showData(m_data, m_editable);
			add(m_form.getComponent(), BorderLayout.CENTER);
		}
	}

	public void setup(String key, BPMData data)
	{
		setup(key, data.getMappedData());
	}

	public void setup(String key, Map<String, Object> data)
	{
		m_key = key;
		m_data = data;
		initDatas();
	}

	public void setup(Class<?> leafcls, Class<?> rootcls, BPMData data)
	{
		setup(leafcls, rootcls, data.getMappedData());
	}

	public void setup(Class<?> leafcls, Class<?> rootcls, Map<String, Object> data)
	{
		m_leafclsname = leafcls.getName();
		m_rootclsname = rootcls == null ? null : rootcls.getName();
		m_data = data;
		initDatas();
	}

	public void setCommandBarMode(int mode)
	{
		super.setCommandBarMode(mode);
	}

	public Map<String, Object> getFormData()
	{
		return m_result;
	}

	public void setEditable(boolean flag)
	{
		m_editable = flag;
	}

	public final static Map<String, Object> showEdit(BPMData obj, Component par)
	{
		BPDialogForm dlg = new BPDialogForm();
		dlg.setTitle(BPGUICore.S_BP_TITLE);
		dlg.setup(obj.getClass(), null, obj);
		dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		dlg.pack();
		dlg.setLocationRelativeTo(par);
		dlg.setVisible(true);
		return dlg.getFormData();
	}
}
