package bp.ui.form;

import bp.ui.form.dynamic.BPFormContext;
import bp.util.ClassUtil;

public class BPFormPanelDynamicByConfig extends BPFormPanelDynamic
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7684228991428466584L;

	protected String m_leafclassname;
	protected String m_rootclassname;

	protected BPFormContext initFormContext()
	{
		BPFormContext context = null;
		if (m_leafclassname != null)
		{
			context = new BPFormContext();
			context.gridweakborder = m_gridweakborder;
			context.lineheight = m_lineheight;
			Class<?> rootcls = m_rootclassname == null ? Object.class : ClassUtil.getEClass(m_rootclassname);
			Class<?> leafcls = ClassUtil.getEClass(m_leafclassname);
			if (leafcls != null && rootcls != null)
			{
				context.loadConfig("bp/ui/form/config/", leafcls, rootcls);
				m_formcontext = context;
			}
		}
		return context;
	}

	public void initByKey(String leafclassname, String rootclassname)
	{
		m_leafclassname = leafclassname;
		m_rootclassname = rootclassname;
		laterInit();
	}
}
