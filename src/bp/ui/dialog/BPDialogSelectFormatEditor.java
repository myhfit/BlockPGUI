package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.UIConfigs;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.format.BPFormatUnknown;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.scomp.BPPopupComboList;
import bp.ui.scomp.BPPopupComboList.BPPopupComboController;
import bp.ui.scomp.BPTableSetting;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIUtil;

public class BPDialogSelectFormatEditor extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 86071752814453127L;

	protected BPFormat m_format;
	protected BPEditorFactory m_editorfac;
	protected BPList<BPFormat> m_lstformat;
	protected BPList<BPEditorFactory> m_lsteditorfac;
	protected BPSetting m_setting;
	protected BPTableSetting m_tbsetting;
	protected JScrollPane m_rbpan;
	protected JPanel m_rpan;
	protected BPTextField m_txtfilter;
	protected BPPopupComboList m_popupfilter;
	protected BPPopupComboController m_popupc;
	protected boolean m_iscreate;
	protected List<BPEditorFactory> m_allfacs;

	public boolean doCallCommonAction(int command)
	{
		switch (command)
		{
			case COMMAND_OK:
			{
				m_format = m_lstformat.getSelectedValue();
				m_editorfac = m_lsteditorfac.getSelectedValue();
				break;
			}
			case COMMAND_CANCEL:
			{
				m_format = null;
				m_editorfac = null;
				break;
			}
		}
		return false;
	}

	protected void initUIComponents()
	{
		setLayout(new BorderLayout());
		m_popupc = new BPPopupComboController(this::listEditors, this::getEditorText, (Consumer<BPEditorFactory>) this::submitEditor);
		JPanel pmain = new JPanel();
		JScrollPane scroll = new JScrollPane();
		JScrollPane scroll2 = new JScrollPane();
		m_txtfilter = new BPTextField();
		m_lstformat = new BPList<BPFormat>();
		m_lsteditorfac = new BPList<BPEditorFactory>();
		m_tbsetting = new BPTableSetting();
		JPanel lpan = new JPanel();
		m_rpan = new JPanel();
		BPLabel lbl = new BPLabel(" Formats");
		BPLabel lbl2 = new BPLabel(" Editors");
		m_rbpan = new JScrollPane();
		m_rbpan.setVisible(false);
		m_rbpan.setViewportView(m_tbsetting);

		m_tbsetting.setTableFont();
		m_txtfilter.setMonoFont();
		lbl.setLabelFont();
		lbl2.setLabelFont();
		m_lstformat.setListFont();
		m_lsteditorfac.setListFont();
		m_lstformat.setCellRenderer(new BPList.BPListRenderer((format) -> ((BPFormat) format).getName()));
		m_lsteditorfac.setCellRenderer(new BPList.BPListRenderer((fac) -> ((BPEditorFactory) fac).getName()));

		m_popupfilter = new BPPopupComboList();
		m_popupfilter.bind(m_txtfilter, m_popupc);

		m_lstformat.addMouseListener(new UIUtil.BPMouseListener(this::onFormatClick, null, null, null, null));
		m_lsteditorfac.addMouseListener(new UIUtil.BPMouseListener(this::onEditorClick, null, null, null, null));
		m_lstformat.addListSelectionListener(this::onFormatChange);
		m_lsteditorfac.addListSelectionListener(this::onEditorChange);

		m_txtfilter.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_TEXTQUARTER()));
		scroll.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		scroll2.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_rpan.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_STRONGBORDER()));
		m_rbpan.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_STRONGBORDER()));

		scroll.setViewportView(m_lstformat);
		scroll2.setViewportView(m_lsteditorfac);
		lpan.setLayout(new BorderLayout());
		m_rpan.setLayout(new BorderLayout());
		pmain.setLayout(new GridLayout(1, 2, 0, 0));

		lpan.add(scroll, BorderLayout.CENTER);
		m_rpan.add(scroll2, BorderLayout.CENTER);
		lpan.add(lbl, BorderLayout.NORTH);
		m_rpan.add(lbl2, BorderLayout.NORTH);
		pmain.add(lpan);
		pmain.add(m_rpan);

		add(m_txtfilter, BorderLayout.NORTH);
		add(pmain, BorderLayout.CENTER);
		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle("Select Format And Editor");
		setModal(true);
	}

	protected String getEditorText(Object fac)
	{
		return ((BPEditorFactory) fac).getName();
	}

	public BPFormat getSelectedFormat()
	{
		return m_format;
	}

	public BPEditorFactory getSelectedEditorFactory()
	{
		return m_editorfac;
	}

	protected List<?> listEditors(String txt)
	{
		List<Object> rc = new ArrayList<>();
		for (BPEditorFactory fac : m_allfacs)
		{
			String fname = fac.getName();
			if (txt == null || txt.length() == 0 || fname.toLowerCase().contains(txt.toLowerCase()))
				rc.add(fac);
		}
		return rc;
	}

	protected void submitEditor(BPEditorFactory fac)
	{
		m_editorfac = fac;
		String[] fs = fac.getFormats();
		if (fs != null && fs.length > 0)
			m_format = BPFormatManager.getFormatByName(fs[0]);
		else
			m_format = null;
		m_setting = null;
		m_actionresult = COMMAND_OK;
		close();
	}

	protected void onFormatClick(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
		{
			callCommonAction(COMMAND_OK);
		}
	}

	protected void onEditorClick(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
		{
			callCommonAction(COMMAND_OK);
		}
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		m_allfacs = BPEditorManager.getAllFactories();
		BPList.BPListModel<BPFormat> modelf = new BPList.BPListModel<BPFormat>();
		BPList.BPListModel<BPEditorFactory> modele = new BPList.BPListModel<BPEditorFactory>();
		List<BPFormat> formats = new ArrayList<BPFormat>();
		Map<String, Object> formatmap = new BPFormatManager().getMappedData();
		for (Entry<String, Object> formatkv : formatmap.entrySet())
		{
			BPFormat format = (BPFormat) formatkv.getValue();
			if (!formats.contains(format))
				formats.add(format);
		}
		formats.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		formats.add(new BPFormatUnknown());
		modelf.setDatas(formats);
		m_lstformat.setModel(modelf);
		m_lsteditorfac.setModel(modele);
	}

	protected void onFormatChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			m_setting = null;

			BPFormat format = m_lstformat.getSelectedValue();
			List<BPEditorFactory> facs = BPEditorManager.getFactories(format.getName());
			List<BPEditorFactory> cfacs = new ArrayList<BPEditorFactory>();
			if (facs != null)
			{
				for (BPEditorFactory fac : facs)
				{
					if (!m_iscreate || fac.showInCreate())
						cfacs.add(fac);
				}
			}
			((BPList.BPListModel<BPEditorFactory>) m_lsteditorfac.getModel()).setDatas(cfacs);
			m_lsteditorfac.updateUI();
		}
	}

	public void setCreateMode(boolean flag)
	{
		m_iscreate = flag;
	}

	protected void onEditorChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			m_setting = null;

			BPFormat format = m_lstformat.getSelectedValue();
			BPEditorFactory fac = m_lsteditorfac.getSelectedValue();
			if (format != null && fac != null)
			{
				m_setting = fac.getSetting(format.getName());
				m_tbsetting.setSetting(m_setting);
				m_tbsetting.setPreferredScrollableViewportSize(new Dimension(200, 200));
			}
			boolean issetting = (m_setting != null);
			if (m_rbpan.isVisible() != issetting)
			{
				if (issetting)
				{
					m_rpan.add(m_rbpan, BorderLayout.SOUTH);
				}
				else
				{
					m_rpan.remove(m_rbpan);
				}
				m_rbpan.setVisible(issetting);
				m_rpan.updateUI();
			}
		}
	}

	public void setFormat(BPFormat format)
	{
		if (format == null)
			return;
		BPList.BPListModel<BPFormat> modelf = (BPListModel<BPFormat>) m_lstformat.getModel();
		List<BPFormat> fs = modelf.getDatas();
		int seli = -1;
		String fname = format.getName();
		for (int i = 0; i < fs.size(); i++)
		{
			if (fs.get(i).getName().equals(fname))
			{
				seli = i;
				break;
			}
		}
		m_lstformat.setSelectedIndex(seli);
	}

	public BPConfig getEditorOptions()
	{
		return m_setting;
	}
}
