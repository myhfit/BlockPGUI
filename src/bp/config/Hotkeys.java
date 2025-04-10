package bp.config;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.config.ShortCuts.ShortCutData;
import bp.res.BPResourceIO;
import bp.task.BPTask;
import bp.ui.shortcut.BPShortCutManager;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class Hotkeys extends BPConfigAdvBase
{
	protected Consumer<? extends BPConfigAdv> m_loader = this::loadConfig;
	protected Consumer<? extends BPConfigAdv> m_persister = this::saveConfig;

	protected volatile AWTEventListener m_awtlistener;

	private final static String CFG_FILENAME = ".bphks";

	public Hotkeys()
	{
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return (Consumer<S>) m_loader;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
		m_loader = loader;
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return (Consumer<S>) m_persister;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
		m_persister = persister;
	}

	protected void loadConfig(BPConfigAdv config)
	{
		byte[] bs = IOUtil.read(BPCore.getWorkspaceContext().getConfigRes(CFG_FILENAME));
		Map<String, String> pmap = new LinkedHashMap<String, String>();
		if (bs != null)
		{
			String str = TextUtil.toString(bs, "utf-8");
			pmap = TextUtil.getPlainMap(str, true);
		}
		m_map.clear();
		m_map.putAll(pmap);
	}

	protected void saveConfig(BPConfigAdv config)
	{
		BPResourceIO cfgres = BPCore.getWorkspaceContext().getConfigRes(CFG_FILENAME, true);
		if (m_map.size() == 0)
		{
			if (cfgres == null)
				return;
		}
		else if (cfgres == null)
		{
			cfgres = BPCore.getWorkspaceContext().getConfigRes(CFG_FILENAME, false);
		}
		byte[] bs = TextUtil.fromString(TextUtil.fromPlainMap(ObjUtil.toPlainMap(m_map, true), null), "utf-8");
		if (bs != null)
		{
			IOUtil.write(cfgres, bs);
		}
	}

	protected Map<String, Object> createMap()
	{
		return new ConcurrentHashMap<String, Object>();
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		refreshHotkeys();
	}

	public Map<String, Object> getMappedData()
	{
		return new LinkedHashMap<String, Object>(m_map);
	}

	public boolean canUserConfig()
	{
		return true;
	}

	protected final static void runTarget(String k, String v)
	{
		if (k == null || k.length() == 0)
			return;
		k = k.toLowerCase();
		switch (k)
		{
			case "togglemfvis":
			{
				BPGUICore.runOnMainFrame(mf -> mf.toggleVisible());
				break;
			}
			case "task":
			{
				List<BPTask<?>> tasks = BPCore.getWorkspaceContext().getTaskManager().listTasks();
				for (BPTask<?> t : tasks)
				{
					if (v.equals(t.getName()))
					{
						if (!t.isRunning())
							t.start();
					}
				}
				break;
			}
			case "shortcut":
			{
				ShortCutData sc = ShortCuts.getShortCut(v);
				if (sc != null)
					BPShortCutManager.runShortCut(sc);
				break;
			}
		}
	}

	public final static void runHotkey(String target)
	{
		String t = target.trim();
		if (t.startsWith("!"))
			t = t.substring(1);
		int vi = t.indexOf(":");
		String k;
		String v = null;
		if (vi > 0)
		{
			k = t.substring(0, vi);
			v = t.substring(vi + 1);
		}
		else
		{
			k = t;
		}
		runTarget(k, v);
	}

	protected void clearAppHotkeys()
	{
		if (m_awtlistener != null)
		{
			Toolkit tk = Toolkit.getDefaultToolkit();
			tk.removeAWTEventListener(m_awtlistener);
			m_awtlistener = null;
		}
	}

	protected void clearSysHotkeys()
	{

	}

	protected void ensureHotkeyListener()
	{
		if (m_awtlistener != null)
			return;
		m_awtlistener = this::onAWTEvent;
		Toolkit tk = Toolkit.getDefaultToolkit();
		tk.addAWTEventListener(m_awtlistener, AWTEvent.KEY_EVENT_MASK);
	}

	public void refreshHotkeys()
	{
		boolean appflag = false;
		boolean sysflag = false;
		if (m_map.size() > 0)
		{
			Map<String, Object> m = new HashMap<String, Object>(m_map);
			for (String k : m.keySet())
			{
				boolean f = k.startsWith("!");
				if (!sysflag && f)
					sysflag = f;
				if (!appflag && !f)
					appflag = true;
			}
		}
		if (appflag)
			ensureHotkeyListener();
		else
			clearAppHotkeys();

		if (sysflag)
		{

		}
		else
			clearSysHotkeys();
	}

	protected void onAWTEvent(AWTEvent e)
	{
		if (e.getID() == KeyEvent.KEY_LAST)
		{
			KeyEvent ke = (KeyEvent) e;
			char c = (char) KeyEvent.getExtendedKeyCodeForChar(ke.getKeyCode());
			int m = ke.getModifiers();
			if (c != 0 && c != Character.MAX_VALUE && m != 0)
			{
				String mtxt = KeyEvent.getKeyModifiersText(m);
				String target = (String) m_map.get(mtxt + "+" + c);
				if (target != null && target.length() > 0)
				{
					ke.consume();
					runHotkey(target);
				}
			}
		}
	}

	public static class Hotkey
	{
		public String key;
		public String target;
		public boolean issystem;

		public Hotkey(String mkey, String target)
		{
			{
				String k = mkey == null ? "" : mkey;
				if (k != null && k.startsWith("!"))
				{
					k = k.substring(1);
					issystem = true;
				}
				key = k;
			}
			this.target = target;
		}

		public String getMapKey()
		{
			return (issystem ? "!" : "") + key;
		}
	}
}
