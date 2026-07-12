package bp.ui.form;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import bp.BPCore;
import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleConstProjectDict;
import bp.locale.BPLocaleHelpers;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.ui.dialog.BPDialogSelectResource.SELECTSCOPE;
import bp.ui.dialog.BPDialogSelectResource.SELECTTYPE;
import bp.ui.dialog.BPDialogSelectResource2;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.util.LogicUtil;

public class BPFormPanelFileProject extends BPFormPanelResourceBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922930958849625L;

	protected BPTextField m_txtpath;
	protected BPTextFieldPane m_pathpan;
	protected BPCheckBox m_chknocache;
	protected String m_oldprjname;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_txtname.getNotEmptyText());
		rc.put("path", m_txtpath.getNotEmptyText());
		rc.put("nocache", m_chknocache.isSelected() ? "true" : "false");
		return rc;
	}

	protected boolean isDefaultNoCache()
	{
		return false;
	}

	protected void initForm()
	{
		super.initForm();

		m_pathpan = makeSingleLineTextFieldPanel(this::onPathMore);
		m_txtpath = m_pathpan.getTextComponent();
		m_chknocache = new BPCheckBox();
		m_chknocache.setSelected(isDefaultNoCache());

		m_chknocache.setLabelFont();

		addLine(new String[] { BPLocaleConstCC.PATH.text() }, new Component[] { m_pathpan }, () -> !m_txtpath.isEmpty());
		addLine(new String[] { BPLocaleHelpers.translate(BPLocaleConstProjectDict.S, "NoCache") }, new Component[] { wrapSingleLineComponent(m_chknocache) });

		m_checks.set(0, LogicUtil.andCheck(m_checks.get(0), this::checkProjectName));
	}

	protected boolean checkProjectName()
	{
		String prjname = m_txtname.getNotEmptyText();
		if (!prjname.equals(m_oldprjname))
			return BPCore.getProjectsContext().checkProjectName(m_txtname.getNotEmptyText());
		else
			return true;
	}

	protected String onPathMore(String oldpath)
	{
		String rc = null;
		BPDialogSelectResource2 dlg = new BPDialogSelectResource2();
		dlg.setSelectType(SELECTTYPE.DIR);
		dlg.setScopes(SELECTSCOPE.WORKSPACE, SELECTSCOPE.COMPUTER);
		dlg.switchPathTreeFunc(1);
		dlg.showOpen();
		BPResource res = dlg.getSelectedResource();
		if (res != null)
			rc = BPCore.getFileContext().comparePath(((BPResourceDir) res).getFileFullName());
		return rc;
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		m_oldprjname = (String) data.get("name");
		setComponentValue(m_txtpath, data, "path", editable);
		m_pathpan.setEditable(false);
		setComponentValue(m_chknocache, data, "nocache", editable);
	}
}
