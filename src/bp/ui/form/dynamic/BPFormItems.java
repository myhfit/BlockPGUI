package bp.ui.form.dynamic;

import bp.util.ClassUtil;

public final class BPFormItems
{
	public final static BPFormItem createItem(BPFormItemDef def, BPFormContext context)
	{
		String itemtype = def.itemtype;
		BPFormItemFactory fac = ClassUtil.findService(BPFormItemFactory.class, f -> f.canHandle(itemtype));
		BPFormItem rc = null;
		if (fac != null)
			rc = fac.create(itemtype, def).setup(def, context);
		if (rc == null)
			rc = (new BPFormItemVoid()).setup(def, context);
		return rc;
	}
}
