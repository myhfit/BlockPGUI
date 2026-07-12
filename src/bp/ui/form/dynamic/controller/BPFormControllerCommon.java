package bp.ui.form.dynamic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.dialog.BPDialogSelectResourceList;
import bp.ui.dialog.BPDialogSelectResource.SELECTSCOPE;
import bp.ui.dialog.BPDialogSelectResource.SELECTTYPE;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.util.CommonUIOperations;
import bp.util.CompareUtil;
import bp.util.LogicUtil;
import bp.util.ObjUtil;

public class BPFormControllerCommon
{
	protected String onSelectResourceFileSystem(String old)
	{
		String rc = null;
		BPResource res = CommonUIOperations.showSelectResource(null, cb -> cb.switchPathTreeFunc(1));
		if (res != null)
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		return rc;
	}

	protected String onSelectResourceFile(String old)
	{
		String rc = null;
		BPResource res = CommonUIOperations.showSelectResource(null, cb ->
		{
			cb.setSelectType(SELECTTYPE.FILE);
			cb.switchPathTreeFunc(1);
		});
		if (res != null)
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		return rc;
	}

	protected String onSelectResourceDir(String old, boolean noprj)
	{
		String rc = null;
		BPResource res = CommonUIOperations.showSelectResource(null, cb ->
		{
			cb.setSelectType(SELECTTYPE.DIR);
			if (noprj)
				cb.setScopes(SELECTSCOPE.WORKSPACE, SELECTSCOPE.COMPUTER);
			cb.switchPathTreeFunc(1);
		});
		if (res != null)
			rc = BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName());
		return rc;
	}

	protected String onSelectResourceDirList(String oldpath)
	{
		String rc = null;
		List<BPResource> oldress = new ArrayList<BPResource>();
		if (oldpath.trim().length() > 0)
		{
			String[] ops = oldpath.split(";");
			for (String op : ops)
				LogicUtil.IFVU(BPCore.getFileContext().getRes(op), res -> oldress.add(res));
		}
		BPDialogSelectResourceList dlg = new BPDialogSelectResourceList();
		dlg.setResourceList(oldress);
		dlg.switchPathTreeFuncs();
		dlg.setVisible(true);
		List<BPResource> rs = dlg.getResult();
		if (rs != null)
			rc = ObjUtil.joinDatas(rs, ";", res -> BPCore.getFileContext().comparePath(((BPResourceFileSystem) res).getFileFullName()), false);
		return rc;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getItemValue(String key, BPFormContext context)
	{
		return (T) context.findItem("name").getComponentValue();
	}

	protected String[] sortKeys(Map<String, ?> data)
	{
		List<String> keys = new ArrayList<String>(data.keySet());
		keys.sort(CompareUtil.COMPARATOR_TXT_NOCASE());
		return keys.toArray(new String[keys.size()]);
	}
}