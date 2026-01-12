package bp.ui.res.icon;

import bp.config.UIConfigs;

public class BPIconResV
{
	private static BPVIcon S_ICON_BP;
	private static BPVIcon S_ICON_CLOSE;
	private static BPVIcon S_ICON_DROPDOWN;
	private static BPVIcon S_ICON_REFRESH;
	private static BPVIcon S_ICON_SAVE;
	private static BPVIcon S_ICON_NOTSAVE;
	private static BPVIcon S_ICON_EXPORT;
	private static BPVIcon S_ICON_ADD;
	private static BPVIcon S_ICON_DEL;
	private static BPVIcon S_ICON_START;
	private static BPVIcon S_ICON_RESUME;
	private static BPVIcon S_ICON_TOEND;
	private static BPVIcon S_ICON_NEXT;
	private static BPVIcon S_ICON_PREV;
	private static BPVIcon S_ICON_STOP;
	private static BPVIcon S_ICON_KILL;
	private static BPVIcon S_ICON_EDIT;
	private static BPVIcon S_ICON_PATHTREE;
	private static BPVIcon S_ICON_PATHTREECOMPUTER;
	private static BPVIcon S_ICON_PATHTREESPECIAL;
	private static BPVIcon S_ICON_PRJSTREE;
	private static BPVIcon S_ICON_CONNECT;
	private static BPVIcon S_ICON_DISCONNECT;
	private static BPVIcon S_ICON_DOC;
	private static BPVIcon S_ICON_CLONE;
	private static BPVIcon S_ICON_TOUP;
	private static BPVIcon S_ICON_TODOWN;
	private static BPVIcon S_ICON_TORIGHT;
	private static BPVIcon S_ICON_TOLEFT;
	private static BPVIcon S_ICON_TORIGHTCLEAN;
	private static BPVIcon S_ICON_TOLEFTCLEAN;
	private static BPVIcon S_ICON_LEFTRIGHT;
	private static BPVIcon S_ICON_UPDOWN;
	private static BPVIcon S_ICON_RELATION;
	private static BPVIcon S_ICON_LAYOUT;
	private static BPVIcon S_ICON_SETTING;
	private static BPVIcon S_ICON_MORE;
	private static BPVIcon S_ICON_RECTSEL;
	private static BPVIcon S_ICON_FIND;
	private static BPVIcon S_ICON_IMG;

	public final static BPVIcon BP()
	{
		if (S_ICON_BP == null)
		{
			S_ICON_BP = (g, x0, y0, w, h) ->
			{
				g.setColor(UIConfigs.COLOR_TEXTHALF());
				g.drawRect(x0 + 2, y0, w - 5, h - 1);
			};
		}
		return S_ICON_BP;
	}

	public final static BPVIcon CLOSE()
	{
		if (S_ICON_CLOSE == null)
		{
			S_ICON_CLOSE = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int y1 = y0 + h - 1;
				g.drawLine(x0 + 3, y0 + 3, x1 - 3, y1 - 3);
				g.drawLine(x0 + 3, y1 - 3, x1 - 3, y0 + 3);
			};
		}
		return S_ICON_CLOSE;
	}

	public final static BPVIcon UPDOWN()
	{
		if (S_ICON_UPDOWN == null)
		{
			S_ICON_UPDOWN = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int c = w / 2;
				g.drawLine(x0 + 4, y0 + c - 3, x0 + c - 1, y0 + 2);
				g.drawLine(x1 - c + 1, y0 + 2, x1 - 4, y0 + c - 3);
				g.drawLine(x0 + 4, y0 + c + 3, x0 + c - 1, y0 + c + c - 2);
				g.drawLine(x1 - c + 1, y0 + c + c - 2, x1 - 4, y0 + c + 3);
			};
		}
		return S_ICON_UPDOWN;
	}

	public final static BPVIcon DROPDOWN()
	{
		if (S_ICON_DROPDOWN == null)
		{
			S_ICON_DROPDOWN = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int c = w / 2;
				g.drawLine(x0 + 4, y0 + c, x0 + c - 1, y0 + c + c - 5);
				g.drawLine(x1 - c + 1, y0 + c + c - 5, x1 - 4, y0 + c);
			};
		}
		return S_ICON_DROPDOWN;
	}

	public final static BPVIcon REFRESH()
	{
		if (S_ICON_REFRESH == null)
		{
			S_ICON_REFRESH = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int y1 = y0 + h - 1;
				int w0 = w / 4;
				int gx0 = x0 + 2;
				int gx1 = x1 - 2;
				int gy0 = y0 + 2;
				int gy1 = y1 - 2;
				g.drawLine(gx0, gy0, gx1, gy0);
				g.drawLine(gx0, gy0, gx0, gy1 - w0);
				g.drawLine(gx1, gy0, gx1 - w0, gy0 + w0);
				g.drawLine(gx1, gy1, gx1, gy0 + w0 + 1);
				g.drawLine(gx1, gy1, gx0, gy1);
				g.drawLine(gx0, gy1, gx0 + w0, gy1 - w0);
			};
		}
		return S_ICON_REFRESH;
	}

	public final static BPVIcon SAVE()
	{
		if (S_ICON_SAVE == null)
		{
			S_ICON_SAVE = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int y1 = y0 + h - 1;
				int r = w / 8;
				int dw = w / 4;
				int dh = w / 3;
				g.drawPolygon(new int[] { x0 + r, x1 - r, x1 - 1, x1 - 1, x1 - r, x0 + r, x0 + 1, x0 + 1 }, new int[] { y0 + 1, y0 + 1, y0 + r, y1 - r, y1 - 1, y1 - 1, y1 - r, y0 + r }, 8);
				g.drawPolygon(new int[] { x0 + dw, x0 + dw, x1 - dw, x1 - dw }, new int[] { y1 - 1, y1 - 1 - dh, y1 - 1 - dh, y1 - 1 }, 4);
				g.drawPolygon(new int[] { x0 + r + 1, x0 + r + 1, x1 - r - 1, x1 - r - 1 }, new int[] { y0 + 1, y0 + 1 + dh, y0 + 1 + dh, y0 + 1 }, 4);
			};
		}
		return S_ICON_SAVE;
	}

	public final static BPVIcon NOTSAVE()
	{
		if (S_ICON_NOTSAVE == null)
		{
			S_ICON_NOTSAVE = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int y1 = y0 + h - 1;
				int r = w / 8;
				int dw = w / 4;
				int dh = w / 3;
				g.drawPolygon(new int[] { x0 + r, x1 - r, x1 - 1, x1 - 1, x1 - r, x0 + r, x0 + 1, x0 + 1 }, new int[] { y0 + 1, y0 + 1, y0 + r, y1 - r, y1 - 1, y1 - 1, y1 - r, y0 + r }, 8);
				g.drawPolygon(new int[] { x0 + dw, x0 + dw, x1 - dw, x1 - dw }, new int[] { y1 - 1, y1 - 1 - dh, y1 - 1 - dh, y1 - 1 }, 4);
				g.drawPolygon(new int[] { x0 + r + 1, x0 + r + 1, x1 - r - 1, x1 - r - 1 }, new int[] { y0 + 1, y0 + 1 + dh, y0 + 1 + dh, y0 + 1 }, 4);
				g.drawLine(x0 + 2, y0 + 1, x1 - 1, y1 - 2);
				g.drawLine(x0 + 1, y0 + 1, x1 - 1, y1 - 1);
				g.drawLine(x0 + 1, y0 + 2, x1 - 2, y1 - 1);
				g.drawLine(x0 + 2, y1 - 1, x1 - 1, y0 + 2);
				g.drawLine(x0 + 1, y1 - 1, x1 - 1, y0 + 1);
				g.drawLine(x0 + 1, y1 - 2, x1 - 2, y0 + 1);
			};
		}
		return S_ICON_NOTSAVE;
	}

	public final static BPVIcon EXPORT()
	{
		if (S_ICON_EXPORT == null)
		{
			S_ICON_EXPORT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int y1 = y0 + h - 1;
				int r = w / 8;
				int dw = w / 4;
				int dh = w / 3;
				g.drawPolygon(new int[] { x0 + r, x1 - r, x1 - 1, x1 - 1, x1 - r, x0 + r, x0 + 1, x0 + 1 }, new int[] { y0 + 1, y0 + 1, y0 + r, y1 - r, y1 - 1, y1 - 1, y1 - r, y0 + r }, 8);
				g.drawPolygon(new int[] { x0 + dw, x0 + dw, x1 - dw, x1 - dw }, new int[] { y1 - 1, y1 - 1 - dh, y1 - 1 - dh, y1 - 1 }, 4);
				g.drawPolygon(new int[] { x0 + r + 1, x0 + r + 1, x1 - r - 1, x1 - r - 1 }, new int[] { y0 + 1, y0 + 1 + dh, y0 + 1 + dh, y0 + 1 }, 4);
			};
		}
		return S_ICON_EXPORT;
	}

	public final static BPVIcon ADD()
	{
		if (S_ICON_ADD == null)
		{
			S_ICON_ADD = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05, x1 - 2, y05);
				g.drawLine(x05, y0 + 1, x05, y1 - 2);
			};
		}
		return S_ICON_ADD;
	}

	public final static BPVIcon DEL()
	{
		if (S_ICON_DEL == null)
		{
			S_ICON_DEL = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05, x1 - 2, y05);
			};
		}
		return S_ICON_DEL;
	}

	public final static BPVIcon START()
	{
		if (S_ICON_START == null)
		{
			S_ICON_START = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawPolygon(new int[] { x1 - 3, x0 + 2, x0 + 2 }, new int[] { y05, y0 + 2, y1 - 2 }, 3);
			};
		}
		return S_ICON_START;
	}

	public final static BPVIcon RESUME()
	{
		if (S_ICON_RESUME == null)
		{
			S_ICON_RESUME = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y0 + 2, x0 + 1, y1 - 2);
				g.drawPolygon(new int[] { x1 - 2, x0 + 3, x0 + 3 }, new int[] { y05, y0 + 2, y1 - 2 }, 3);
			};
		}
		return S_ICON_RESUME;
	}

	public final static BPVIcon TOEND()
	{
		if (S_ICON_TOEND == null)
		{
			S_ICON_TOEND = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x1 - 2, y0 + 2, x1 - 2, y1 - 2);
				g.drawPolygon(new int[] { x1 - 4, x0 + 1, x0 + 1 }, new int[] { y05, y0 + 2, y1 - 2 }, 3);
			};
		}
		return S_ICON_TOEND;
	}

	public final static BPVIcon NEXT()
	{
		if (S_ICON_NEXT == null)
		{
			S_ICON_NEXT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x1 - 2, y0 + 2, x1 - 2, y1 - 2);
				g.drawPolygon(new int[] { x1 - 3, x0 + 2, x0 + 2 }, new int[] { y05, y0 + 2, y1 - 2 }, 3);
			};
		}
		return S_ICON_NEXT;
	}

	public final static BPVIcon PREV()
	{
		if (S_ICON_PREV == null)
		{
			S_ICON_PREV = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 2, y0 + 2, x0 + 2, y1 - 2);
				g.drawPolygon(new int[] { x0 + 3, x1 - 2, x1 - 2 }, new int[] { y05, y0 + 2, y1 - 2 }, 3);
			};
		}
		return S_ICON_PREV;
	}

	public final static BPVIcon STOP()
	{
		if (S_ICON_STOP == null)
		{
			S_ICON_STOP = (g, x0, y0, w, h) ->
			{
				g.drawRect(x0 + 2, y0 + 2, w - 5, h - 4);
			};
		}
		return S_ICON_STOP;
	}

	public final static BPVIcon KILL()
	{
		if (S_ICON_KILL == null)
		{
			S_ICON_KILL = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				g.drawLine(x0 + 2, y0 + 2, x1 - 2, y1 - 2);
				g.drawLine(x0 + 2, y1 - 2, x1 - 2, y0 + 2);
			};
		}
		return S_ICON_KILL;
	}

	public final static BPVIcon EDIT()
	{
		if (S_ICON_EDIT == null)
		{
			S_ICON_EDIT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x11 = x0 + 2 + ((w - 5) / 4);
				int x12 = x1 - 3 - ((w - 5) / 4);
				int y11 = y0 + 1 + ((h - 2) / 6);
				int y12 = y11 + ((h - 2) / 4);
				int y13 = y1 - 1 - ((h - 2) / 4);
				int y121 = (y12 + y13) / 2;

				g.drawRect(x0 + 1, y0 + 1, w - 3, h - 2);
				g.drawLine(x0 + 2, y11, x1 - 3, y11);
				g.drawLine(x11, y12, x12, y12);
				g.drawLine(x11, y121, x12, y121);
				g.drawLine(x11, y13, x12, y13);
			};
		}
		return S_ICON_EDIT;
	}

	public final static BPVIcon CLONE()
	{
		if (S_ICON_CLONE == null)
		{
			S_ICON_CLONE = (g, x0, y0, w, h) ->
			{
				int x02 = x0 + (w / 4);
				int x03 = x0 + (w / 4 * 3);

				g.drawRect(x0 + 1, y0 + 1, x03 - x0 - 2, h - 4);
				g.drawRect(x02, y0 + 3, x03 - x0 - 2, h - 4);
			};
		}
		return S_ICON_CLONE;
	}

	public final static BPVIcon DOC()
	{
		if (S_ICON_DOC == null)
		{
			S_ICON_DOC = (g, x0, y0, w, h) ->
			{
				int x02 = x0 + (int) Math.floor(w / 8);
				int x03 = x0 + (w / 4 * 3);
				int y1 = y0 + (h / 4);
				int y2 = y0 + (h / 2);
				int y3 = y0 + (h / 4 * 3);

				g.drawRect(x02 + 1, y0 + 1, x03 - 2, h - 2);
				g.drawLine(x02 + 3, y1, x02 + x03 - 3, y1);
				g.drawLine(x02 + 3, y2, x02 + x03 - 3, y2);
				g.drawLine(x02 + 3, y3, x02 + x03 - 3, y3);
			};
		}
		return S_ICON_DOC;
	}

	public final static BPVIcon PRJSTREE()
	{
		if (S_ICON_PRJSTREE == null)
		{
			S_ICON_PRJSTREE = (g, x0, y0, w, h) ->
			{
				int y1 = y0 + h;
				int r = w / 10;
				g.drawRect(x0 + (w / 4), y0 + 2 + r, w / 2, h / 2 - r - 1);
				g.drawLine(x0 + (w / 4), y0 + 1 + r + r, x0 + (w / 4), y1 - 2 - r);
			};
		}
		return S_ICON_PRJSTREE;
	}

	public final static BPVIcon PATHTREE()
	{
		if (S_ICON_PATHTREE == null)
		{
			S_ICON_PATHTREE = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int w2 = w / 10;
				g.drawLine(x0 + w2 + 1, y0 + 2 + w2, x1 - 2 - w2, y0 + 2 + w2);
				g.drawLine(x0 + (w / 2) - ((w + 1) % 2), y0 + 2 + w2, x0 + (w / 2) - ((w + 1) % 2), y1 - 2 - w2);
			};
		}
		return S_ICON_PATHTREE;
	}

	public final static BPVIcon PATHTREE_COMPUTER()
	{
		if (S_ICON_PATHTREECOMPUTER == null)
		{
			S_ICON_PATHTREECOMPUTER = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int w2 = w / 10;
				g.drawLine(x0 + w2 + 2, y0 + 2 + w2, x1 - 2 - w2, y0 + 2 + w2);
				g.drawLine(x0 + 2 + w2, y0 + 2 + w2, x0 + 2 + w2, y1 - 2 - w2);
				g.drawLine(x0 + 2 + w2, y1 - 2 - w2, x1 - 2 - w2, y1 - 2 - w2);
			};
		}
		return S_ICON_PATHTREECOMPUTER;
	}

	public final static BPVIcon PATHTREE_SPECIAL()
	{
		if (S_ICON_PATHTREESPECIAL == null)
		{
			S_ICON_PATHTREESPECIAL = (g, x0, y0, w, h) ->
			{

				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				int w2 = w / 10;
				g.drawLine(x0 + w2 + 2, y0 + 2 + w2, x1 - 2 - w2, y0 + 2 + w2);
				g.drawLine(x0 + 2 + w2, y0 + 2 + w2, x0 + 2 + w2, y05);
				g.drawLine(x0 + 2 + w2, y05, x1 - 2 - w2, y05);
				g.drawLine(x1 - 2 - w2, y05, x1 - 2 - w2, y1 - 2 - w2);
				g.drawLine(x0 + 2 + w2, y1 - 2 - w2, x1 - 2 - w2, y1 - 2 - w2);
			};
		}
		return S_ICON_PATHTREESPECIAL;
	}

	public final static BPVIcon CONNECT()
	{
		if (S_ICON_CONNECT == null)
		{
			S_ICON_CONNECT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				g.drawLine(x0 + 2, y1 - 2, x1 - 2, y0 + 2);
				g.drawLine(x1 - 2, y0 + 2, x1 - 6, y0 + 2);
				g.drawLine(x1 - 2, y0 + 2, x1 - 2, y0 + 6);
			};
		}
		return S_ICON_CONNECT;
	}

	public final static BPVIcon DISCONNECT()
	{
		if (S_ICON_DISCONNECT == null)
		{
			S_ICON_DISCONNECT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 2, y1 - 2, x05 - 2, y05 + 2);
				g.drawLine(x05 - 2, y05 - 2, x05 + 2, y05 + 2);
				g.drawLine(x05 + 2, y05 - 2, x1 - 2, y0 + 2);
			};
		}
		return S_ICON_DISCONNECT;
	}

	public final static BPVIcon TOLEFT()
	{
		if (S_ICON_TOLEFT == null)
		{
			S_ICON_TOLEFT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05, x1 - 1, y05);
				g.drawLine(x0 + 4, y05 - 3, x0 + 1, y05);
				g.drawLine(x0 + 4, y05 + 3, x0 + 1, y05);
			};
		}
		return S_ICON_TOLEFT;
	}

	public final static BPVIcon TORIGHT()
	{
		if (S_ICON_TORIGHT == null)
		{
			S_ICON_TORIGHT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05, x1 - 1, y05);
				g.drawLine(x1 - 4, y05 - 3, x1 - 1, y05);
				g.drawLine(x1 - 4, y05 + 3, x1 - 1, y05);
			};
		}
		return S_ICON_TORIGHT;
	}

	public final static BPVIcon TOUP()
	{
		if (S_ICON_TOUP == null)
		{
			S_ICON_TOUP = (g, x0, y0, w, h) ->
			{
				int y1 = y0 + h;
				int x05 = (y0 + y1) / 2;
				g.drawLine(x05, y0 + 1, x05, y1 - 1);
				g.drawLine(x05 - 3, y0 + 4, x05, y0 + 1);
				g.drawLine(x05 + 3, y0 + 4, x05, y0 + 1);
			};
		}
		return S_ICON_TOUP;
	}

	public final static BPVIcon TODOWN()
	{
		if (S_ICON_TODOWN == null)
		{
			S_ICON_TODOWN = (g, x0, y0, w, h) ->
			{
				int y1 = y0 + h;
				int x05 = (y0 + y1) / 2;
				g.drawLine(x05, y0 + 1, x05, y1 - 1);
				g.drawLine(x05 - 3, y1 - 4, x05, y1 - 1);
				g.drawLine(x05 + 3, y1 - 4, x05, y1 - 1);
			};
		}
		return S_ICON_TODOWN;
	}

	public final static BPVIcon TOLEFTCLEAN()
	{
		if (S_ICON_TOLEFTCLEAN == null)
		{
			S_ICON_TOLEFTCLEAN = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05 + 1, x1 - 1, y05 + 1);
				g.drawLine(x0 + 4, y05 - 2, x0 + 1, y05 + 1);
				g.drawLine(x0 + 4, y05 + 4, x0 + 1, y05 + 1);
				g.drawLine(x05 - 2, y05 - 6, x05 + 2, y05 - 2);
				g.drawLine(x05 - 2, y05 - 2, x05 + 2, y05 - 6);
				g.drawLine(x05 - 3, y05 - 4, x05 + 3, y05 - 4);
			};
		}
		return S_ICON_TOLEFTCLEAN;
	}

	public final static BPVIcon TORIGHTCLEAN()
	{
		if (S_ICON_TORIGHTCLEAN == null)
		{
			S_ICON_TORIGHTCLEAN = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05 + 1, x1 - 1, y05 + 1);
				g.drawLine(x1 - 4, y05 - 2, x1 - 1, y05 + 1);
				g.drawLine(x1 - 4, y05 + 4, x1 - 1, y05 + 1);
				g.drawLine(x05 - 2, y05 - 6, x05 + 2, y05 - 2);
				g.drawLine(x05 - 2, y05 - 2, x05 + 2, y05 - 6);
				g.drawLine(x05 - 3, y05 - 4, x05 + 3, y05 - 4);
			};
		}
		return S_ICON_TORIGHTCLEAN;
	}

	public final static BPVIcon LEFTRIGHT()
	{
		if (S_ICON_LEFTRIGHT == null)
		{
			S_ICON_LEFTRIGHT = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int y05 = (y0 + y1) / 2;
				g.drawLine(x0 + 1, y05, x1 - 1, y05);
				g.drawLine(x0 + 4, y05 - 3, x0 + 1, y05);
				g.drawLine(x0 + 4, y05 + 3, x0 + 1, y05);
				g.drawLine(x1 - 4, y05 - 3, x1 - 1, y05);
				g.drawLine(x1 - 4, y05 + 3, x1 - 1, y05);
			};
		}
		return S_ICON_LEFTRIGHT;
	}

	public final static BPVIcon RELATION()
	{
		if (S_ICON_RELATION == null)
		{
			S_ICON_RELATION = (g, x0, y0, w, h) ->
			{
				int y1 = y0 + h;
				int r = w / 10;
				g.drawRect(x0 + (w / 4), y0 + 1 + r, w / 2, h / 2 - r - 1);
				g.drawLine(x0 + (w / 4), y0 + 1 + r + r, x0 + (w / 4), y1 - 2 - r);
				g.drawLine(x0 + (w / 2), y0 + 1 + r + (h / 2) - r - 1, x0 + (w / 4 * 3), y1 - 2 - r);
			};
		}
		return S_ICON_RELATION;
	}

	public final static BPVIcon LAYOUT()
	{
		if (S_ICON_LAYOUT == null)
		{
			S_ICON_LAYOUT = (g, x0, y0, w, h) ->
			{
				int y1 = y0 + h;
				g.drawLine(x0 + (w / 4), y0 + 1, x0 + (w / 4), y1 - 2);
				g.drawLine(x0 + (w / 4), y1 - 2, x0 + (w / 4 * 3), y1 - 2);
			};
		}
		return S_ICON_LAYOUT;
	}

	public final static BPVIcon SETTING()
	{
		if (S_ICON_SETTING == null)
		{
			S_ICON_SETTING = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				int t = w / 4;
				int t2 = t / 2;
				g.drawLine(x05 - t - t, y05 + t + t, x05, y05);
				g.drawLine(x05 + 1 - t2, y05 - 1 - t2, x05 + 1 + t2, y05 - 1 + t2);
				g.drawLine(x05 + 1 - t2, y05 - 1 - t2, x05 + 1 - t2, y05 - 2 - t2);
				g.drawLine(x05 + 1 - t2, y05 - 2 - t2, x05 + 1, y05 - 2 - t2 - t2);
				g.drawLine(x05 + 1 + t2, y05 - 1 + t2, x05 + 2 + t2, y05 - 1 + t2);
				g.drawLine(x05 + 2 + t2, y05 - 1 + t2, x05 + 2 + t2 + t2, y05 - 1);
			};
		}
		return S_ICON_SETTING;
	}

	public final static BPVIcon MORE()
	{
		if (S_ICON_MORE == null)
		{
			S_ICON_MORE = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;

				int x61 = w / 3;

				g.drawRect(x05 - x61 - 1, y05 - 1, 2, 2);
				g.drawRect(x05 - 1, y05 - 1, 2, 2);
				g.drawRect(x05 + x61 - 1, y05 - 1, 2, 2);
			};
		}
		return S_ICON_MORE;
	}

	public final static BPVIcon RECTSEL()
	{
		if (S_ICON_RECTSEL == null)
		{
			S_ICON_RECTSEL = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;

				g.drawLine(x0 + w - 3, y0 + h - 3, x0 + w - 3, y1 - 1);
				g.drawLine(x0 + w - 3, y0 + h - 3, x1 - 1, y0 + h - 3);

				g.setColor(UIConfigs.COLOR_TEXTQUARTER());
				g.drawRect(x0 + 1, y0 + 1, w - 4, h - 4);
			};
		}
		return S_ICON_RECTSEL;
	}

	public final static BPVIcon FIND()
	{
		if (S_ICON_FIND == null)
		{
			S_ICON_FIND = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w;
				int y1 = y0 + h;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				int x025 = (x0 + x1) / 4;

				g.drawLine(x0, y1 - 2, x05 - (w / 8) - 1, y05 + (w / 8) - 1);
				g.drawLine(x0 + 2, y1, x05 - (w / 8) + 1, y05 + (w / 8) + 1);
				g.drawLine(x0, y1 - 2, x0 + 2, y1);
				g.drawOval(x025, y0, x1 - x025 - 1, x1 - x025 - 1);
			};
		}
		return S_ICON_FIND;
	}
	
	public final static BPVIcon IMG()
	{
		if (S_ICON_IMG == null)
		{
			S_ICON_IMG = (g, x0, y0, w, h) ->
			{
				int x1 = x0 + w - 1;
				int y1 = y0 + h - 1;
				int x05 = (x0 + x1) / 2;
				int y05 = (y0 + y1) / 2;
				int y075 = y1 - ((y0 + y1) / 4);

				g.drawLine(x0, y0, x0, y1);
				g.drawLine(x0, y1, x1, y1);
				g.drawLine(x1, y0, x1, y1);
				g.drawLine(x0, y0, x1, y0);
				g.drawLine(x0, y075, x05, y05);
				g.drawLine(x05, y05, x1, y075);
			};
		}
		return S_ICON_IMG;
	}
}
