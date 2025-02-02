package bp.ui.view;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import bp.BPGUICore;
import bp.format.BPFormatProject;
import bp.project.BPResourceProject;
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPProjectBlockViewComp extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 807897189035705605L;

	protected WeakReference<BPResourceProject> m_prjref;

	public BPProjectBlockViewComp()
	{
		initUI();
	}

	public void initUI()
	{
		setBorder(new BevelBorder(BevelBorder.RAISED));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addMouseListener(new UIUtil.BPMouseListener(null, this::onMouseDown, null, null, null));
	}

	protected void onMouseDown(MouseEvent e)
	{
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		WeakReference<BPResourceProject> prjref = m_prjref;
		if (prjref == null)
			return;
		BPResourceProject prj = m_prjref.get();
		if (prj == null)
			return;
		BPGUICore.runOnMainFrame(mf -> mf.openResource(prj, new BPFormatProject(), null, false, UIUtil.getRoutableContainerID(this)));
	}

	@SuppressWarnings("unchecked")
	public void initData(BPResourceProject prj)
	{
		removeAll();

		m_prjref = new WeakReference<BPResourceProject>(prj);
		Map<String, Object> data = prj.getOverview();

		BPLabel lbltitle = new BPLabel();
		lbltitle.setHorizontalAlignment(BPLabel.CENTER);
		lbltitle.setLabelFont();
		lbltitle.setFont(UIUtil.deltaFont(lbltitle.getFont(), 3));

		BPLabel lblprjtype = new BPLabel();
		lblprjtype.setHorizontalAlignment(BPLabel.CENTER);
		lblprjtype.setText("Type:");
		lblprjtype.setMonoFont();

		JPanel pantitle = new JPanel();
		pantitle.setLayout(new BorderLayout());
		pantitle.add(lbltitle, BorderLayout.CENTER);

		JPanel panline = new JPanel();
		panline.setLayout(new BorderLayout());
		panline.add(lblprjtype, BorderLayout.CENTER);

		lbltitle.setText((String) data.get("name"));
		lblprjtype.setText("type:" + (String) data.get("prjtype"));

		add(pantitle);
		add(panline);

		List<String> extrakeys = (List<String>) data.get("extrakeys");
		if (extrakeys != null)
		{
			for (String extrakey : extrakeys)
			{
				String v = ObjUtil.toString(data.get(extrakey), "");
				JPanel panextra = new JPanel();
				BPLabel lblextra = new BPLabel();
				lblextra.setHorizontalAlignment(BPLabel.CENTER);
				lblextra.setMonoFont();
				lblextra.setText(extrakey + ":" + v);
				panextra.setLayout(new BorderLayout());
				panextra.add(lblextra, BorderLayout.CENTER);
				add(panextra);
			}
		}
	}
}
