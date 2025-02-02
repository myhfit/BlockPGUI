package bp.ui.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;

import bp.BPCore;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.data.BPXYData;
import bp.event.BPEventCoreUI;
import bp.project.BPResourceProjectMemory;
import bp.res.BPResourceFile;
import bp.res.BPResourceHolder;
import bp.ui.dialog.BPDialogSelectFile;
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
				if (data instanceof BPXYData && ACTIONNAME_CLONEDATA.equals(actionname))
				{
					Action actclonetemp = BPAction.build("Temp").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYData, loaddatafunc)).getAction();
					Action actclonejson = BPAction.build("JSON").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryCommon::cloneXYDataToJSON, loaddatafunc)).getAction();
					rc = new Action[] { actclonetemp, actclonejson };
				}
			}
			return rc;
		}

		private final static void cloneXYData(BPXYData xydata, ActionEvent event)
		{
			BPResourceProjectMemory prj = (BPResourceProjectMemory) BPCore.getProjectsContext().getOrCreateTempProject();
			String id = BPCore.genID(null);
			BPResourceHolder holder = new BPResourceHolder(xydata, prj, BPXYData.EXT_XYDATA, BPXYData.EXT_XYDATA + ":temp" + id, "temp" + id, true);
			prj.addChild(holder);
			BPCore.EVENTS_CORE.trigger(BPCore.getCoreUIChannelID(), BPEventCoreUI.refreshProjectTree(null, prj));
			UIStd.info("Cloned to " + prj.toString() + "/temp" + id);
		}

		private final static void cloneXYDataToJSON(BPXYData xydata, ActionEvent event)
		{
			BPDialogSelectFile dlg = new BPDialogSelectFile();
			dlg.setVisible(true);
			BPResourceFile file = dlg.getSelectedFile();
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
