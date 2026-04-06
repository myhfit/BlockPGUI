package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.Action;

import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPFileActionsInPop;
import bp.ui.util.CommonUIOperations;
import bp.util.ResourceUtil;

public class BPShortCutResource extends BPShortCutBase
{
	public final static String SCKEY_RES = "Resource";
	public final static String SC_KEY_RES = "res";

	protected final static int S_LIST_LIMIT = 100;

	public String getShortCutKey()
	{
		return SCKEY_RES;
	}

	public boolean run()
	{
		return false;
	}

	protected String[] getParamKeys()
	{
		return new String[] { SC_KEY_RES };
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		rc.addItem(BPSettingItem.create(SC_KEY_RES, "Resource", BPSettingItem.ITEM_TYPE_RESOURCE, null));
		rc.setAll(m_params);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		m_params = new LinkedHashMap<String, Object>();
		m_params.put("res", ResourceUtil.getResourceLink(setting.get("res")));
	}

	public BPResource getResource()
	{
		String res = (String) m_params.get("res");
		BPResource rc = ResourceUtil.getResourceByLink(res);
		return rc;
	}

	public Action[] expand()
	{
		BPResource res = getResource();
		return expandRes(res);
	}

	public Action[] expandRes(BPResource res)
	{
		if (res == null)
			return null;
		List<Action> rc = new ArrayList<Action>();
		Integer channelid = BPGUICore.execOnCurrentFrame(f -> f.getChannelID());
		if (channelid == null)
			channelid = BPGUICore.execOnMainFrame(f -> f.getChannelID());
		if (res.isProjectResource())
		{
			if (res instanceof BPResourceProject)
			{
				BPResourceProject prj = (BPResourceProject) res;
				List<BPResource> subs = prj.getProjectFunctionItems();
				int c = 0;
				if (subs != null && subs.size() > 0)
				{
					if (rc.size() > 0)
						rc.add(BPAction.separator());
					for (BPResource sub : subs)
					{
						rc.add(makeActionDirect(sub, channelid));
						c++;
						if (c >= S_LIST_LIMIT)
						{
							rc.add(BPAction.build("...").getAction());
							break;
						}
					}
				}
				assembleSubResources(res, rc, S_LIST_LIMIT - c);
			}
			else
			{

			}
		}
		else if (res.isFileSystem())
		{
			BPFileActionsInPop fas = new BPFileActionsInPop();
			rc.addAll(fas.getActions(res, channelid));
			assembleSubResources(res, rc, S_LIST_LIMIT);
		}
		return rc.toArray(new Action[rc.size()]);
	}

	protected void assembleSubResources(BPResource res, List<Action> acts, int limit)
	{
		BPResource[] subs = res.listResources();
		if (subs != null && subs.length > 0)
		{
			if (acts.size() > 0)
				acts.add(BPAction.separator());
			int c = 0;
			for (BPResource sub : subs)
			{
				if (!sub.isLeaf())
				{
					acts.add(makeAction(sub));
					c++;
					if (c >= limit)
					{
						acts.add(BPAction.build("...").getAction());
						break;
					}
				}
			}
			if (c < limit)
			{
				for (BPResource sub : subs)
				{
					if (sub.isLeaf())
					{
						acts.add(makeAction(sub));
						c++;
						if (c >= limit)
						{
							acts.add(BPAction.build("...").getAction());
							break;
						}
					}
				}
			}
		}
	}

	protected BPAction makeAction(BPResource res)
	{
		BPFormat format = BPFormatManager.getFormatByExt(res.getExt());
		String name = "[" + format.getName() + "]" + res.getName();
		BPAction s = BPAction.build(name).getAction();
		s.putValue(BPAction.SUB_ACTIONS_FUNC, (Supplier<Action[]>) (() -> this.expandRes(res)));
		s.putValue(BPAction.SUB_ACTIONS_FUNC_AUTOCLEAR, true);
		return s;
	}

	protected BPAction makeActionDirect(BPResource res, int channelid)
	{
		BPFormat format = BPFormatManager.getFormatByExt(res.getExt());
		String name = "[" + format.getName() + "]" + res.getName();
		BPAction s = BPAction.build(name).callback(e -> CommonUIOperations.openResource(res, null, null)).getAction();
		return s;
	}

	public boolean canExpand()
	{
		return true;
	}
}
