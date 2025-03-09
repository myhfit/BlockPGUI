package bp.ui.form;

import static bp.util.LogicUtil.IFVU;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import bp.ui.dialog.BPDialogSelectData;
import bp.ui.dialog.BPDialogSelectFont;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPFormPanelUIConfigs extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8487716069295871763L;

	protected BPTextField m_txttabsize;
	protected BPTextField m_txtsplitsize;
	protected BPCheckBox m_chkvminfo;
	protected BPCheckBox m_chkdoublebuffer;
	protected BPCheckBox m_chksystray;
	protected BPCheckBox m_chkmin2tray;
	protected BPTextFieldPane m_panlafclsname;
	protected BPTextField m_txtlafclsname;
	protected BPTextFieldPane m_panmonofontname;
	protected BPTextFieldPane m_panlabelfontname;
	protected BPTextFieldPane m_panlistfontname;
	protected BPTextFieldPane m_pantreefontname;
	protected BPTextFieldPane m_pantablefontname;
	protected BPTextFieldPane m_panmenufontname;
	protected BPTextField m_txtmonofontname;
	protected BPTextField m_txtlabelfontname;
	protected BPTextField m_txtlistfontname;
	protected BPTextField m_txttreefontname;
	protected BPTextField m_txttablefontname;
	protected BPTextField m_txtmenufontname;
	protected BPTextField m_txtmonofontsizedelta;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		IFVU(ObjUtil.toInt(m_txttabsize.getNotEmptyText(), null), v -> rc.put("TAB_SIZE", v));
		IFVU(ObjUtil.toInt(m_txtsplitsize.getNotEmptyText(), null), v -> rc.put("DIVIDER_SIZE", v));
		rc.put("SHOW_VMINFO", m_chkvminfo.isSelected());
		rc.put("DOUBLE_BUFFER", m_chkdoublebuffer.isSelected());
		IFVU(m_txtlafclsname.getNotEmptyText(), v -> rc.put("LAF_CLASSNAME", v));
		IFVU(m_txtmonofontname.getNotEmptyText(), v -> rc.put("MONO_FONT_NAME", v));
		IFVU(m_txtlabelfontname.getNotEmptyText(), v -> rc.put("LABEL_FONT_NAME", v));
		IFVU(m_txtlistfontname.getNotEmptyText(), v -> rc.put("LIST_FONT_NAME", v));
		IFVU(m_txttreefontname.getNotEmptyText(), v -> rc.put("TREE_FONT_NAME", v));
		IFVU(m_txttablefontname.getNotEmptyText(), v -> rc.put("TABLE_FONT_NAME", v));
		IFVU(m_txtmenufontname.getNotEmptyText(), v -> rc.put("MENU_FONT_NAME", v));
		rc.put("MONO_FONT_SIZEDELTA", ObjUtil.toInt(m_txtmonofontsizedelta.getNotEmptyText(), null));
		rc.put("SYSTEM_TRAY", m_chksystray.isSelected());
		rc.put("MIN_TO_TRAY", m_chkmin2tray.isSelected());
		return rc;
	}

	protected void initForm()
	{
		m_txttabsize = makeSingleLineTextField();
		m_txtsplitsize = makeSingleLineTextField();

		m_chkvminfo = new BPCheckBox();
		m_chkdoublebuffer = new BPCheckBox();
		m_chksystray = new BPCheckBox();
		m_chkmin2tray = new BPCheckBox();
		m_chkvminfo.setLabelFont();
		m_chkdoublebuffer.setLabelFont();
		m_chksystray.setLabelFont();
		m_chkmin2tray.setLabelFont();

		m_panlafclsname = makeSingleLineTextFieldPanel(this::onLAFFind);
		m_txtlafclsname = m_panlafclsname.getTextComponent();

		m_panmonofontname = makeSingleLineTextFieldPanel(this::onFontMore);
		m_panlabelfontname = makeSingleLineTextFieldPanel(this::onFontMore);
		m_panlistfontname = makeSingleLineTextFieldPanel(this::onFontMore);
		m_pantreefontname = makeSingleLineTextFieldPanel(this::onFontMore);
		m_pantablefontname = makeSingleLineTextFieldPanel(this::onFontMore);
		m_panmenufontname = makeSingleLineTextFieldPanel(this::onFontMore);
		m_txtmonofontname = m_panmonofontname.getTextComponent();
		m_txtlabelfontname = m_panlabelfontname.getTextComponent();
		m_txtlistfontname = m_panlistfontname.getTextComponent();
		m_txttreefontname = m_pantreefontname.getTextComponent();
		m_txttablefontname = m_pantablefontname.getTextComponent();
		m_txtmenufontname = m_panmenufontname.getTextComponent();
		m_txtmonofontsizedelta = makeSingleLineTextField();

		addSeparator("UI Settings");
		addLine(new String[] { "Tab Size" }, new Component[] { m_txttabsize }, false, () -> m_txttabsize.isEmpty() || m_txttabsize.isInt());
		addLine(new String[] { "Divider Size" }, new Component[] { m_txtsplitsize }, false, () -> m_txtsplitsize.isEmpty() || m_txtsplitsize.isInt());
		addLine(new String[] { "LAF Class Name" }, new Component[] { m_panlafclsname });
		addLine(new String[] { "Double Buffer" }, new Component[] { wrapSingleLineComponent(m_chkdoublebuffer) });
		addLine(new String[] { "Show SysTray" }, new Component[] { wrapSingleLineComponent(m_chksystray) });
		addLine(new String[] { "Minimize to Tray" }, new Component[] { wrapSingleLineComponent(m_chkmin2tray) });
		addSeparator("Font Settings");
		addLine(new String[] { "Mono Font Name" }, new Component[] { m_panmonofontname });
		addLine(new String[] { "Label Font Name" }, new Component[] { m_panlabelfontname });
		addLine(new String[] { "List Font Name" }, new Component[] { m_panlistfontname });
		addLine(new String[] { "Tree Font Name" }, new Component[] { m_pantreefontname });
		addLine(new String[] { "Table Font Name" }, new Component[] { m_pantablefontname });
		addLine(new String[] { "Menu Font Name" }, new Component[] { m_panmenufontname });
		addLine(new String[] { "Mono Font Delta" }, new Component[] { m_txtmonofontsizedelta });
		addSeparator("Features");
		addLine(new String[] { "Show VM Info" }, new Component[] { wrapSingleLineComponent(m_chkvminfo) });
	}

	protected CompletionStage<List<String>> getLAFClasses()
	{
		return CompletableFuture.supplyAsync(() ->
		{
			List<String> classnames = ClassUtil.getClassNames("", true);
			List<String> laflist2 = new ArrayList<String>();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			for (String classname : classnames)
			{
				if (!laflist2.contains(classname))
				{
					if (ClassUtil.checkChildClass(LookAndFeel.class, classname, cl))
						laflist2.add(classname);
				}
			}
			return laflist2;
		});
	}

	protected String onLAFFind(String old)
	{
		List<String> laflist = new ArrayList<String>();
		LookAndFeelInfo[] lafinfos = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo lafinfo : lafinfos)
		{
			laflist.add(lafinfo.getClassName());
		}
		List<String> laflist2 = UIUtil.block(this::getLAFClasses, "Searching L&F Class...");
		if (laflist2 != null)
		{
			for (String laf : laflist2)
			{
				if (!laflist.contains(laf))
				{
					laflist.add(laf);
				}
			}
		}
		BPDialogSelectData<String> dlg = new BPDialogSelectData<>();
		dlg.setSource(laflist);
		dlg.setTitle("Select LookAndFeel Class Name");
		dlg.setVisible(true);
		return dlg.getSelectData();
	}

	protected String onFontMore(String old)
	{
		BPDialogSelectFont dlg = new BPDialogSelectFont();
		dlg.setVisible(true);
		return dlg.getSelectedFontName();
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		setComponentValue(m_txttabsize, data, "TAB_SIZE", editable);
		setComponentValue(m_txtsplitsize, data, "DIVIDER_SIZE", editable);
		setComponentValue(m_chkvminfo, data, "SHOW_VMINFO", editable);
		setComponentValue(m_chkdoublebuffer, data, "DOUBLE_BUFFER", editable);
		setComponentValue(m_txtlafclsname, data, "LAF_CLASSNAME", editable);
		setComponentValue(m_txtmonofontname, data, "MONO_FONT_NAME", editable);
		setComponentValue(m_txtlabelfontname, data, "LABEL_FONT_NAME", editable);
		setComponentValue(m_txtlistfontname, data, "LIST_FONT_NAME", editable);
		setComponentValue(m_txttreefontname, data, "TREE_FONT_NAME", editable);
		setComponentValue(m_txttablefontname, data, "TABLE_FONT_NAME", editable);
		setComponentValue(m_txtmenufontname, data, "MENU_FONT_NAME", editable);
		setComponentValue(m_txtmonofontsizedelta, data, "MONO_FONT_SIZEDELTA", editable);
		setComponentValue(m_chksystray, data, "SYSTEM_TRAY", editable);
		setComponentValue(m_chkmin2tray, data, "MIN_TO_TRAY", editable);
	}
}