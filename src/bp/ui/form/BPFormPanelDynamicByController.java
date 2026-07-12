package bp.ui.form;

import bp.ui.form.dynamic.BPFormContext;
import bp.util.ObjUtil;

public class BPFormPanelDynamicByController extends BPFormPanelDynamic
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4302510285163639987L;

	protected String m_leafclassname;
	protected String m_rootclassname;

	protected BPFormContext initFormContext()
	{
		BPFormContext context = new BPFormContext();
		String cls = m_leafclassname;
		if (cls.startsWith("bp."))
			cls = cls.substring(3);
		int vi = cls.lastIndexOf(".");
		String packname = cls.substring(0, vi);
		String ccname = cls.substring(vi + 1);
		if (ccname.startsWith("BP"))
			ccname = ccname.substring(2);
		cls = packname + ".BPFormController" + ccname;
		context.setMappedData(ObjUtil.makeMap("controller", cls));
		context.fullctrl = true;
		m_formcontext = context;
		return context;
	}

	public void initByKey(String leafclassname, String rootclassname)
	{
		m_leafclassname = leafclassname;
		m_rootclassname = rootclassname;
		laterInit();
	}
}
