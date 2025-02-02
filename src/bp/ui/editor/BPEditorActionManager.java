package bp.ui.editor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import javax.swing.Action;

import bp.BPCore;
import bp.BPGUICore;
import bp.format.BPFormatManager;
import bp.processor.BPDataProcessor;
import bp.processor.BPDataProcessorManager;
import bp.res.BPResource;
import bp.res.BPResourceHolder;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.res.icon.BPIconResV;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.util.ClassUtil;
import bp.util.LockUtil;
import bp.util.Std;

public class BPEditorActionManager
{
	private final static Map<String, List<WeakReference<Function<? extends BPEditor<?>, Action[]>>>> S_BA = new HashMap<String, List<WeakReference<Function<? extends BPEditor<?>, Action[]>>>>();
	private final static ReadWriteLock S_LOCK = new ReentrantReadWriteLock();

	public final static <T extends BPEditor<?>> void registerBarActionFactories(Class<T> cls, Function<T, Action[]> actionfac)
	{
		String clsname = cls.getName();
		List<WeakReference<Function<? extends BPEditor<?>, Action[]>>> plst = LockUtil.rwLock(S_LOCK, true, () ->
		{
			List<WeakReference<Function<? extends BPEditor<?>, Action[]>>> lst = S_BA.get(clsname);
			if (lst == null)
			{
				lst = new ArrayList<WeakReference<Function<? extends BPEditor<?>, Action[]>>>();
				S_BA.put(clsname, lst);
			}
			return lst;
		});
		plst.add(new WeakReference<Function<? extends BPEditor<?>, Action[]>>(actionfac));
	}

	@SuppressWarnings("unchecked")
	public final static Action[] getBarActions(BPEditor<?> editor)
	{
		List<Action> acts = new ArrayList<Action>();
		String clsname = editor.getClass().getName();
		List<WeakReference<Function<? extends BPEditor<?>, Action[]>>> facs = getBarActionFactories(clsname);
		if (facs != null)
		{
			for (WeakReference<Function<? extends BPEditor<?>, Action[]>> fac : facs)
			{
				Function<? extends BPEditor<?>, Action[]> func = fac.get();
				if (func != null)
				{
					try
					{
						Action[] sacts = ((Function<BPEditor<?>, Action[]>) func).apply(editor);
						if (sacts != null)
						{
							for (Action sact : sacts)
							{
								acts.add(sact);
							}
						}
					}
					catch (Error e)
					{
						Std.err(e);
					}
				}
			}
		}
		if (editor instanceof BPViewer)
		{
			BPViewer<?> viewer = (BPViewer<?>) editor;
			String[] formats = viewer.getViewerFormat();
			if (formats != null)
			{
				for (String format : formats)
				{
					if (format != null)
					{
						List<BPDataProcessor<?, ?>> ps = BPDataProcessorManager.getDataProcessors(format);
						if (ps != null && ps.size() > 0)
						{
							for (BPDataProcessor<?, ?> p : ps)
							{
								if (p.getResultFactoryClassName() != null)
								{
									BPAction act = BPAction.build(p.getName()).callback((e) ->
									{
										String facclsname = p.getResultFactoryClassName();
										BPEditorFactory fac = ClassUtil.createObject(facclsname);

										String tempid = BPCore.genID(BPCore.getFileContext());
										BPResource res = new BPResourceHolder(p.process(viewer.getViewerData(p.getDefaultPart(), format)), null, BPFormatManager.getFormatByName(format).getExts()[0], tempid, tempid, true);
										BPGUICore.EVENTS_UI.trigger(editor.getChannelID(), new BPEventUIPathTree(BPEventUIPathTree.NODE_OPEN, new Object[] { new BPResource[] { res }, BPFormatManager.getFormatByName(format), fac }));
									}).tooltip(p.getName()).vIcon(BPIconResV.DOC()).getAction();
									acts.add(act);
								}
							}
						}
					}
				}
			}
		}
		return acts.toArray(new Action[acts.size()]);
	}

	public final static List<WeakReference<Function<? extends BPEditor<?>, Action[]>>> getBarActionFactories(String clsname)
	{
		return LockUtil.rwLock(S_LOCK, false, () ->
		{
			return S_BA.get(clsname);
		});
	}
}
