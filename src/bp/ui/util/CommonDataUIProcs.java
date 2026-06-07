package bp.ui.util;

import java.awt.FlowLayout;
import java.awt.Image;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;

import bp.BPCore;
import bp.data.BPXYData;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.ui.editor.BPCodePanel;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.editor.BPTextEditor;
import bp.ui.editor.BPTextPanel;
import bp.ui.editor.BPXYDEditor;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.tree.BPTreeCellRendererObject;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncs;
import bp.ui.tree.BPTreeFuncsObject;
import bp.util.LockUtil;
import bp.util.ObjUtil;

public final class CommonDataUIProcs
{
	public final static int MODE_UNKNOWN = 0;

	public final static int MODE_OBJTREE = 1;
	public final static int MODE_OBJLIST = 2;
	public final static int MODE_XY = 3;

	public final static int MODE_DATA_EMPTY = 16;
	public final static int MODE_DATA_TEXT = 17;
	public final static int MODE_DATA_BYTEARR = 18;
	public final static int MODE_DATA_IMAGE = 19;

	public final static int MODE_RESOURCE = 33;

	public final static int MODELB_EXT = 1024;
	public final static AtomicInteger MODECOUNTER_EXT = new AtomicInteger(MODELB_EXT);

	public final static Map<Integer, Function<?, ?>> S_CPROCS = new HashMap<Integer, Function<?, ?>>();
	public final static Map<Integer, BiConsumer<?, ?>> S_IPROCS = new HashMap<Integer, BiConsumer<?, ?>>();

	public final static Map<BiPredicate<Class<?>, Object>, Integer> S_EXTMODES = new ConcurrentHashMap<>();

	private final static ReadWriteLock S_LOCK = new ReentrantReadWriteLock();

	static
	{
		initDefaults();
	}

	public final static int testDataMode(Object obj)
	{
		if (obj == null)
			return MODE_DATA_EMPTY;
		if (obj instanceof Map)
			return MODE_OBJTREE;
		if (obj instanceof Collection)
			return MODE_OBJLIST;
		if (obj instanceof String)
			return MODE_DATA_TEXT;
		if (obj instanceof byte[])
			return MODE_DATA_BYTEARR;
		if (obj instanceof Image)
			return MODE_DATA_IMAGE;
		if (obj instanceof BPResource)
			return MODE_RESOURCE;
		if (obj instanceof BPXYData)
			return MODE_XY;

		{
			Class<?> cls = obj.getClass();
			Map<BiPredicate<Class<?>, Object>, Integer> extmodes = new HashMap<>(S_EXTMODES);
			for (BiPredicate<Class<?>, Object> proc : extmodes.keySet())
			{
				if (proc.test(cls, obj))
					return extmodes.get(proc);
			}
		}

		return MODE_UNKNOWN;
	}

	public final static void useCreatePanel(int mode, BiConsumer<Function<?, ?>, BiConsumer<?, ?>> seg)
	{
		Object[] rt = LockUtil.rwLock(S_LOCK, false, () -> new Object[] { S_CPROCS.get(mode), S_IPROCS.get(mode) });
		seg.accept((Function<?, ?>) rt[0], (BiConsumer<?, ?>) rt[1]);
	}

	public final static BPTextPanel createTextPanel(Object data)
	{
		return new BPCodePanel();
	}

	public final static JPanel createEmptyPanel(Object data)
	{
		JPanel rc = new JPanel();
		BPLabel lbl = new BPLabel("N/A", BPLabel.LEFT);
		lbl.setMonoFont();
		rc.setLayout(new FlowLayout(FlowLayout.LEFT));
		rc.add(lbl);
		return rc;
	}

	public final static BPXYDEditor<?> createTablePanel(Object data)
	{
		BPXYDEditor<?> rc = new BPXYDEditor<>();
		return rc;
	}

	public final static BPTreeComponentBase createTreePanel(Object data)
	{
		BPTreeComponentBase rc = new BPTreeComponentBase();
		rc.setCellRenderer(new BPTreeCellRendererObject());
		return rc;
	}
	
	public final static BPEditor<?> createResourcePanel(BPResource res)
	{
		String id = res.openWithTempID() ? BPCore.genID(BPCore.getFileContext()) : res.getID();
		String ext = res.getExt();
		BPFormat format = BPFormatManager.getFormatByExt(ext);
		BPEditorFactory fac = BPEditorManager.getFactory(format.getName());
		if (fac == null)
		{
			return null;
		}
		BPEditor<?> editor = fac.createEditor(format, res, null);
		if (editor == null)
			return null;
		editor.setID(id);
		fac.initEditor(editor, format, res, null);
		if (editor instanceof BPTextEditor)
		{
			BPTextEditor<?, ?> teditor = ((BPTextEditor<?, ?>) editor);
			teditor.getTextPanel().resizeDoc();
		}
		return editor;
	}

	public final static void initTextPanel(BPTextPanel comp, Object data)
	{
		comp.getTextPanel().setText(ObjUtil.toString(data));
	}

	public final static void initTablePanel(BPXYDEditor<?> comp, BPXYData data)
	{
		comp.setXYData(data);
	}

	public final static void initObjTreePanel(BPTreeComponentBase comp, Object data)
	{
		BPTreeFuncs tf = new BPTreeFuncsObject(data);
		comp.setRootVisible(false);
		comp.setModel(new BPTreeModel(tf));
	}

	protected final static void initDefaults()
	{
		{
			BiConsumer<? extends JComponent, ?> ip = (BiConsumer<BPTextPanel, Object>) CommonDataUIProcs::initTextPanel;
			registerProc(MODE_UNKNOWN, CommonDataUIProcs::createTextPanel, ip);
			registerProc(MODE_DATA_TEXT, CommonDataUIProcs::createTextPanel, ip);
		}

		registerProc(MODE_DATA_EMPTY, CommonDataUIProcs::createEmptyPanel, null);
		registerProc(MODE_OBJTREE, CommonDataUIProcs::createTreePanel, (BiConsumer<BPTreeComponentBase, Object>) CommonDataUIProcs::initObjTreePanel);
		registerProc(MODE_OBJLIST, CommonDataUIProcs::createTreePanel, (BiConsumer<BPTreeComponentBase, Object>) CommonDataUIProcs::initObjTreePanel);
		registerProc(MODE_XY, CommonDataUIProcs::createTablePanel, (BiConsumer<BPXYDEditor<?>, BPXYData>) CommonDataUIProcs::initTablePanel);
		registerProc(MODE_RESOURCE, (Function<BPResource, ?>) CommonDataUIProcs::createResourcePanel, null);
	}

	public final static int registerMode(String modetype, BiPredicate<Class<?>, Object> proc)
	{
		int rc = MODECOUNTER_EXT.getAndIncrement();
		S_EXTMODES.put(proc, rc);
		return rc;
	}

	public final static void registerProc(int mode, Function<?, ?> sproc, BiConsumer<?, ?> iproc)
	{
		LockUtil.rwLock(S_LOCK, true, () ->
		{
			S_CPROCS.put(mode, sproc);
			S_IPROCS.put(mode, iproc);
		});
	}
}
