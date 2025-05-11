package bp.ui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bp.config.UIConfigs;
import bp.event.BPEventUI;
import bp.ui.actions.BPAction;
import bp.ui.container.BPRoutableContainer;
import bp.ui.dialog.BPDialogBlock;
import bp.ui.scomp.BPCodeLinePanel;
import bp.ui.scomp.BPEditorPane;
import bp.ui.scomp.BPMenu;
import bp.ui.scomp.BPMenuItem;
import bp.util.LockUtil;
import bp.util.Std;

public class UIUtil
{
	public final static void inUI(Runnable seg)
	{
		if (SwingUtilities.isEventDispatchThread())
			seg.run();
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(seg);
			}
			catch (InvocationTargetException | InterruptedException e)
			{
				Std.err(e);
			}
		}
	}

	public final static void laterUI(Runnable seg)
	{
		SwingUtilities.invokeLater(seg);
	}

	public final static void laterUICombine(Runnable seg, AtomicBoolean ab)
	{
		ab.set(true);
		SwingUtilities.invokeLater(() ->
		{
			boolean t = ab.getAndSet(false);
			if (t)
				seg.run();
		});
	}

	public final static <OBJ> void laterUIOnceLast(Consumer<OBJ> seg, AtomicReference<OBJ> checker, OBJ params)
	{
		checker.set(params);
		SwingUtilities.invokeLater(() ->
		{
			OBJ obj = checker.getAndSet(null);
			if (obj != null)
			{
				seg.accept(obj);
			}
		});
	}

	public final static Color mix(Color c1, Color c2, int alpha)
	{
		return new Color((c1.getRed() + c2.getRed()) / 2, (c1.getGreen() + c2.getGreen()) / 2, (c1.getBlue() + c2.getBlue()) / 2, alpha);
	}

	public final static Color mix(Color c1, int alpha)
	{
		return new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), alpha);
	}

	public final static Font deltaFont(Font f, int delta)
	{
		return f.deriveFont((float) (f.getSize() + delta));
	}

	public final static Font monoFont(int style, int fontsize)
	{
		return new Font(UIConfigs.MONO_FONT_NAME(), style, fontsize + UIConfigs.MONO_FONT_SIZEDELTA());
	}

	public final static void transComponentFont(JComponent comp, boolean mono, boolean bold, int delta)
	{
		Font f = comp.getFont();
		Font f2 = new Font(mono ? "monospaced" : f.getName(), bold ? Font.BOLD : 0, f.getSize() + delta);
		comp.setFont(f2);
	}

	public final static Color mix2Plain(Color c1, Color c2, int alpha)
	{
		float p = 1 - ((float) alpha / 255f);
		int r = c1.getRed() + (int) (p * (c2.getRed() - c1.getRed()));
		int g = c1.getGreen() + (int) (p * (c2.getGreen() - c1.getGreen()));
		int b = c1.getBlue() + (int) (p * (c2.getBlue() - c1.getBlue()));
		if (r < 0)
			r = 0;
		if (r > 255)
			r = 255;
		if (g < 0)
			g = 0;
		if (g > 255)
			g = 255;
		if (b < 0)
			b = 0;
		if (b > 255)
			b = 255;
		return new Color(r, g, b);
	}

	public final static void setPercentWindow(Window win, float perw, float perh)
	{
		win.setPreferredSize(getPercentDimension(perw, perh));
		win.pack();
		win.setLocationRelativeTo(null);
	}

	public final static Dimension getPercentDimension(float perw, float perh)
	{
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rect = env.getMaximumWindowBounds();
		return new Dimension((int) ((float) rect.getWidth() * perw), (int) ((float) rect.getHeight() * perh));
	}

	public final static BPCodeLinePanel createLinePanel(BPEditorPane txt, JScrollPane scroll)
	{
		BPCodeLinePanel lp = new BPCodeLinePanel();
		txt.setBorder(null);
		txt.setLinePanel(lp);
		lp.setup(txt, scroll);
		scroll.setRowHeaderView(lp);
		lp.setBackground(UIConfigs.COLOR_TEXTBG());
		return lp;
	}

	public final static void clearLinePanel(BPEditorPane txt, JScrollPane scroll)
	{
		JViewport rh = scroll.getRowHeader();
		if (rh != null)
		{
			Component comp = rh.getView();
			if (comp != null && comp instanceof BPCodeLinePanel)
			{
				txt.setLinePanel(null);
				scroll.setRowHeaderView(null);
			}
		}
	}

	public final static void rebuildMenu(JMenu par, Action[] actions, boolean autovis)
	{
		par.removeAll();
		if (actions != null && actions.length > 0)
		{
			JComponent[] mnus = UIUtil.makeMenuItems(actions);
			for (JComponent mnu : mnus)
			{
				par.add(mnu);
			}
			if (autovis && !par.isVisible())
				par.setVisible(true);
		}
		else
		{
			if (autovis && par.isVisible())
				par.setVisible(false);
		}
	}

	public final static class ActionRunnable implements Runnable, Consumer<ActionEvent>
	{
		private Action act;

		public ActionRunnable(Action act)
		{
			this.act = act;
		}

		public void run()
		{
			act.actionPerformed(null);
		}

		public void accept(ActionEvent e)
		{
			if(act.isEnabled())
				act.actionPerformed(e);
		}
	}

	public final static class BPDocumentChangedHandler implements DocumentListener
	{
		private Consumer<DocumentEvent> m_cb;

		public BPDocumentChangedHandler(Consumer<DocumentEvent> cb)
		{
			m_cb = cb;
		}

		public void insertUpdate(DocumentEvent e)
		{
			m_cb.accept(e);
		}

		public void removeUpdate(DocumentEvent e)
		{
			m_cb.accept(e);
		}

		public void changedUpdate(DocumentEvent e)
		{
			m_cb.accept(e);
		}
	}

	public final static class BPComponentListener implements ComponentListener
	{
		protected Consumer<ComponentEvent> cr;
		protected Consumer<ComponentEvent> cm;
		protected Consumer<ComponentEvent> cs;
		protected Consumer<ComponentEvent> ch;

		public BPComponentListener(Consumer<ComponentEvent> oncresized, Consumer<ComponentEvent> oncmoved, Consumer<ComponentEvent> oncshown, Consumer<ComponentEvent> onchidden)
		{
			cr = oncresized;
			cm = oncmoved;
			cs = oncshown;
			ch = onchidden;
		}

		public void componentResized(ComponentEvent e)
		{
			if (cr != null)
				cr.accept(e);
		}

		public void componentMoved(ComponentEvent e)
		{
			if (cm != null)
				cm.accept(e);
		}

		public void componentShown(ComponentEvent e)
		{
			if (cs != null)
				cs.accept(e);
		}

		public void componentHidden(ComponentEvent e)
		{
			if (ch != null)
				ch.accept(e);
		}
	}

	public final static class BPMouseMotionListener implements MouseMotionListener
	{
		protected Consumer<MouseEvent> md;
		protected Consumer<MouseEvent> mm;

		public BPMouseMotionListener(Consumer<MouseEvent> onmdragged, Consumer<MouseEvent> onmmoved)
		{
			this.md = onmdragged;
			this.mm = onmmoved;
		}

		public void mouseDragged(MouseEvent e)
		{
			if (md != null)
				md.accept(e);
		}

		public void mouseMoved(MouseEvent e)
		{
			if (mm != null)
				mm.accept(e);
		}
	}

	public final static class BPMouseListenerForPopup implements MouseListener
	{
		protected Consumer<MouseEvent> m_cb;

		public BPMouseListenerForPopup(Consumer<MouseEvent> cb)
		{
			m_cb = cb;
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				if (m_cb != null)
					m_cb.accept(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				if (m_cb != null)
					m_cb.accept(e);
		}

		public void mouseClicked(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}
	}

	public final static class BPMouseListener implements MouseListener
	{
		protected Consumer<MouseEvent> mc;
		protected Consumer<MouseEvent> mp;
		protected Consumer<MouseEvent> mr;
		protected Consumer<MouseEvent> me;
		protected Consumer<MouseEvent> mx;

		public BPMouseListener(Consumer<MouseEvent> onmclick, Consumer<MouseEvent> onmpressed, Consumer<MouseEvent> onmreleased, Consumer<MouseEvent> onmentered, Consumer<MouseEvent> onmexited)
		{
			this.mc = onmclick;
			this.mp = onmpressed;
			this.mr = onmreleased;
			this.me = onmentered;
			this.mx = onmexited;
		}

		public void mouseClicked(MouseEvent e)
		{
			if (mc != null)
				mc.accept(e);
		}

		public void mousePressed(MouseEvent e)
		{
			if (mp != null)
				mp.accept(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (mr != null)
				mr.accept(e);
		}

		public void mouseEntered(MouseEvent e)
		{
			if (me != null)
				me.accept(e);
		}

		public void mouseExited(MouseEvent e)
		{
			if (mx != null)
				mx.accept(e);
		}
	}

	public final static class BPKeyListener implements KeyListener
	{
		protected Consumer<KeyEvent> kt;
		protected Consumer<KeyEvent> kp;
		protected Consumer<KeyEvent> kr;

		public BPKeyListener(Consumer<KeyEvent> onkeytyped, Consumer<KeyEvent> onkeypressed, Consumer<KeyEvent> onkeyreleased)
		{
			kt = onkeytyped;
			kp = onkeypressed;
			kr = onkeyreleased;
		}

		public void keyTyped(KeyEvent e)
		{
			if (kt != null)
				kt.accept(e);
		}

		public void keyPressed(KeyEvent e)
		{
			if (kp != null)
				kp.accept(e);
		}

		public void keyReleased(KeyEvent e)
		{
			if (kr != null)
				kr.accept(e);
		}
	}

	public final static class BPDropTargetListener implements DropTargetListener
	{
		protected Consumer<DropTargetDragEvent> dragenter;
		protected Consumer<DropTargetDragEvent> dragover;
		protected Consumer<DropTargetDragEvent> dropac;
		protected Consumer<DropTargetEvent> dragexit;
		protected Consumer<DropTargetDropEvent> drop;

		public BPDropTargetListener(Consumer<DropTargetDragEvent> ondragenter, Consumer<DropTargetDragEvent> ondragover, Consumer<DropTargetDragEvent> ondropactionchanged, Consumer<DropTargetEvent> ondragexit, Consumer<DropTargetDropEvent> ondrop)
		{
			dragenter = ondragenter;
			dragover = ondragover;
			dropac = ondropactionchanged;
			dragexit = ondragexit;
			drop = ondrop;
		}

		public void dragEnter(DropTargetDragEvent e)
		{
			if (dragenter != null)
				dragenter.accept(e);
		}

		public void dragOver(DropTargetDragEvent e)
		{
			if (dragover != null)
				dragover.accept(e);
		}

		public void dropActionChanged(DropTargetDragEvent e)
		{
			if (dropac != null)
				dropac.accept(e);
		}

		public void dragExit(DropTargetEvent e)
		{
			if (dragexit != null)
				dragexit.accept(e);
		}

		public void drop(DropTargetDropEvent e)
		{
			if (drop != null)
				drop.accept(e);
		}
	}

	@SuppressWarnings("unchecked")
	public final static JComponent[] makeMenuItems(Action[] acts)
	{
		JComponent[] rc = new JComponent[acts.length];
		for (int i = 0; i < acts.length; i++)
		{
			Action act = acts[i];
			JComponent item = null;
			Action[] subacts = (Action[]) act.getValue(BPAction.SUB_ACTIONS);
			if (subacts != null)
			{
				JMenu mnu = new JMenu(act);
				mnu.setFont(new Font(UIConfigs.MENU_FONT_NAME(), Font.PLAIN, UIConfigs.MENUFONT_SIZE()));
				item = mnu;
				JComponent[] subitems = makeMenuItems(subacts);
				for (JComponent subitem : subitems)
				{
					mnu.add(subitem);
				}
			}
			else
			{
				Supplier<Action[]> subactsfunc = (Supplier<Action[]>) act.getValue(BPAction.SUB_ACTIONS_FUNC);
				if (subactsfunc != null)
				{
					item = new BPMenu.BPMenuDynamic((String) act.getValue(Action.NAME), subactsfunc);
				}
				else
				{
					if (act.getValue(BPAction.IS_SEPARATOR) != null && (boolean) act.getValue(BPAction.IS_SEPARATOR))
					{
						item = new JPopupMenu.Separator();
					}
					else
						item = new BPMenuItem(act);
				}
			}
			if (item != null)
				rc[i] = item;
		}
		return rc;
	}

	public final static int scale(int v)
	{
		double f = UIConfigs.UI_SCALE();
		return (int) ((float) v * f);
	}

	public final static <T> T block(Supplier<CompletionStage<T>> callback, String text)
	{
		return block(callback, text, true, true);
	}

	public final static <T> T block(Supplier<CompletionStage<T>> callback, String text, boolean closeoncomplete, boolean closeonerr, Consumer<BPDialogBlock<T>> setupfunc)
	{
		BPDialogBlock<T> dlg = new BPDialogBlock<T>(callback, closeoncomplete, closeonerr);
		dlg.setTitle("Waiting");
		dlg.setText(text);
		if (setupfunc != null)
			setupfunc.accept(dlg);
		dlg.setVisible(true);
		RuntimeException err = dlg.getError();
		if (err != null)
			throw err;
		return dlg.getResult();
	}

	public final static <T> T block(Supplier<CompletionStage<T>> callback, String text, boolean closeoncomplete, boolean closeonerr)
	{
		return block(callback, text, closeoncomplete, closeonerr, null);
	}

	public final static Dimension scaleUIDimension(Dimension d)
	{
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rect = env.getMaximumWindowBounds();
		double f = UIConfigs.UI_SCALE();
		int neww = (int) ((float) d.width * f);
		int newh = (int) ((float) d.height * f);
		return new Dimension(Math.min(neww, rect.width), Math.min(newh, rect.height));
	}

	public final static Color getColorFromHexText(String text)
	{
		if (text == null)
			return null;
		if (text.startsWith("0x"))
			text = text.substring(2);
		else if (text.startsWith("#"))
			text = text.substring(1);
		int r, g, b, a;
		Color c = null;
		try
		{
			long l = Long.parseLong(text, 16);
			if (l <= 0xffffff)
			{
				r = (int) (l >> 16);
				g = (int) (l >> 8 & 0xff);
				b = (int) (l & 0xff);
				a = 255;
			}
			else
			{
				r = (int) (l >> 24 & 0xff);
				g = (int) (l >> 16 & 0xff);
				b = (int) (l >> 8 & 0xff);
				a = (int) (l & 0xff);
			}
			c = new Color(r, g, b, a);
		}
		catch (NumberFormatException e)
		{
			Std.debug(e.toString());
		}
		return c;
	}

	public final static String getRoutableContainerID(Component source)
	{
		String rc = null;
		if (source != null)
		{
			Component c = source.getParent();
			List<Component> comps = new ArrayList<Component>();
			while (c != null)
			{
				if (c instanceof BPRoutableContainer)
				{
					rc = ((BPRoutableContainer<?>) c).getID();
					break;
				}
				else
				{
					comps.add(c);
					c = c.getParent();
					if (c == null || comps.contains(c))
						break;
				}
			}
		}
		return rc;
	}

	public final static Map<String, Object> getRouteContext(Object source)
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		if (source != null)
			rc.put(BPEventUI.FIELD_ROUTABLE_CONTAINERID, getRoutableContainerID((Component) source));
		return rc;
	}

	public final static class LaterUIUpdateSegment<T>
	{
		protected volatile WeakReference<Consumer<T>> m_segref;
		protected volatile WeakReference<AtomicReference<T>> m_objrr;
		protected volatile ReadWriteLock m_rwlock;

		public LaterUIUpdateSegment(Consumer<T> seg, AtomicReference<T> ref)
		{
			m_segref = new WeakReference<Consumer<T>>(seg);
			m_objrr = new WeakReference<AtomicReference<T>>(ref);
			m_rwlock = new ReentrantReadWriteLock();
		}

		private void runSegment()
		{
			Consumer<T> seg = m_segref.get();
			AtomicReference<T> objref = m_objrr.get();
			ReadWriteLock l = m_rwlock;
			if (seg == null || objref == null)
				return;
			LockUtil.rwLock(l, false, () ->
			{
				T obj = objref.getAndSet(null);
				if (obj != null)
				{
					seg.accept(obj);
				}
			});
		}

		public void updateObject(T obj)
		{
			AtomicReference<T> objref = m_objrr.get();
			ReadWriteLock l = m_rwlock;
			if (objref != null)
			{
				LockUtil.rwLock(l, true, () ->
				{
					T old = objref.getAndSet(obj);
					if (old == null)
						laterUI(this::runSegment);
				});
			}
		}
	}
}
