package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.client.BPClient;
import bp.client.BPClientFactory;
import bp.client.BPClientManager;
import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPClientComponent;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.util.UIStd;
import bp.util.JSONUtil;
import bp.ui.scomp.BPToolVIconButton;

public class BPClientPanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7828407629656565087L;

	protected BPComboBox<BPClientFactory> m_cmbfacs;
	protected BPClientComponent m_ccomp;

	protected String m_id;
	protected int m_channelid;

	private BPAction m_actrunclient;

	public BPClientPanel()
	{
		initUIComponents();
		initDatas();
	}

	protected void initUIComponents()
	{
		JPanel sp = new JPanel();
		BPToolBarSQ toolbar = new BPToolBarSQ();
		m_cmbfacs = new BPComboBox<BPClientFactory>();
		Action actuseclient = BPAction.build("useclient").tooltip("Use Client").vIcon(BPIconResV.TODOWN()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)).callback(this::onUseClient).getAction();
		m_actrunclient = BPAction.build("run").tooltip("Run Client").vIcon(BPIconResV.START()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).callback(this::onRunClient).getAction();
		BPToolVIconButton btnuseclient = new BPToolVIconButton(actuseclient);
		BPToolVIconButton btnrunclient = new BPToolVIconButton(m_actrunclient);
		m_actrunclient.putValue("enabled", false);
		JScrollPane scroll = new JScrollPane();
		m_ccomp = new BPClientComponent();

		m_cmbfacs.setRenderer(new BPComboBox.BPComboBoxRenderer(obj -> ((BPClientFactory) obj).getName()));
		toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		m_cmbfacs.setListFont();
		m_cmbfacs.replaceWBorder();

		toolbar.add(btnuseclient);
		toolbar.add(btnrunclient);
		toolbar.add(Box.createRigidArea(new Dimension(2, 1)));
		toolbar.add(m_cmbfacs);
		toolbar.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));

		setLayout(new BorderLayout());
		sp.setLayout(new BorderLayout());
		sp.add(m_ccomp, BorderLayout.CENTER);
		add(sp, BorderLayout.CENTER);
		add(toolbar, BorderLayout.NORTH);
	}

	protected void initDatas()
	{
		List<BPClientFactory> facs = BPClientManager.list();
		BPComboBoxModel<BPClientFactory> model = new BPComboBoxModel<BPClientFactory>();
		model.setDatas(facs);
		m_cmbfacs.setModel(model);
		if (facs.size() > 0)
			m_cmbfacs.setSelectedIndex(0);
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	public void save()
	{
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return false;
	}

	public void setNeedSave(boolean needsave)
	{
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public String getEditorName()
	{
		return "Client Panel";
	}

	protected void onUseClient(ActionEvent e)
	{
		String text = UIStd.input("{}", "Input create params(json)", "BlockP - Create Client");
		if (text != null)
		{
			Map<String, Object> ps = null;
			if (text.length() > 0)
				ps = JSONUtil.decode(text);
			BPClientFactory fac = (BPClientFactory) m_cmbfacs.getSelectedItem();
			BPClient client = fac.getClient(ps);
			useClient(client);
		}
	}

	protected void useClient(BPClient client)
	{
		m_ccomp.setup(client);
		m_actrunclient.setEnabled(true);
	}

	protected void onRunClient(ActionEvent e)
	{
		m_ccomp.run();
	}
}