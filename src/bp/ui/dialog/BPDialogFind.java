package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPCommonDialogActions;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;

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
	protected WeakRefGo<Function<? super BPFindPs, Boolean>> m_findcb;

	public BPDialogFind(Window par)
	{
		super(par);
		m_findcb = new WeakRefGo<Function<? super BPFindPs, Boolean>>();
	}

	protected void initUIComponents()
	{
		m_mainp = new JPanel();
		JPanel line0 = new JPanel();
		JPanel line1 = new JPanel();
		JPanel line2 = new JPanel();
		BPLabel lblsrc = new BPLabel("Search :");
		BPLabel lbldest = new BPLabel("Replace:");
		m_txtsrc = new BPComboBox<String>();
		m_txtdest = new BPComboBox<String>();
		m_chkword = new BPCheckBox("Whole word");
		m_chkcase = new BPCheckBox("Case sensitive");
		m_chkbackward = new BPCheckBox("Backward");

		lblsrc.setMonoFont();
		lbldest.setMonoFont();
		m_chkword.setMonoFont();
		m_chkcase.setMonoFont();
		m_chkbackward.setMonoFont();
		m_txtsrc.setMonoFont();
		m_txtdest.setMonoFont();
		m_txtsrc.setEditable(true);
		m_txtdest.setEditable(true);

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
		line1.add(lbldest, BorderLayout.WEST);
		line1.add(m_txtdest, BorderLayout.CENTER);
		line2.add(m_chkword);
		line2.add(m_chkcase);
		line2.add(m_chkbackward);

		m_mainp.add(line0);
		m_mainp.add(line1);
		m_mainp.add(line2);

		getContentPane().add(m_mainp);

		BPCommonDialogActions dlgacts = new BPCommonDialogActions(this);
		dlgacts.actioncancel.putValue(Action.NAME, "Close");
		dlgacts.actioncancel.putValue(Action.MNEMONIC_KEY, null);
		Action actfind = BPAction.build("Find").callback(this::onFind).mnemonicKey(KeyEvent.VK_F).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK)).getAction();
		Action actreplace = BPAction.build("Replace").callback(this::onReplace).mnemonicKey(KeyEvent.VK_R).getAction();
		Action actreplaceall = BPAction.build("ReplaceAll").callback(this::onReplaceAll).mnemonicKey(KeyEvent.VK_A).getAction();
		setCommandBar(new Action[] { actfind, actreplace, actreplaceall, BPAction.separator(), BPAction.separator(), dlgacts.actioncancel });
		setTitle("Search/Replace");
	}

	protected void onSrcKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiersEx() == 0)
		{
			onFind(null);
		}
	}

	public void setFindCallBack(Function<? super BPFindPs, Boolean> cb)
	{
		m_findcb.setTarget(cb);
	}

	protected void onFind(ActionEvent e)
	{
		String src = m_txtsrc.getText();
		if (src.length() == 0)
			return;
		m_findcb.exec(cb -> cb.apply(getFindPs()));
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

	public void setFindText(String text)
	{
		m_txtsrc.setText(text);
	}

	protected void onReplace(ActionEvent e)
	{

	}

	protected void onReplaceAll(ActionEvent e)
	{

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
		public boolean onlyselection;
	}
}