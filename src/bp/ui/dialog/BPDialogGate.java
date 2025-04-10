package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

@SuppressWarnings("serial")
public class BPDialogGate extends BPDialog
{
	protected BPTextField m_txtjavahome;
	protected BPComboBox<String> m_cmbhome;
	protected JButton m_btnok;
	protected JButton m_btncancel;
	protected JButton m_btnext;
	protected BPCheckBox[] m_chks;
	protected JPanel m_panright;
	protected JScrollPane m_scrollright;

	protected Map<String, String> m_envs;
	protected Map<String, String> m_result;

	protected void initUIComponents()
	{
		JPanel panmain = new JPanel();
		m_txtjavahome = new BPTextField();
		m_cmbhome = new BPComboBox<String>();
		JPanel line0 = new JPanel();
		JPanel line1 = new JPanel();
		JPanel panact = new JPanel();
		m_scrollright = new JScrollPane();
		m_panright = new JPanel();
		BPLabel lbljavahome = new BPLabel("Java Home:");
		BPLabel lblhome = new BPLabel("Workspace:");
		m_btnok = new JButton(BPAction.build("OK").mnemonicKey(KeyEvent.VK_O).callback(this::onOK).getAction());
		m_btncancel = new JButton(BPAction.build("Cancel").mnemonicKey(KeyEvent.VK_C).callback(this::onCancel).getAction());
		m_btnext = new JButton(BPAction.build("Extension").mnemonicKey(KeyEvent.VK_E).callback(this::onExtension).getAction());
		m_btnok.registerKeyboardAction(this::onOK, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		m_btncancel.registerKeyboardAction(this::onCancel, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		m_txtjavahome.setEditable(false);
		UIUtil.transComponentFont(lbljavahome, true, true, 0);
		UIUtil.transComponentFont(lblhome, true, true, 0);
		UIUtil.transComponentFont(m_txtjavahome, true, false, 0);
		UIUtil.transComponentFont(m_cmbhome, true, false, 0);
		m_cmbhome.setEditable(true);
		m_scrollright.setVisible(false);
		m_scrollright.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		line0.setBorder(new EmptyBorder(2, 4, 2, 2));
		line1.setBorder(new EmptyBorder(0, 4, 2, 2));
		m_scrollright.setBorder(new EmptyBorder(0, 0, 0, 0));

		Dimension d = new Dimension(300, UIConfigs.TEXTFIELD_HEIGHT() + 4);
		line0.setPreferredSize(d);
		line0.setMaximumSize(d);
		line1.setPreferredSize(d);
		line1.setMaximumSize(d);

		line0.setLayout(new BorderLayout());
		line1.setLayout(new BorderLayout());
		panmain.setLayout(new BoxLayout(panmain, BoxLayout.Y_AXIS));
		m_panright.setLayout(new BoxLayout(m_panright, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());

		line0.add(lbljavahome, BorderLayout.WEST);
		line0.add(m_txtjavahome, BorderLayout.CENTER);
		line1.add(lblhome, BorderLayout.WEST);
		line1.add(m_cmbhome, BorderLayout.CENTER);
		panmain.add(line0);
		panmain.add(line1);

		panact.add(m_btnok);
		panact.add(m_btncancel);
		panact.add(m_btnext);

		m_scrollright.setViewportView(m_panright);

		add(panmain, BorderLayout.CENTER);
		add(m_scrollright, BorderLayout.EAST);
		add(panact, BorderLayout.SOUTH);

		setModal(true);
		setTitle("BlockPGUI Launcher");
	}

	protected void initDatas()
	{
		if (m_envs == null)
			return;
		Map<String, String> envs = m_envs;
		m_txtjavahome.setText(ObjUtil.toString(envs.get("java.home"), ""));
		String rwstr = envs.get("recentworkspaces");
		if (rwstr != null && rwstr.length() > 0)
		{
			try
			{
				BPComboBox.BPComboBoxModel<String> model = new BPComboBox.BPComboBoxModel<String>();
				model.setDatas(TextUtil.splitTextToList(rwstr, ","));
				m_cmbhome.setModel(model);
			}
			catch (Exception e)
			{
				Std.err(e);
			}
		}
		m_cmbhome.setText(ObjUtil.toString(envs.get("workspace"), ""));

		m_chks = null;
		m_panright.removeAll();

		File extf = new File("exts");
		List<File> jarfs = new ArrayList<File>();
		if (extf.exists() && extf.isDirectory())
		{
			File[] fs = extf.listFiles();
			for (File f : fs)
			{
				if (f.exists() && f.isFile())
				{
					String fname = f.getName();
					if (fname.endsWith(".jar"))
					{
						jarfs.add(f);
					}
				}
			}
		}
		String extjarstr = envs.get("extensionjars");
		if (extjarstr == null)
			extjarstr = "";
		extjarstr = extjarstr.toLowerCase();
		List<String> extjars = TextUtil.splitTextToList(extjarstr, ",");
		List<BPCheckBox> chks = new ArrayList<BPCheckBox>();
		for (File f : jarfs)
		{
			String fname = f.getName();
			BPCheckBox chk = new BPCheckBox(fname);
			if (extjars.contains(fname.toLowerCase()))
				chk.setSelected(true);
			chks.add(chk);
			m_panright.add(chk);
		}
		m_chks = chks.toArray(new BPCheckBox[chks.size()]);
	}

	public void setupByEnvs(Map<String, String> envs)
	{
		if (envs == null)
			envs = new HashMap<String, String>();
		m_envs = envs;
		initDatas();
	}

	protected Map<String, String> assembleConfigs()
	{
		Map<String, String> envs = new HashMap<String, String>();
		envs.putAll(m_envs);
		envs.put("java.home", m_txtjavahome.getText().trim());
		envs.put("workspace", m_cmbhome.getText().trim());
		List<String> exts = new ArrayList<String>();
		for (BPCheckBox chk : m_chks)
		{
			if (chk.isSelected())
				exts.add(chk.getText());
		}
		envs.put("extensionjars", TextUtil.join(exts, ","));
		return envs;
	}

	protected boolean checkConfigs(Map<String, String> envs)
	{
		return true;
	}

	protected void onExtension(ActionEvent e)
	{
		m_scrollright.setPreferredSize(new Dimension(m_panright.getWidth(), 400));
		m_scrollright.setVisible(!m_scrollright.isVisible());
		pack();
	}

	protected void onOK(ActionEvent e)
	{
		Map<String, String> envs = assembleConfigs();
		if (checkConfigs(envs))
		{
			dispose();
			m_result = envs;
			dispose();
		}
	}

	public Map<String, String> getResult()
	{
		return m_result;
	}

	protected void onCancel(ActionEvent e)
	{
		dispose();
	}
}
