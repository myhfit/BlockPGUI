package bp.ui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.env.BPEnv;
import bp.ui.dialog.BPDialogForm;
import bp.ui.scomp.BPBoxButtons;
import bp.ui.util.UIUtil;

public class BPFormPanelEnvs extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5931448552413089447L;

	protected BPBoxButtons<BPEnv> m_lstenvs;

	protected boolean needScroll()
	{
		return false;
	}

	public Map<String, Object> getFormData()
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	public void showData(Map<String, ?> data, boolean editable)
	{
		List<BPEnv> envs = (List<BPEnv>) data.get("envs");
		m_lstenvs.setDatas(envs);
	}

	protected void initForm()
	{
		m_lstenvs = new BPBoxButtons<BPEnv>(BoxLayout.Y_AXIS);
		m_lstenvs.setShowDelete(false);
		m_lstenvs.setShowSelect(false);
		JLabel lbl = makeLineLabel("Environments", true);
		JScrollPane scroll = new JScrollPane();

		m_lstenvs.setRenderer(this::renderEnv);
		m_lstenvs.setClickHandler(this::onClickEnv);

		lbl.setPreferredSize(new Dimension(m_labelwidth, m_lineheight));
		lbl.setMinimumSize(new Dimension(m_labelwidth, m_lineheight));

		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(m_lstenvs);

		m_lstenvs.setDatas(new ArrayList<BPEnv>());

		doAddLineComponents(null, false, 0, new Component[] { scroll });
	}

	protected String renderEnv(BPEnv env)
	{
		return env.getName();
	}

	protected void onClickEnv(BPEnv env)
	{
		BPDialogForm dlg = new BPDialogForm();
		dlg.setTitle("BlockP - Environment : " + env.getName());
		dlg.setup(BPEnv.class.getName(), env);
		dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		dlg.pack();
		dlg.setLocationRelativeTo(this.getTopLevelAncestor());
		dlg.setVisible(true);
		Map<String, Object> result = dlg.getFormData();
		if (result != null)
			env.setMappedData(result);
	}
}
