package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.UIConfigs;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIUtil;

public class BPDialogSelectFont extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3556973484245269229L;

	protected BPList<Font> m_lstfonts;

	protected BPTextField m_txttest;
	protected BPTextField m_txttestsize;
	protected JLabel m_lbltest;

	protected String m_fontname;

	protected void initUIComponents()
	{
		m_lstfonts = new BPList<Font>();
		m_lstfonts.setModel(new BPList.BPListModel<Font>());
		m_lstfonts.setCellRenderer(new BPList.BPListRenderer(BPDialogSelectFont::transFontName));
		m_lstfonts.setListFont();
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(m_lstfonts);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel leftpan = new JPanel();
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(scroll, BorderLayout.CENTER);
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(220, 0)));

		m_txttest = new BPTextField();
		m_txttestsize = new BPTextField();
		m_txttest.setText("Test");
		m_txttest.setMonoFont();
		m_txttestsize.setText(UIConfigs.EDITORFONT_SIZE() + "");
		m_txttestsize.setMonoFont();
		m_txttest.setBorder(new MatteBorder(0, 1, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		m_txttestsize.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		m_txttest.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onChange));
		m_txttestsize.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onChange));

		JPanel rightpan = new JPanel();
		JPanel bottompan = new JPanel();
		rightpan.setLayout(new BorderLayout());
		rightpan.setBorder(null);
		rightpan.setBackground(UIConfigs.COLOR_TEXTBG());
		BoxLayout box = new BoxLayout(bottompan, BoxLayout.X_AXIS);
		bottompan.setLayout(box);
		bottompan.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_STRONGBORDER()));

		m_lbltest = new JLabel("Test");
		m_lbltest.setHorizontalAlignment(SwingConstants.CENTER);
		m_lbltest.setVerticalAlignment(SwingConstants.CENTER);
		JLabel lbltest = new JLabel(" Text:");
		JLabel lbltestsize = new JLabel(" Size:");
		lbltestsize.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_STRONGBORDER()));
		lbltest.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));
		lbltestsize.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));

		bottompan.add(lbltest);
		bottompan.add(Box.createRigidArea(new Dimension(2, 2)));
		bottompan.add(m_txttest);
		bottompan.add(Box.createRigidArea(new Dimension(2, 2)));
		bottompan.add(lbltestsize);
		bottompan.add(m_txttestsize);
		rightpan.add(m_lbltest, BorderLayout.CENTER);
		rightpan.add(bottompan, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(leftpan, BorderLayout.WEST);
		add(rightpan, BorderLayout.CENTER);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle("Select Font");
		setModal(true);
	}

	protected final static String transFontName(Object font)
	{
		return ((Font) font).getName();
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		Font[] fs = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		List<Font> list = new ArrayList<Font>();
		for (Font f : fs)
		{
			if (f.getStyle() == Font.PLAIN && f.getName().equals(f.getFamily()))
			{
				list.add(f);
			}
		}
		((BPList.BPListModel<Font>) m_lstfonts.getModel()).setDatas(list);
		m_lstfonts.addListSelectionListener(this::onListSelectionChange);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			onChange(null);
		}
	}

	protected void onChange(Object o)
	{
		try
		{
			int size = Integer.parseInt(m_txttestsize.getText());
			String teststr = m_txttest.getText();
			Font f = m_lstfonts.getSelectedValue();
			if (f != null)
			{
				m_lbltest.setText(teststr);
				m_lbltest.setFont(new Font(f.getName(), Font.PLAIN, size));
			}
		}
		catch (NumberFormatException e)
		{
		}
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			Font f = m_lstfonts.getSelectedValue();
			if (f != null)
			{
				m_fontname = f.getFamily();
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	public String getSelectedFontName()
	{
		return m_fontname;
	}
}