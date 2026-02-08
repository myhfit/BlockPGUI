package bp.ui.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;

import bp.BPCore;
import bp.BPGUICore;
import bp.data.BPData;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
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
import bp.util.BPPDUtil;
import bp.util.JSONUtil;
import bp.util.Std;

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
							Action actclonetemp = BPActionHelpers.getAction(BPActionConstCommon.TXT_TEMP, new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYData, loaddatafunc));
							Action actclonewin = BPActionHelpers.getAction(BPActionConstCommon.TXT_NEWEDITOR, new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYDataToNewEditor, loaddatafunc));
							Action actclonejson = BPAction.build("JSON").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYDataToJSON, loaddatafunc)).getAction();
							Action actclonebppd = BPAction.build("BPPD").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYDataToBPPD, loaddatafunc)).getAction();
							acts.add(actclonetemp);
							acts.add(actclonewin);
							acts.add(actclonejson);
							acts.add(actclonebppd);
							break;
						}
						case T:
						{
							Action actclonetemp = BPActionHelpers.getAction(BPActionConstCommon.TXT_TEMP, new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeData, loaddatafunc));
							Action actclonewin = BPActionHelpers.getAction(BPActionConstCommon.TXT_NEWEDITOR, new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeDataToNewEditor, loaddatafunc));
							Action actclonejson = BPAction.build("JSON").callback(new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeDataToJSON, loaddatafunc)).getAction();
							Action actclonebppd = BPAction.build("BPPD").callback(new DataActionProcessor<BPTreeData>((BPTreeData) data, BPDataActionFactoryCommon::cloneTreeDataToBPPD, loaddatafunc)).getAction();
							acts.add(actclonetemp);
							acts.add(actclonewin);
							acts.add(actclonejson);
							acts.add(actclonebppd);
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
			BPResource file = CommonUIOperations.selectResource(null, true, new String[] { "json" });
			if (file != null)
			{
				BPTextContainer con = new BPTextContainerBase();
				con.setEncoding("utf-8");
				con.open();
				try
				{
					con.bind(file);
					con.writeAllText(JSONUtil.encode(xydata.toMapList()));
					UIStd.info("Cloned to " + file.toString() + " finished");
				}
				finally
				{
					con.close();
				}
			}
		}

		private final static void cloneXYDataToBPPD(BPXYData xydata, ActionEvent event)
		{
			BPResource file = CommonUIOperations.selectResource(null, true, new String[] { "bppd" });
			if (file != null)
			{
				BPDataContainer con = new BPDataContainerBase();
				con.open();
				try
				{
					con.bind(file);
					con.useOutputStream(out ->
					{
						try (BufferedOutputStream bos = new BufferedOutputStream(out))
						{
							BPPDUtil.write(bos, xydata.toMapList());
							UIStd.info("Cloned to " + file.toString() + " finished");
						}
						catch (IOException e)
						{
							Std.err(e);
						}
						return true;
					});
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
					UIStd.info("Cloned to " + file.toString() + " finished");
				}
				finally
				{
					con.close();
				}
			}
		}

		private final static void cloneTreeDataToBPPD(BPTreeData treedata, ActionEvent event)
		{
			BPResource file = CommonUIOperations.selectResource(null, true, new String[] { "bppd" });
			if (file != null)
			{
				BPDataContainer con = new BPDataContainerBase();
				con.open();
				try
				{
					con.bind(file);
					con.useOutputStream(out ->
					{
						try (BufferedOutputStream bos = new BufferedOutputStream(out))
						{
							BPPDUtil.write(bos, treedata.getRoot());
							UIStd.info("Cloned to " + file.toString() + " finished");
						}
						catch (IOException e)
						{
							Std.err(e);
						}
						return true;
					});
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
