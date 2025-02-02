package bp.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.ref.WeakReference;
import java.util.List;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.util.UIUtil;

public class BPProjectOverviewPanelCommon extends JPanel implements BPProjectOverviewComp<BPResourceProject>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6973313529298926478L;

	protected WeakReference<BPResourceProject> m_prjref;
	protected JPanel m_panmain;

	public BPProjectOverviewPanelCommon()
	{
		initUI();
	}

	protected void initUI()
	{
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_panmain = new JPanel();
		m_panmain.setLayout(new BoxLayout(m_panmain, BoxLayout.Y_AXIS));

		scroll.setViewportView(m_panmain);

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
	}

	public void setup(BPResourceProject prj)
	{
		m_prjref = new WeakReference<BPResourceProject>(prj);
		initDatas();
	}

	protected void initDatas()
	{
		m_panmain.removeAll();
		WeakReference<BPResourceProject> prjref = m_prjref;
		if (prjref != null)
		{
			BPResourceProject prj = prjref.get();
			if (prj != null)
			{
				List<BPResource> ress = prj.getProjectFunctionItems();
				for (BPResource res : ress)
				{
					Action[] acts = getResourceActions(res);
					Component[] btns = new Component[acts.length];
					for (int i = 0; i < acts.length; i++)
					{
						BPToolVIconButton btn = new BPToolVIconButton(acts[i]);
						btns[i] = btn;
					}
					JPanel pnl = makeFLine(res.getName(), btns);
					m_panmain.add(pnl);
				}
			}
		}
	}

	protected Action[] getResourceActions(BPResource res)
	{
		BPAction actopen = BPAction.build("open").callback((e) -> openResource(res)).vIcon(BPIconResV.START()).getAction();
		Action[] rc = new Action[] { actopen };
		return rc;
	}

	protected void openResource(BPResource res)
	{
		BPGUICore.runOnMainFrame(mf -> mf.openResource(res, null, null, false, UIUtil.getRoutableContainerID(this)));
	}

	protected JPanel makeFLine(String label, Component... comps)
	{
		JPanel rc = new JPanel();
		BPLabel lbl = new BPLabel(label);
		lbl.setMonoFont();
		lbl.setPreferredSize(new Dimension(UIUtil.scale(120), UIConfigs.TEXTFIELD_HEIGHT()));

		rc.setLayout(new BorderLayout());
		rc.add(lbl, BorderLayout.WEST);
		JPanel pnlr = new JPanel();
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT, 1, 1);
		pnlr.setLayout(fl);
		for (Component comp : comps)
		{
			pnlr.add(comp);
		}
		rc.add(pnlr, BorderLayout.CENTER);
		rc.setMaximumSize(new Dimension(4000, UIConfigs.TEXTFIELD_HEIGHT()));
		rc.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 0), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		return rc;
	}
}
