package bp.ui.scomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import bp.config.UIConfigs;
import bp.data.BPDiagram;
import bp.data.BPDiagram.BPDiagramElement;
import bp.data.BPDiagram.BPDiagramLayer;
import bp.data.BPDiagram.BPDiagramLink;
import bp.data.BPDiagram.BPDiagramNode;
import bp.ui.scomp.diagram.BPDiagramController;
import bp.ui.scomp.diagram.BPDiagramControllerNavigation;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;

public class BPDiagramComponent extends JComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7087171751219917995L;

	protected BPDiagram m_diagram;
	protected AffineTransform m_transform;
	protected BPDiagramSelection m_selection;

	protected BPDiagramController m_controller;

	protected WeakRefGo<BiFunction<BPDiagramElement, BPDiagramComponent, JPopupMenu>> m_contextcbref = new WeakRefGo<>();
	protected WeakRefGo<BiConsumer<BPDiagramElement, BPDiagramComponent>> m_clickref = new WeakRefGo<>(null);

	protected double m_scale;
	protected int m_rawfontsize = -1;
	protected double m_gcscale;

	protected BPComponentOverlay m_overlay;

	public BPDiagramComponent()
	{
		Font f = new Font(UIConfigs.MONO_FONT_NAME(), Font.PLAIN, UIConfigs.EDITORFONT_SIZE());
		setFont(f);
		setBackground(UIConfigs.COLOR_TEXTBG());
		setFocusable(true);
		setFocusCycleRoot(true);

		m_scale = UIConfigs.GC_SCALE() * UIConfigs.UI_SCALE();
		m_gcscale = UIConfigs.GC_SCALE();

		m_controller = new BPDiagramControllerNavigation();
		m_controller.setDiagramComponent(this);
		addMouseListener(m_controller);
		addMouseMotionListener(m_controller);
		addMouseWheelListener(m_controller);
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
	}

	public void bindDiagram(BPDiagram diagram)
	{
		m_diagram = diagram;
		setup();
	}

	protected void setup()
	{
		m_transform = new AffineTransform();
		m_transform.scale(m_scale, m_scale);
		m_transform.translate(100, 100);
		m_selection = new BPDiagramSelection();
		m_selection.setDComp(this);
		setupBounds();
	}

	public void setController(BPDiagramController controller)
	{
		BPDiagramController oldcontroller = m_controller;
		if (oldcontroller != null)
		{
			oldcontroller.clearState();
			oldcontroller.setDiagramComponent(null);
			removeMouseListener(oldcontroller);
			removeMouseMotionListener(oldcontroller);
			removeMouseWheelListener(oldcontroller);
		}
		controller.setDiagramComponent(this);
		m_controller = controller;
		addMouseListener(controller);
		addMouseMotionListener(controller);
		addMouseWheelListener(controller);
		controller.initCursor();
	}

	public void setContextCallback(BiFunction<BPDiagramElement, BPDiagramComponent, JPopupMenu> cb)
	{
		m_contextcbref.setTarget(cb);
	}

	public void setClickNodeCallback(BiConsumer<BPDiagramElement, BPDiagramComponent> cb)
	{
		m_clickref = new WeakRefGo<BiConsumer<BPDiagramElement, BPDiagramComponent>>(cb);
	}

	protected void onKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			resetController();
		}
	}

	public void resetController()
	{
		BPDiagramController controller = m_controller;
		if (!(controller.getClass().getName().equals(BPDiagramControllerNavigation.class.getName())))
		{
			setController(new BPDiagramControllerNavigation());
		}
	}

	public BPDiagramSelection getRawSelection()
	{
		return m_selection;
	}

	protected void paintComponent(Graphics g)
	{
		double scale = m_scale;
		Font f = getFont();
		if (m_rawfontsize < 0)
		{
			m_rawfontsize = (int) Math.round(f.getSize() / m_gcscale);
		}
		Color fg = getForeground();
		Color bg = getBackground();
		g.setColor(bg);
		int cx = 0;
		int cy = 0;
		int dx = 0;
		int dy = 0;
		Shape c = g.getClip();
		if (c != null)
		{
			Rectangle r = g.getClip().getBounds();
			cx = r.x;
			cy = r.y;
		}
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(fg);
		BPDiagram diagram = m_diagram;
		if (diagram == null)
			return;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		AffineTransform gtf = g2d.getTransform();
		float fgs = 1f;
		if (gtf != null)
		{
			fgs = (float) gtf.getScaleY();
		}
		if (fgs != 1f)
		{
			gtf.setToScale(1, 1);
			g2d.setTransform(gtf);
		}
		if (fgs != 1f)
		{
			if (cx > 0)
			{
				dx = (int) Math.round(cx * fgs);
				dy = (int) Math.round(cy * fgs);
			}
		}

		double[] tmppt = new double[2];
		double[] tmplink = new double[4];
		Font nf = null;
		int newfontsize = (int) Math.round((double) m_rawfontsize * scale);
		if (newfontsize != f.getSize())
		{
			nf = new Font(f.getName(), f.getStyle(), newfontsize);
			setFont(nf);
		}
		else
		{
			nf = f;
		}
		FontMetrics fm = g.getFontMetrics(nf);
		// Color fgsel = UIUtil.mix2Plain(fg, Color.RED, 128);
		Color bgsel = UIUtil.mix2Plain(bg, Color.RED, 128);
		for (BPDiagramLayer layer : diagram.getLayers())
		{
			for (BPDiagramElement element : layer.getElements())
			{
				int et = element.getElementType();
				if (et == BPDiagramElement.ELEMENTTYPE_NODE)
				{
					BPDiagramNode n = (BPDiagramNode) element;
					m_transform.transform(new double[] { n.x, n.y }, 0, tmppt, 0, 1);
					String text = element.label;
					if (text == null || text.length() == 0)
						text = " ";
					double[] msize = n.measuresize;
					int mflag = n.measureflag;
					int x, y, rw, rh, a;
					double w, h;
					String[] lines = text.split("\n");
					if (msize != null && mflag == newfontsize)
					{
						w = msize[0] * scale;
						h = msize[1] * scale;
						a = (int) (Math.round(msize[2]) * scale);
						x = (int) Math.round(tmppt[0] - (w / 2));
						y = (int) Math.round(tmppt[1] - (h / 2));
					}
					else
					{
						Object[][] measures = UIUtil.measureLines(fm, lines, g);
						double[] newms;
						{
							int l = lines.length;
							newms = new double[l + l + l + 3];
						}

						w = 0;
						h = 0;
						if (measures.length > 0)
						{
							a = (int) Math.round(((LineMetrics) measures[0][0]).getAscent());
							int sti = 3;
							for (int i = 0; i < measures.length; i++)
							{
								LineMetrics lm = (LineMetrics) measures[i][0];
								Rectangle2D rect = (Rectangle2D) measures[i][1];
								double linew = rect.getWidth();
								double lineh = rect.getHeight();
								w = Math.max(w, linew);
								h += lineh;

								newms[sti] = linew / scale;
								newms[sti + 1] = lineh / scale;
								newms[sti + 2] = lm.getAscent() / scale;
								sti += 3;
							}
							newms[0] = w / scale;
							newms[1] = h / scale;
							newms[2] = a / scale;
						}
						else
						{
							a = 0;
							newms = new double[] { w / scale, h / scale, a / scale };
						}

						x = (int) Math.round(tmppt[0] - (w / 2));
						y = (int) Math.round(tmppt[1] - (h / 2));
						n.measuresize = newms;
						n.measureflag = newfontsize;
						msize = newms;
					}
					rw = (int) Math.round(w);
					rh = (int) Math.round(h);

					if (n.isSelected())
						g.setColor(bgsel);
					else
						g.setColor(bg);

					g.fillRect(x - 2 - dx, y - 1 - dy, rw + 4, rh + 2);
					g.setColor(fg);
					g.drawRect(x - 2 - dx, y - 1 - dy, rw + 4, rh + 2);

					int ty = 0;
					for (int i = 0; i < lines.length; i++)
					{
						g.drawString(lines[i], x - dx, y + a - dy + ty);
						ty += msize[4 + i * 3];
					}
				}
				else if (et == BPDiagramElement.ELEMENTTYPE_LINK)
				{
					BPDiagramLink l = (BPDiagramLink) element;
					BPDiagramNode n1 = l.n1;
					BPDiagramNode n2 = l.n2;
					m_transform.transform(new double[] { n1.x, n1.y, n2.x, n2.y }, 0, tmplink, 0, 2);
					int px1 = (int) Math.round(tmplink[0]);
					int py1 = (int) Math.round(tmplink[1]);
					int px2 = (int) Math.round(tmplink[2]);
					int py2 = (int) Math.round(tmplink[3]);
					if (l.isSelected())
						g.setColor(bgsel);
					else
						g.setColor(fg);

					g.drawLine((int) Math.round(px1) - dx, (int) Math.round(py1) - dy, (int) Math.round(px2) - dx, (int) Math.round(py2) - dy);
					String text = element.label;
					if (text != null)
					{
						// Rectangle2D rect = fm.getStringBounds(text, g);
						// g.drawString(element.label, (int) Math.round(px -
						// (rect.getWidth() / 2)), (int) Math.round(py -
						// (rect.getHeight() / 2)));
					}
				}
			}
		}

		BPComponentOverlay overlay = m_overlay;
		if (overlay != null)
		{
			g.setXORMode(bg);
			overlay.draw(g, 0, 0, getWidth(), getHeight(), 0, 0, fgs);
			g.setPaintMode();
		}
	}

	public void setComponentOverlay(BPComponentOverlay overlay)
	{
		m_overlay = overlay;
	}

	public List<BPDiagramElement> getSelectedElements()
	{
		BPDiagram diagram = m_diagram;
		List<BPDiagramElement> rc = new ArrayList<BPDiagramElement>();
		for (String key : m_selection.getKeys())
		{
			BPDiagramElement ele = diagram.findElement(key);
			if (ele != null)
				rc.add(ele);
		}
		return rc;
	}

	public BPDiagramElement getElementFromPos(int x, int y)
	{
		BPDiagram diagram = m_diagram;
		double[] mpt = viewPt2ModelPt(new double[] { x, y });
		List<BPDiagramLayer> layers = diagram.getLayers();
		for (int i = layers.size() - 1; i >= 0; i--)
		{
			BPDiagramLayer layer = layers.get(i);
			for (BPDiagramElement element : layer.getElements())
			{
				if (element.test(mpt[0], mpt[1]))
				{
					return element;
				}
			}
		}
		return null;
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public static class BPDiagramSelection
	{
		protected List<String> m_keys;
		protected int m_mode;// 1-single,2-plus
		protected WeakRefGo<BPDiagramComponent> m_dcompref;

		public BPDiagramSelection()
		{
			m_keys = new ArrayList<String>();
			m_mode = 1;
		}

		public void setDComp(BPDiagramComponent dcomp)
		{
			m_dcompref = new WeakRefGo<BPDiagramComponent>(dcomp);
		}

		public void setMode(int mode)
		{
			m_mode = mode;
		}

		public List<String> getKeys()
		{
			return new ArrayList<String>(m_keys);
		}

		public void setKeys(List<String> keys)
		{
			List<String> oldkeys = new ArrayList<String>(m_keys);
			m_keys.clear();
			m_keys.addAll(keys);
			BPDiagram d = m_dcompref.get().getDiagram();
			for (String key : oldkeys)
			{
				if (!keys.contains(key))
				{
					BPDiagramElement e = d.findElement(key);
					if (e != null)
						e.setSelected(false);
				}
			}
			for (String key : keys)
			{
				BPDiagramElement e = d.findElement(key);
				if (e != null)
					e.setSelected(true);
			}
		}

		public void addKeys(List<String> keys, boolean cascade)
		{
			for (String key : keys)
			{
				if (!m_keys.contains(key))
					m_keys.add(key);
			}
			if (cascade)
			{
				BPDiagram d = m_dcompref.get().getDiagram();
				if (d != null)
				{
					for (String key : keys)
					{
						BPDiagramElement e = d.findElement(key);
						if (e != null)
							e.setSelected(true);
					}
				}
			}
		}

		public void removeKeys(List<String> keys, boolean cascade)
		{
			m_keys.removeAll(keys);
			if (cascade)
			{
				BPDiagram d = m_dcompref.get().getDiagram();
				if (d != null)
				{
					for (String key : keys)
					{
						BPDiagramElement e = d.findElement(key);
						if (e != null)
							e.setSelected(false);
					}
				}
			}
		}

		public void selectElement(BPDiagramElement element, boolean isctrl)
		{
			boolean issel = element.isSelected();
			String key = element.key;
			BPDiagramComponent dcomp = m_dcompref.get();
			BPDiagram diagram = dcomp.getDiagram();
			if (issel)
			{
				int i = m_keys.indexOf(key);
				if (i > -1)
					m_keys.remove(i);
				if (m_mode == 1)// single
				{
					List<String> delkeys = new ArrayList<String>(m_keys);
					i = delkeys.indexOf(key);
					if (i > -1)
						delkeys.remove(i);
					for (String dkey : delkeys)
					{
						diagram.findElement(dkey).setSelected(false);
					}
					m_keys.clear();
					m_keys.add(key);
				}
			}
			else
			{
				if (isctrl)
				{
					m_keys.add(key);
				}
				else if (m_mode == 1)// single
				{
					List<String> delkeys = new ArrayList<String>(m_keys);
					int i = delkeys.indexOf(key);
					if (i > -1)
						delkeys.remove(i);
					for (String dkey : delkeys)
					{
						diagram.findElement(dkey).setSelected(false);
					}
					m_keys.clear();
					m_keys.add(key);
				}
			}
			element.setSelected(!issel);
			dcomp.repaint();
		}
	}

	public BPDiagram getDiagram()
	{
		return m_diagram;
	}

	public void setupBounds()
	{
		double[] bs = m_diagram.calcBounds();
		if (bs[0] == Double.MAX_VALUE || bs[1] == 0 - Double.MAX_VALUE || bs[2] == Double.MAX_VALUE || bs[3] == 0 - Double.MAX_VALUE)
		{
			return;
		}
		int max = 20000;
		int w = (int) bs[2] + 100;// (int) (bs[2] - bs[0]);
		int h = (int) bs[3] + 100;// (int) (bs[3] - bs[1]);
		if (w > max)
			w = max;
		if (h > max)
			h = max;
		setPreferredSize(new Dimension(w, h));
		setSize(new Dimension(w, h));
		refresh();
	}

	public void deleteElements(List<String> keys)
	{
		BPDiagram d = m_diagram;
		m_selection.removeKeys(keys, false);
		List<BPDiagramElement> delnodes = new ArrayList<BPDiagramElement>();
		List<BPDiagramElement> dellinks = new ArrayList<BPDiagramElement>();
		for (String key : keys)
		{
			BPDiagramElement ele = d.findElement(key);
			int eletype = ele.getElementType();
			if (eletype == BPDiagramElement.ELEMENTTYPE_NODE)
			{
				delnodes.add(ele);
				d.eachElement((e, l) ->
				{
					if (e.getElementType() == BPDiagramElement.ELEMENTTYPE_LINK)
					{
						BPDiagramLink link = (BPDiagramLink) e;
						if (link.n1 == ele || link.n2 == ele)
						{
							if (!dellinks.contains(link))
								dellinks.add(link);
						}
					}
				});
			}
			else if (eletype == BPDiagramElement.ELEMENTTYPE_LINK)
			{
				if (!dellinks.contains(ele))
					dellinks.add(ele);
			}
		}
		Map<Integer, BPDiagramLayer> layermap = m_diagram.getLayerMap();
		for (BPDiagramElement dellink : dellinks)
		{
			layermap.get(dellink.layerid).removeElement(dellink);
		}
		for (BPDiagramElement delnode : delnodes)
		{
			layermap.get(delnode.layerid).removeElement(delnode);
		}

		refresh();
	}

	public void deleteSelectedElement()
	{
		BPDiagram d = m_diagram;
		BPDiagramSelection selection = m_selection;
		List<BPDiagramElement> delnodes = new ArrayList<BPDiagramElement>();
		List<BPDiagramElement> dellinks = new ArrayList<BPDiagramElement>();
		for (String selkey : selection.getKeys())
		{
			BPDiagramElement ele = d.findElement(selkey);
			int eletype = ele.getElementType();
			if (eletype == BPDiagramElement.ELEMENTTYPE_NODE)
			{
				delnodes.add(ele);
				d.eachElement((e, l) ->
				{
					if (e.getElementType() == BPDiagramElement.ELEMENTTYPE_LINK)
					{
						BPDiagramLink link = (BPDiagramLink) e;
						if (link.n1 == ele || link.n2 == ele)
						{
							if (!dellinks.contains(link))
								dellinks.add(link);
						}
					}
				});
			}
			else if (eletype == BPDiagramElement.ELEMENTTYPE_LINK)
			{
				if (!dellinks.contains(ele))
					dellinks.add(ele);
			}
		}
		Map<Integer, BPDiagramLayer> layermap = m_diagram.getLayerMap();
		List<String> dkeys = new ArrayList<String>();
		for (BPDiagramElement dellink : dellinks)
		{
			if (!dkeys.contains(dellink.key))
				dkeys.add(dellink.key);
			layermap.get(dellink.layerid).removeElement(dellink);
		}
		for (BPDiagramElement delnode : delnodes)
		{
			if (!dkeys.contains(delnode.key))
				dkeys.add(delnode.key);
			layermap.get(delnode.layerid).removeElement(delnode);
		}
		m_selection.removeKeys(dkeys, false);

		refresh();
	}

	public void refresh()
	{
		validate();
		invalidate();
		repaint();
	}

	public void selectElement(BPDiagramElement ele, boolean isctrl, int btn)
	{
		if (m_diagram == null)
			return;

		m_selection.selectElement(ele, isctrl);
		refresh();

		if (ele != null)
			m_clickref.run(cb -> cb.accept(ele, this));
	}

	public void showContextMenu(BPDiagramElement ele, int x, int y)
	{
		BPDiagramElement selelef = ele;
		JPopupMenu pop = m_contextcbref.exec(cb -> cb.apply(selelef, this));
		if (pop != null)
			pop.show(this, x, y);
	}

	public void dragElement(BPDiagramElement downele, int[] dragpts, double[] dragelepts)
	{
		if (downele.getElementType() == BPDiagramElement.ELEMENTTYPE_NODE)
		{
			BPDiagramNode node = (BPDiagramNode) downele;
			double[] orivpt = modelPt2ViewPt(new double[] { dragelepts[0], dragelepts[1] });
			double[] newpt = viewPt2ModelPt(new double[] { dragpts[2] + orivpt[0] - dragpts[0], dragpts[3] + orivpt[1] - dragpts[1] });
			node.x = newpt[0];
			node.y = newpt[1];
			repaint();
		}
	}

	public double[] modelPt2ViewPt(double[] pt)
	{
		double[] npt = new double[2];
		m_transform.transform(pt, 0, npt, 0, 1);
		return new double[] { npt[0] / m_gcscale, npt[1] / m_gcscale };
	}

	public double[] viewPt2ModelPt(double[] pt)
	{
		double[] npt = new double[2];
		try
		{
			m_transform.inverseTransform(new double[] { pt[0] * m_gcscale, pt[1] * m_gcscale }, 0, npt, 0, 1);
		}
		catch (NoninvertibleTransformException e)
		{
		}
		return npt;
	}

	public void moveDiagram(int[] offsetpt)
	{
		int newx = offsetpt[2];
		int newy = offsetpt[3];
		int lastx = offsetpt[4];
		int lasty = offsetpt[5];
		double scale = m_scale / m_gcscale;

		int newxreal = (int) Math.round((double) newx / scale);
		int newyreal = (int) Math.round((double) newy / scale);
		int lastxreal = (int) Math.round((double) lastx / scale);
		int lastyreal = (int) Math.round((double) lasty / scale);

		int dx = newxreal - lastxreal;
		int dy = newyreal - lastyreal;
		if (dx == 0 && dy == 0)
			return;
		m_transform.translate(dx, dy);
		refresh();
	}

	public void stopDrag()
	{
	}

	public void scale(int i)
	{
		double scale = m_scale;
		if ((scale > 100 && i > 0) || (scale < 0.01 && i < 0))
			return;
		double tp = Math.pow(1.2, i);
		scale = scale * tp;
		m_scale = scale;
		m_transform.scale(tp, tp);
		refresh();
	}
}
