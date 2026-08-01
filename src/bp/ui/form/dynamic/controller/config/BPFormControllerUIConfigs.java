package bp.ui.form.dynamic.controller.config;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import bp.locale.BPLocaleConstCC;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.dialog.BPDialogSelectFont;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.ui.form.dynamic.controller.BPFormControllerCommon;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;

public class BPFormControllerUIConfigs extends BPFormControllerCommon implements BPFormController
{
	public Object select(String key, Object oldvalue, BPFormContext context)
	{
		switch (key)
		{
			case "LAF_CLASSNAME":
				return onLAFFind((String) oldvalue);
			case "MONO_FONT_NAME":
			case "LABEL_FONT_NAME":
			case "LIST_FONT_NAME":
			case "TREE_FONT_NAME":
			case "TABLE_FONT_NAME":
			case "MENU_FONT_NAME":
				return onFontMore((String) oldvalue);
		}
		return null;
	}

	protected String onFontMore(String old)
	{
		BPDialogSelectFont dlg = new BPDialogSelectFont();
		dlg.setVisible(true);
		return dlg.getSelectedFontName();
	}

	protected String onLAFFind(String old)
	{
		List<String> laflist = new ArrayList<String>();
		LookAndFeelInfo[] lafinfos = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo lafinfo : lafinfos)
		{
			laflist.add(lafinfo.getClassName());
		}
		List<String> laflist2 = UIUtil.block(this::getLAFClasses, BPActionConstCommon.TXT_SEARCHING.text());
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
		return UIStd.select(laflist, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_LAF, BPLocaleConstCC.CLASSNAME), null, (old != null ? laflist.indexOf(old) : -1));
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
				String cn = classname.toLowerCase();
				if (!laflist2.contains(classname) && (cn.contains("lookandfeel") || cn.contains("laf")))
				{
					if (ClassUtil.checkChildClass(LookAndFeel.class, classname, cl, c -> !Modifier.isAbstract(c.getModifiers())))
						laflist2.add(classname);
				}
			}
			return laflist2;
		});
	}
}
