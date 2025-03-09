package bp.ui.editor;

import java.util.List;
import java.util.Map;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.console.BPConsoleCLI;
import bp.data.BPDataContainerRandomAccess;
import bp.data.BPDataContainerRandomAccessBase;
import bp.data.BPDataPipes;
import bp.data.BPDiagram;
import bp.data.BPJSONContainerBase;
import bp.data.BPMHolder;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.data.BPXYContainer;
import bp.data.BPXYData;
import bp.data.BPXYData.BPXYDataList;
import bp.data.BPXYHolder;
import bp.format.BPFormat;
import bp.format.BPFormatCSV;
import bp.format.BPFormatJSON;
import bp.format.BPFormatTSV;
import bp.format.BPFormatText;
import bp.format.BPFormatUnknown;
import bp.format.BPFormatXYData;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceFileSystemLocal;
import bp.res.BPResourceHolder;
import bp.res.BPResourceIO;
import bp.ui.console.BPConsolePanel;
import bp.ui.scomp.BPConsolePane;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.LogicUtil;
import bp.util.TextUtil;

public interface BPEditorFactory
{
	String[] getFormats();

	BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params);

	void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options);

	String getName();

	default BPSetting getSetting(String formatkey)
	{
		return null;
	}

	default boolean checkSameTab()
	{
		return true;
	}

	default boolean handleFormat(String formatkey)
	{
		return true;
	}

	default String getExtPrefix(String ext)
	{
		return null;
	}

	default boolean showInCreate()
	{
		return true;
	}

	public final static class BPEditorFactoryXYData implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatXYData.FORMAT_XYDATA };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPXYDEditor<>();
		}

		public boolean handleFormat(String formatkey)
		{
			return BPFormatXYData.FORMAT_XYDATA.equals(formatkey);
		}

		@SuppressWarnings("unchecked")
		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res != null)
			{
				BPXYContainer con = null;
				if (res instanceof BPResourceHolder)
				{
					BPXYData data = ((BPResourceHolder) res).getData();
					BPXYHolder holder = new BPXYHolder();
					holder.setData(data);
					con = holder;
				}
				((BPXYDEditor<BPXYContainer>) editor).bind(con);
			}
		}

		public String getName()
		{
			return "XY Editor";
		}
	}

	public final static class BPEditorFactoryJSONToXYData implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatJSON.FORMAT_JSON };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPXYDEditor<>();
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}

		@SuppressWarnings("unchecked")
		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res != null)
			{
				BPXYContainer con = null;
				// if (res instanceof BPResourceHolder)
				// {
				// List<> data = ((BPResourceHolder) res).getData();
				// BPXYHolder holder = new BPXYHolder();
				// holder.setData(data);
				// con = holder;
				// }
				if (res instanceof BPResourceIO)
				{
					List<Map<String, Object>> datas = null;
					try
					{
						datas = JSONUtil.decode(TextUtil.toString(IOUtil.read((BPResourceIO) res), "utf-8"));
					}
					catch (Exception e)
					{
						UIStd.err(e);
					}
					BPXYDataList xydata = new BPXYDataList(true);
					xydata.fromMapList(datas);
					BPXYHolder xyholder = new BPXYHolder();
					xyholder.setData(xydata);
					xyholder.setTitle(res.getName());
					con = xyholder;
				}
				((BPXYDEditor<BPXYContainer>) editor).bind(con);
			}
		}

		public String getName()
		{
			return "XY Editor";
		}
	}

	public final static class BPEditorFactoryText implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatUnknown.FORMAT_NA, BPFormatText.FORMAT_TEXT, BPFormatCSV.FORMAT_CSV, BPFormatTSV.FORMAT_TSV, BPFormatJSON.FORMAT_JSON };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPCodePanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res != null)
			{
				BPTextContainer con = new BPTextContainerBase();
				if (options != null)
					LogicUtil.VLF(((String) options.get("encoding")), TextUtil::checkNotEmpty, con::setEncoding);
				con.bind(res);
				((BPCodePanel) editor).bind(con);
			}
		}

		public String getName()
		{
			return "Text Editor";
		}

		public BPSetting getSetting(String formatkey)
		{
			BPSettingBase rc = new BPSettingBase();
			rc.addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
			return rc;
		}
	}

	public static class BPEditorFactoryConsole implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatUnknown.FORMAT_NA };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			String cmd = params.length == 0 ? null : (String) params[0];
			if (cmd == null && options != null)
				cmd = options.get("command");
			String dir = null;
			if (params.length > 1)
			{
				dir = (String) params[1];
				if (dir != null && dir.trim().length() == 0)
					dir = null;
			}
			String encoding = null;
			if (params.length > 2)
			{
				encoding = (String) params[2];
				if (encoding != null && encoding.trim().length() == 0)
					encoding = null;
			}
			else if (options != null)
			{
				String en = TextUtil.eds(options.get("encoding"));
				if (en != null)
					encoding = en;
			}
			BPConsolePanel rc = new BPConsolePanel();
			BPConsolePane cc = new BPConsolePane();
			BPConsoleCLI c2 = new BPConsoleCLI();
			if (encoding != null)
				c2.setEncoding(encoding);
			cc.bindConsole(c2);
			c2.setCommand(new String[] { cmd });
			c2.setWorkdir(dir);
			rc.setTextPane(cc);
			c2.start();
			return rc;
		}

		public BPSetting getSetting(String formatkey)
		{
			BPSettingBase rc = new BPSettingBase();
			rc.addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
			rc.addItem(BPSettingItem.create("command", "Command", BPSettingItem.ITEM_TYPE_TEXT, null));
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
		}

		public String getName()
		{
			return "Console";
		}
	}

	public static class BPEditorFactoryDiagram implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatJSON.FORMAT_JSON };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPDiagramPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			BPDiagramPanel pnl = (BPDiagramPanel) editor;
			if (res instanceof BPResourceHolder)
			{
				BPMHolder<BPDiagram> con = new BPMHolder<BPDiagram>();
				con.bind(res);
				con.setData(((BPResourceHolder) res).getData());
				pnl.bind(con);
			}
			else
			{
				BPJSONContainerBase<BPDiagram> con = new BPJSONContainerBase<BPDiagram>();
				con.bind(res);
				pnl.bind(con);
			}
		}

		public String getName()
		{
			return "Diagram Editor";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}

	public static class BPEditorFactoryExtWebBrowser implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { "HTML" };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			if (res.isFileSystem())
			{
				BPResourceFileSystem fres = (BPResourceFileSystem) res;
				if (fres.isLocal())
					CommonUIOperations.openExternal((BPResourceFileSystemLocal) fres);
			}
			return null;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
		}

		public String getName()
		{
			return "External WebBrowser";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}

	public final static class BPEditorFactoryRaw implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatUnknown.FORMAT_NA };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPRawEditor();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				BPDataContainerRandomAccess con = new BPDataContainerRandomAccessBase();
				con.bind(res);
				((BPRawEditor) editor).bind(con);
			}
			else if (res instanceof BPResourceHolder && ((BPResourceHolder) res).isHold(byte[].class))
			{
				BPDataContainerRandomAccess con = new BPDataContainerRandomAccessBase();
				con.bind(res);
				((BPRawEditor) editor).bind(con);
			}
		}

		public String getName()
		{
			return "Raw Editor";
		}
	}

	public static class BPEditorFactoryDataPipes implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatJSON.FORMAT_JSON };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPDataPipesPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			BPDataPipesPanel pnl = (BPDataPipesPanel) editor;
			if (res instanceof BPResourceHolder)
			{
				BPMHolder<BPDataPipes> con = new BPMHolder<BPDataPipes>();
				con.bind(res);
				con.setData(((BPResourceHolder) res).getData());
				pnl.bind(con);
			}
			else
			{
				BPJSONContainerBase<BPDataPipes> con = new BPJSONContainerBase<BPDataPipes>();
				con.bind(res);
				pnl.bind(con);
			}
		}

		public String getName()
		{
			return "Data Pipes Editor";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}
}
