package bp.ui.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;

import bp.BPCore;
import bp.BPGUICore;
import bp.data.BPData;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.data.BPTreeData;
import bp.data.BPXYData;
import bp.event.BPEventCoreUI;
import bp.format.BPFormatTreeData;
import bp.format.BPFormatXYData;
import bp.project.BPResourceProjectMemory;
import bp.res.BPResource;
import bp.res.BPResourceHolder;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.util.JSONUtil;

public interface BPDataActionFactory
{
	public final static String ACTIONNAME_CLONEDATA = "CLONE";

	Action[] getAction(Object data, String actionname, Runnable loaddatafunc);

	public static class BPDataActionFactoryCommon implements BPDataActionFactory
	{
		public Action[] getAction(Object data, String actionname, Runnable loaddatafunc)
		{
			Action[] rc = null;
			if (data != null && actionname != null)
			{
				List<Action> acts = new ArrayList<>();
				if (data instanceof BPData && ACTIONNAME_CLONEDATA.equals(actionname))
				{
					BPData bd = (BPData) data;
					switch (bd.getDataStruture())
					{
						case XY:
						{
							Action actclonetemp = BPAction.build("Temp").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYData, loaddatafunc)).getAction();
							Action actclonewin = BPAction.build("New Editor").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYDataToNewEditor, loaddatafunc)).getAction();
							Action actclonejson = BPAction.build("JSON").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYDataToJSON, loaddatafunc)).getAction();
							acts.add(actclonetemp);
							acts.add(actclonewin);
							acts.add(actclonejson);
							break;
						}
						case T:
						{
							Action actclonetemp = BPAction.build("Temp").callback(new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeData, loaddatafunc)).getAction();
							Action actclonewin = BPAction.build("New Editor").callback(new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeDataToNewEditor, loaddatafunc)).getAction();
							Action actclonejson = BPAction.build("JSON").callback(new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeDataToJSON, loaddatafunc)).getAction();
							acts.add(actclonetemp);
							acts.add(actclonewin);
							acts.add(actclonejson);
							break;
						}
						default:
					}
				}
				rc = acts.toArray(new Action[acts.size()]);
			}
			return rc;
		}

		private final static void cloneXYData(BPXYData xydata, ActionEvent event)
		{
			BPResourceProjectMemory prj = (BPResourceProjectMemory) BPCore.getProjectsContext().getOrCreateTempProject();
			String id = BPCore.genID(null);
			BPResourceHolder holder = new BPResourceHolder(xydata.clone(), prj, BPXYData.EXT_XYDATA, BPXYData.EXT_XYDATA + ":temp" + id, "temp" + id, true);
			prj.addChild(holder);
			BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshProjectTree(null, prj));
			UIStd.info("Cloned to " + prj.toString() + "/temp" + id);
		}

		private final static void cloneTreeData(BPTreeData treedata, ActionEvent event)
		{
			BPResourceProjectMemory prj = (BPResourceProjectMemory) BPCore.getProjectsContext().getOrCreateTempProject();
			String id = BPCore.genID(null);
			BPResourceHolder holder = new BPResourceHolder(treedata.clone(), prj, BPTreeData.EXT_TREEDATA, BPTreeData.EXT_TREEDATA + ":temp" + id, "temp" + id, true);
			prj.addChild(holder);
			BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshProjectTree(null, prj));
			UIStd.info("Cloned to " + prj.toString() + "/temp" + id);
		}

		private final static void cloneXYDataToNewEditor(BPXYData xydata, ActionEvent event)
		{
			String id = BPCore.genID(null);
			BPResourceHolder holder = new BPResourceHolder(xydata.clone(), null, BPXYData.EXT_XYDATA, BPXYData.EXT_XYDATA + ":temp" + id, "temp" + id, true);
			BPGUICore.runOnCurrentFrame(f -> f.openResource(holder, new BPFormatXYData(), null, false, null));
		}

		private final static void cloneTreeDataToNewEditor(BPTreeData treedata, ActionEvent event)
		{
			String id = BPCore.genID(null);
			BPResourceHolder holder = new BPResourceHolder(treedata.clone(), null, BPTreeData.EXT_TREEDATA, BPTreeData.EXT_TREEDATA + ":temp" + id, "temp" + id, true);
			BPGUICore.runOnCurrentFrame(f -> f.openResource(holder, new BPFormatTreeData(), null, false, null));
		}

		private final static void cloneXYDataToJSON(BPXYData xydata, ActionEvent event)
		{
			BPResource file = CommonUIOperations.selectResource(null, true);
			if (file != null)
			{
				BPTextContainer con = new BPTextContainerBase();
				con.setEncoding("utf-8");
				con.open();
				try
				{
					con.bind(file);
					con.writeAllText(JSONUtil.encode(xydata.toMapList()));
				}
				finally
				{
					con.close();
				}
			}
		}
		
		private final static void cloneTreeDataToJSON(BPTreeData treedata, ActionEvent event)
		{
			BPResource file = CommonUIOperations.selectResource(null, true);
			if (file != null)
			{
				BPTextContainer con = new BPTextContainerBase();
				con.setEncoding("utf-8");
				con.open();
				try
				{
					con.bind(file);
					con.writeAllText(JSONUtil.encode(treedata.getRoot()));
				}
				finally
				{
					con.close();
				}
			}
		}
	}

	public static class DataActionProcessor<T> implements Consumer<ActionEvent>
	{
		protected BiConsumer<T, ActionEvent> m_callback;
		protected T m_data;
		protected WeakReference<Runnable> m_loaddatafunc;

		public DataActionProcessor(T data, BiConsumer<T, ActionEvent> callback, Runnable loaddatafunc)
		{
			m_data = data;
			m_callback = callback;
			m_loaddatafunc = new WeakReference<Runnable>(loaddatafunc);
		}

		public void accept(ActionEvent e)
		{
			Runnable loaddatafunc = m_loaddatafunc.get();
			if (loaddatafunc != null)
				loaddatafunc.run();
			m_callback.accept(m_data, e);
		}
	}
}
