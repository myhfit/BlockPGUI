package bp.ui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.env.BPEnv;
import bp.env.BPEnvs;
import bp.locale.BPLocaleHelpers;
import bp.ui.actions.BPActionConstCommon;
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
		m_lstenvs.setDatas((List<BPEnv>) data.get("envs"));
	}

	protected void initForm()
	{
		m_lstenvs = new BPBoxButtons<BPEnv>(BoxLayout.Y_AXIS);
		m_lstenvs.setShowDelete(false);
		m_lstenvs.setShowSelect(false);
		JScrollPane scroll = new JScrollPane();

		m_lstenvs.setRenderer(this::renderEnv);
		m_lstenvs.setClickHandler(this::onClickEnv);

		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(m_lstenvs);

		m_lstenvs.setDatas(new ArrayList<BPEnv>());

		doAddLineComponents(null, false, 0, new Component[] { scroll });
	}

	protected String renderEnv(BPEnv env)
	{
		return BPLocaleHelpers.translateByClass(BPEnvs.class, env.getName());
	}

	protected void onClickEnv(BPEnv env)
	{
		BPDialogForm dlg = new BPDialogForm();
		dlg.setTitle(UIUtil.wrapBPTitle(BPActionConstCommon.TXT_ENV) + " : " + BPLocaleHelpers.translateByClass(BPEnvs.class, env.getName()));
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
