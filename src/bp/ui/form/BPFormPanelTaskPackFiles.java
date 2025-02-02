package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.ui.dialog.BPDialogSelectResourceDir;
import bp.ui.dialog.BPDialogSelectResourceList;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;

public class BPFormPanelTaskPackFiles extends BPFormPanelTask
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2160898383312639639L;

	protected BPTextField m_txtsrc;
	protected BPTextField m_txtsrcbase;
	protected BPTextField m_txttar;
	protected BPTextField m_txttardir;
	protected BPTextFieldPane m_pansrc;
	protected BPTextFieldPane m_pansrcbase;
	protected BPTextFieldPane m_pantardir;
	protected BPCheckBox m_chkpacklist;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		rc.put("source", m_txtsrc.getText());
		rc.put("sourcebase", m_txtsrcbase.getText());
		rc.put("target", m_txttar.getText());
		rc.put("targetdir", m_txttardir.getText());
		rc.put("packlist", m_chkpacklist.isSelected());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();

		m_pansrc = makeSingleLineTextFieldPanel(this::onSelectDirList);
		m_txtsrc = m_pansrc.getTextComponent();

		m_pansrcbase = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txtsrcbase = m_pansrcbase.getTextComponent();

		m_txttar = makeSingleLineTextField();

		m_pantardir = makeSingleLineTextFieldPanel(this::onSelectDir);
		m_txttardir = m_pantardir.getTextComponent();

		m_chkpacklist = makeCheckBox();

		addLine(new String[] { "Source" }, new Component[] { m_pansrc }, () -> !m_txtsrc.isEmpty());
		addLine(new String[] { "Target" }, new Component[] { m_txttar }, () -> !m_txttar.isEmpty());
		addLine(new String[] { "Target Dir" }, new Component[] { m_pantardir });
		addLine(new String[] { "Source Base" }, new Component[] { m_pansrcbase });
		addLine(new String[] { "Save Source List" }, new Component[] { m_chkpacklist });
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txtsrc, data, "source", editable);
		setComponentValue(m_txtsrcbase, data, "sourcebase", editable);
		setComponentValue(m_txttar, data, "target", editable);
		setComponentValue(m_txttardir, data, "targetdir", editable);
		setComponentValue(m_chkpacklist, data, "packlist", editable);
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
		String[] ops = oldpath.split(";");
		for (String op : ops)
		{
			oldress.add(BPCore.getFileContext().getRes(op));
		}
		BPDialogSelectResourceList dlg = new BPDialogSelectResourceList();
		dlg.setResourceList(oldress);
		dlg.switchPathTreeFuncs();
		dlg.setVisible(true);
		List<BPResource> rs = dlg.getResult();
		if (rs != null)
		{
			StringBuilder sb = new StringBuilder();
			for (BPResource r : rs)
			{
				if (r != null)
				{
					if (sb.length() > 0)
						sb.append(";");
					sb.append(BPCore.getFileContext().comparePath(((BPResourceDir) r).getFileFullName()));
				}
			}
			rc = sb.toString();
		}
		return rc;
	}
}