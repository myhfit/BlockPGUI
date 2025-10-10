package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileSystem;
import bp.task.BPTaskUnpackFiles;
import bp.task.BPTaskUnpackFiles.OVERWRITE_MODE;
import bp.ui.dialog.BPDialogSelectResourceDir;
import bp.ui.dialog.BPDialogSelectResourceList;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.util.LogicUtil;
import bp.util.ObjUtil;

public class BPFormPanelTaskUnpackFiles extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 613170451604826398L;

	protected BPTextField m_txtsrc;
	protected BPTextField m_txtsrcdir;
	protected BPTextField m_txttar;
	protected BPTextField m_txttarbase;
	protected BPTextFieldPane m_pantar;
	protected BPTextFieldPane m_pansrcdir;
	protected BPTextFieldPane m_pantarbase;
	protected BPCheckBox m_chkpacklist;
	protected BPComboBox<OVERWRITE_MODE> m_cmbowmode;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("source", m_txtsrc.getText());
		rc.put("sourcebase", m_txtsrcdir.getText());
		rc.put("target", m_txttar.getText());
		rc.put("targetdir", m_txttarbase.getText());
		rc.put("readpacklist", m_chkpacklist.isSelected());
		rc.put("owmode", LogicUtil.IFVR((OVERWRITE_MODE) m_cmbowmode.getSelectedItem(), v -> v == null ? null : v.name()));
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_txtsrc = makeSingleLineTextField();

		m_pansrcdir = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txtsrcdir = m_pansrcdir.getTextComponent();

		m_pantar = makeSingleLineTextFieldPanel(this::onSelectDirList);
		m_txttar = m_pantar.getTextComponent();

		m_pantarbase = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txttarbase = m_pantarbase.getTextComponent();

		m_chkpacklist = makeCheckBox();
		m_cmbowmode = makeComboBox(null);
		{
			BPComboBoxModel<OVERWRITE_MODE> model = new BPComboBoxModel<OVERWRITE_MODE>();
			model.setDatas(Arrays.asList(BPTaskUnpackFiles.OVERWRITE_MODE.values()));
			m_cmbowmode.setModel(model);
		}

		addLine(new String[] { "Source" }, new Component[] { m_txtsrc }, () -> !m_txtsrc.isEmpty());
		addLine(new String[] { "Target" }, new Component[] { m_pantar });
		addLine(new String[] { "Target Base" }, new Component[] { m_pantarbase });
		addLine(new String[] { "Source Dir" }, new Component[] { m_pansrcdir });
		addLine(new String[] { "Read Source List" }, new Component[] { m_chkpacklist });
		addLine(new String[] { "Overwrite Mode" }, new Component[] { m_cmbowmode });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtsrc, data, "source", editable);
		setComponentValue(m_txtsrcdir, data, "sourcedir", editable);
		setComponentValue(m_txttar, data, "target", editable);
		setComponentValue(m_txttarbase, data, "targetbase", editable);
		setComponentValue(m_chkpacklist, data, "readpacklist", editable);
		setComponentValue(m_cmbowmode, data, "owmode", editable, d -> LogicUtil.IFVR(d.get("owmode"), d2 -> d2 == null ? null : OVERWRITE_MODE.valueOf((String) d2)));
	}

	protected String onSelectDir(String oldpath)
	{
		String rc = null;
		BPDialogSelectResourceDir dlg = new BPDialogSelectResourceDir();
		dlg.setVisible(true);
		BPResource res = dlg.getSelectedResource();
		if (res != null)
		{
			rc = BPCore.getFileContext().comparePath(((BPResourceDir) res).getFileFullName());
		}
		return rc;
	}

	protected String onSelectDirList(String oldpath)
	{
		String rc = null;
		List<BPResource> oldress = new ArrayList<BPResource>();
		if (oldpath.trim().length() > 0)
		{
			String[] ops = oldpath.split(";");
			for (String op : ops)
				LogicUtil.IFVU(BPCore.getFileContext().getRes(op), res -> oldress.add(res));
		}
		BPDialogSelectResourceList dlg = new BPDialogSelectResourceList();
		dlg.setResourceList(oldress);
		dlg.switchPathTreeFuncs();
		dlg.setVisible(true);
		List<BPResource> rs = dlg.getResult();
		if (rs != null)
			rc = ObjUtil.joinDatas(rs, ";", res -> BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName()), false);
		return rc;
	}
}