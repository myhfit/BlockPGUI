package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import bp.client.BPClient;
import bp.config.UIConfigs;
import bp.ui.data.BPDataUIManager;
import bp.ui.form.BPFormPanel;
import bp.ui.form.BPFormPanelList;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPClientComponent extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 969701863234535920L;

	protected volatile BPClient m_client;

	protected JPanel m_pmain;
	protected BPTextField m_txtaction;
	protected BPFormPanel m_formparams;
	protected JComponent m_resultcomp;

	public BPClientComponent()
	{
		initUI();
	}

	protected void initUI()
	{
		JPanel pleft = new JPanel();
		JPanel pleft0 = new JPanel();
		JPanel pleft1 = new JPanel();
		m_pmain = new JPanel();
		m_formparams = new BPFormPanelList();
		m_txtaction = new BPTextField();
		BPLabel lblaction = new BPLabel(" Action");
		BPLabel lblparams = new BPLabel(" Parameters");
		BPLabel lblresult = new BPLabel(" Result");

		lblaction.setLabelFont();
		lblparams.setLabelFont();
		lblresult.setLabelFont();
		m_txtaction.setMonoFont();

		pleft.setPreferredSize(new Dimension(200, 200));

		lblaction.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_TEXTQUARTER()));
		lblparams.setBorder(new MatteBorder(1, 0, 1, 0, UIConfigs.COLOR_TEXTQUARTER()));
		lblresult.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_TEXTQUARTER()));
		m_pmain.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		pleft0.setLayout(new BorderLayout());
		pleft1.setLayout(new BorderLayout());
		pleft0.add(lblaction, BorderLayout.NORTH);
		pleft0.add(m_txtaction, BorderLayout.CENTER);
		pleft1.add(lblparams, BorderLayout.NORTH);
		pleft1.add(m_formparams, BorderLayout.CENTER);
		pleft.setLayout(new BorderLayout());
		m_pmain.setLayout(new BorderLayout());
		pleft.add(pleft0, BorderLayout.NORTH);
		pleft.add(pleft1, BorderLayout.CENTER);
		m_pmain.add(lblresult, BorderLayout.NORTH);

		m_formparams.showData(ObjUtil.makeMap("_list", new ArrayList<>()));

		setLayout(new BorderLayout());
		add(pleft, BorderLayout.WEST);
		add(m_pmain, BorderLayout.CENTER);
	}

	public void setup(BPClient client)
	{
		m_client = client;
		if (m_txtaction.getText().length() == 0)
			m_txtaction.setText("");
		m_formparams.showData(ObjUtil.makeMap("_list", new ArrayList<>()));
	}

	public void run()
	{
		String actionname = m_txtaction.getText();
		if (actionname == null || actionname.length() == 0)
		{
			UIStd.info("Need action name");
			return;
		}

		List<?> ps = (List<?>) m_formparams.getFormData().get("_list");

		BPClient client = m_client;
		Object obj = UIUtil.block(() -> CompletableFuture.supplyAsync(() -> client.call(actionname, ps.toArray())), "Call Action:" + actionname);
		if (obj == null)
		{
			UIStd.info("Result empty");
		}
		else
		{
			JComponent resultcomp = BPDataUIManager.getUIForData(obj);
			setResultUI(resultcomp);
		}
	}

	protected void setResultUI(JComponent comp)
	{
		JComponent rcomp = m_resultcomp;
		m_resultcomp = null;
		if (rcomp != null)
		{
			m_pmain.remove(rcomp);
		}
		m_resultcomp = comp;
		m_pmain.add(comp, BorderLayout.CENTER);
		m_pmain.updateUI();
	}
}
