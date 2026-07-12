package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.actions.BPCommonDialogActions;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGoFunction;

public class BPDialogFind extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8680688151938727056L;

	protected JPanel m_mainp;
	protected BPComboBox<String> m_txtsrc;
	protected BPComboBox<String> m_txtdest;
	protected BPCheckBox m_chkword;
	protected BPCheckBox m_chkcase;
	protected BPCheckBox m_chkbackward;
	protected BPLabel m_lbldest;
	protected WeakRefGoFunction<? super BPFindPs, Boolean> m_findcb;

	protected BPAction m_actreplace;
	protected BPAction m_actreplaceall;

	public BPDialogFind(Window par)
	{
		super(par);
		m_findcb = new WeakRefGoFunction<>();
	}

	public BPDialogFind(Component comp)
	{
		this(SwingUtilities.getWindowAncestor(comp));
		m_findcb = new WeakRefGoFunction<>();
	}

	protected void initUIComponents()
	{
		m_mainp = new JPanel();
		JPanel line0 = new JPanel();
		JPanel line1 = new JPanel();
		JPanel line2 = new JPanel();
		BPLabel lblsrc = new BPLabel(BPActionConstCommon.FDLG_FIND.text());
		m_lbldest = new BPLabel(BPActionConstCommon.FDLG_REPLACE.text());
		m_txtsrc = new BPComboBox<String>();
		m_txtdest = new BPComboBox<String>();
		m_chkword = new BPCheckBox(BPActionConstCommon.FDLG_WHOLEWORD.text());
		m_chkcase = new BPCheckBox(BPActionConstCommon.FDLG_CASESENSITIVE.text());
		m_chkbackward = new BPCheckBox(BPActionConstCommon.FDLG_BACKWARD.text());

		lblsrc.setMonoFont();
		m_lbldest.setMonoFont();
		m_chkword.setMonoFont();
		m_chkcase.setMonoFont();
		m_chkbackward.setMonoFont();
		m_txtsrc.setMonoFont();
		m_txtdest.setMonoFont();
		m_txtsrc.setEditable(true);
		m_txtdest.setEditable(true);

		lblsrc.setPreferredSize(new Dimension(60, 0));
		m_lbldest.setPreferredSize(new Dimension(60, 0));

		ComboBoxEditor c = m_txtsrc.getEditor();
		JTextField srctxt = (JTextField) c.getEditorComponent();
		srctxt.addKeyListener(new UIUtil.BPKeyListener(null, this::onSrcKeyDown, null));

		m_chkword.setMnemonic('W');
		m_chkcase.setMnemonic('C');
		m_chkbackward.setMnemonic('B');

		Dimension d = UIUtil.scaleUIDimension(new Dimension(300, UIConfigs.TEXTFIELD_HEIGHT() + 4));
		line0.setPreferredSize(d);
		line1.setPreferredSize(d);

		line0.setBorder(new EmptyBorder(2, 2, 2, 2));
		line1.setBorder(new EmptyBorder(0, 2, 2, 2));
		line2.setBorder(new EmptyBorder(0, 2, 2, 2));

		line0.setLayout(new BorderLayout());
		line1.setLayout(new BorderLayout());
		line2.setLayout(new FlowLayout());
		m_mainp.setLayout(new BoxLayout(m_mainp, BoxLayout.Y_AXIS));

		line0.add(lblsrc, BorderLayout.WEST);
		line0.add(m_txtsrc, BorderLayout.CENTER);
		line1.add(m_lbldest, BorderLayout.WEST);
		line1.add(m_txtdest, BorderLayout.CENTER);
		line2.add(m_chkword);
		line2.add(m_chkcase);
		line2.add(m_chkbackward);

		m_mainp.add(line0);
		m_mainp.add(line1);
		m_mainp.add(line2);

		getContentPane().add(m_mainp);

		BPCommonDialogActions dlgacts = new BPCommonDialogActions(this);
		dlgacts.actioncancel.putValue(Action.NAME, BPActionConstCommon.ACT_BTNCLOSE.text());
		dlgacts.actioncancel.putValue(Action.MNEMONIC_KEY, null);
		Action actfind = BPActionHelpers.getAction(BPActionConstCommon.FDLG_FIND, this::onFind);
		m_actreplace = BPActionHelpers.getAction(BPActionConstCommon.FDLG_REPLACE, this::onReplace);
		m_actreplaceall = BPActionHelpers.getAction(BPActionConstCommon.FDLG_REPLACEALL, this::onReplaceAll);
		setCommandBar(new Action[] { actfind, m_actreplace, m_actreplaceall, BPAction.separator(), BPAction.separator(), dlgacts.actioncancel });
		setTitle(BPActionConstCommon.FDLG_FIND.text() + "/" + BPActionConstCommon.FDLG_REPLACE.text());
	}

	protected void onSrcKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiersEx() == 0)
		{
			onFind(null);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setFindCallBack(Function<? super BPFindPs, Boolean> cb)
	{
		((WeakRefGoFunction)m_findcb).setTarget(cb);
	}
	
	public void setReplaceable(boolean flag)
	{
		m_lbldest.setEnabled(flag);
		m_txtdest.setEnabled(flag);
		m_actreplace.setEnabled(flag);
		m_actreplaceall.setEnabled(flag);
	}

	protected void onFind(ActionEvent e)
	{
		String src = m_txtsrc.getText();
		if (src.length() == 0)
			return;
		m_findcb.apply(getFindPs());
	}

	protected BPFindPs getFindPs()
	{
		BPFindPs rc = new BPFindPs();
		rc.src = m_txtsrc.getText();
		rc.isforward = !m_chkbackward.isSelected();
		rc.iswholeword = m_chkword.isSelected();
		rc.iscasesensitive = m_chkcase.isSelected();
		rc.onlyselection = false;
		return rc;
	}

	protected BPReplacePs getReplacePs()
	{
		BPReplacePs rc = new BPReplacePs();
		rc.src = m_txtsrc.getText();
		rc.replacestr = m_txtdest.getText();
		rc.isforward = !m_chkbackward.isSelected();
		rc.iswholeword = m_chkword.isSelected();
		rc.iscasesensitive = m_chkcase.isSelected();
		rc.onlyselection = false;
		return rc;
	}

	public void setFindText(String text)
	{
		m_txtsrc.setText(text);
	}

	protected void onReplace(ActionEvent e)
	{
		String src = m_txtsrc.getText();
		if (src.length() == 0)
			return;
		BPReplacePs r = getReplacePs();
		r.isreplaceall = false;
		m_findcb.apply(r);
	}

	protected void onReplaceAll(ActionEvent e)
	{
		String src = m_txtsrc.getText();
		if (src.length() == 0)
			return;
		BPReplacePs r = getReplacePs();
		r.isreplaceall = true;
		m_findcb.apply(r);
	}

	protected void setPrefers()
	{
		super.setPrefers();
	}

	protected void initDatas()
	{
	}

	public boolean doCallCommonAction(int command)
	{
		return false;
	}

	public static class BPFindPs
	{
		public String src;
		public boolean isforward;
		public boolean iswholeword;
		public boolean iscasesensitive;
		public boolean isregex;
		public boolean onlyselection;

		public boolean isReplace()
		{
			return false;
		}
	}

	public static class BPReplacePs extends BPFindPs
	{
		public boolean isReplace()
		{
			return true;
		}

		public String replacestr;
		public boolean isreplaceall;
	}
}