package bp.ui.tree;

import java.awt.Component;
import java.io.EOFException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import bp.BPCore;
import bp.data.BPDataContainer;
import bp.format.BPFormatDir;
import bp.res.BPResourceByteArraySupplier;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.util.FileUtil;
import bp.util.Std;
import bp.util.ZipUtil;

public interface BPArchiveTreeFuncs extends BPTreeFuncs
{
	void setup(BPDataContainer con);

	public static class BPZipFileTreeFuncs implements BPArchiveTreeFuncs
	{
		protected Map<String, List<Object>> m_entrymap = new HashMap<String, List<Object>>();
		protected List<Object> m_roots = new ArrayList<Object>();
		protected int m_channelid;
		protected BPPathTreeNodeActions m_actptree;
		protected WeakReference<BPDataContainer> m_conref;

		public BPZipFileTreeFuncs(int channelid)
		{
			m_channelid = channelid;
			m_actptree = new BPPathTreeNodeActions();
		}

		public void setChannelID(int channelid)
		{
		}

		public List<?> getRoots()
		{
			return m_roots;
		}

		protected void sort(List<Object> nodes)
		{
			nodes.sort((a, b) ->
			{

				boolean isdir1 = isDir(a);
				boolean isdir2 = isDir(b);
				if (isdir1 == isdir2)
				{
					return a.toString().compareToIgnoreCase(b.toString());
				}
				return isdir1 ? -1 : 1;
			});
		}

		protected boolean isDir(Object node)
		{
			if (node == null)
				return true;
			if (node instanceof String)
				return true;
			return ((ZipEntry) node).isDirectory();
		}

		public List<?> getChildren(BPTreeNode node, boolean isdelta)
		{
			Object obj = node.getUserObject();
			String name;
			if (obj instanceof String)
				name = (String) obj;
			else
				name = ((ZipEntry) obj).getName();
			if (name.length() > 1 && name.endsWith("/"))
				name = name.substring(0, name.length() - 1);
			List<Object> rc = new ArrayList<Object>();
			List<Object> l = m_entrymap.get(name);
			if (l != null)
			{
				rc.addAll(l);
			}
			return rc;
		}

		public boolean isLeaf(BPTreeNode node)
		{
			Object obj = node.getUserObject();
			if (obj == null)
				return false;
			if (obj instanceof String)
				return false;
			return !((ZipEntry) node.getUserObject()).isDirectory();
		}

		protected List<Object> getEntrys(ZipInputStream zis)
		{
			List<Object> roots = new ArrayList<Object>();
			List<ZipEntry> all = getAllEntries(zis);
			List<String> rootkeys = new ArrayList<String>();
			for (ZipEntry e : all)
			{
				String name = e.getName();
				if (name.length() > 1 && name.endsWith("/"))
					name = name.substring(0, name.length() - 1);
				int vi = name.lastIndexOf("/");
				if (vi > -1)
				{
					String path = name.substring(0, vi);
					List<Object> l = m_entrymap.get(path);
					if (l == null)
					{
						l = new ArrayList<Object>();
						m_entrymap.put(path, l);
					}
					l.add(e);
				}
				else
				{
					roots.add(e);
					rootkeys.add(name);
				}
			}
			List<String> fkeys = fixEntryTree();
			for (String fkey : fkeys)
			{
				if (!rootkeys.contains(fkey))
				{
					roots.add(fkey);
				}
			}
			sort(roots);
			return roots;
		}

		protected List<String> fixEntryTree()
		{
			List<String> dirs = new ArrayList<String>(m_entrymap.keySet());
			List<String> newdirs = new ArrayList<String>();
			List<String> rootdirs = new ArrayList<String>();
			for (String dir : dirs)
			{
				String key = dir;
				int vi = key.lastIndexOf("/");
				while (vi > 0)
				{
					String par = key.substring(0, vi);
					List<Object> l = m_entrymap.get(par);
					if (l == null)
					{
						l = new ArrayList<Object>();
						m_entrymap.put(par, l);
						l.add(key);
					}
					else if (newdirs.contains(par))
					{
						l.add(key);
					}
					key = par;
					vi = key.lastIndexOf("/");
				}
				if (!rootdirs.contains(key))
					rootdirs.add(key);
			}
			return rootdirs;
		}

		protected List<ZipEntry> getAllEntries(ZipInputStream zis)
		{
			List<ZipEntry> rc = new ArrayList<ZipEntry>();
			try
			{
				ZipEntry e = zis.getNextEntry();
				while (e != null)
				{
					rc.add(e);
					e = zis.getNextEntry();
				}
			}
			catch (EOFException e)
			{

			}
			catch (IOException e)
			{
				Std.err(e);
			}
			return rc;
		}

		public void setup(BPDataContainer con)
		{
			m_conref = new WeakReference<BPDataContainer>(con);
			con.useInputStream((in) ->
			{
				try (ZipInputStream zis = new ZipInputStream(in))
				{
					m_roots = getEntrys(zis);
				}
				catch (IOException e)
				{
					Std.err(e);
				}
				return null;
			});
		}

		public List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
		{
			List<Action> rc = new ArrayList<Action>();
			ZipEntry entry = (ZipEntry) node.getUserObject();
			if (!entry.isDirectory())
			{
				String ename = entry.getName();
				String ext = entry.isDirectory() ? BPFormatDir.FORMAT_DIR : FileUtil.getExt(ename);
				EntryHolder eh = new EntryHolder(m_conref.get(), entry);
				BPResourceByteArraySupplier res = new BPResourceByteArraySupplier(eh, null, ext, BPCore.genID(BPCore.getFileContext()), ename, true);
				rc.add(m_actptree.getOpenFileAction(tree, res, m_channelid));
				rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
			}
			return rc;
		}
	}

	public static class EntryHolder implements Supplier<byte[]>
	{
		protected WeakReference<BPDataContainer> m_conref;
		protected ZipEntry m_entry;

		public EntryHolder(BPDataContainer con, ZipEntry entry)
		{
			m_conref = new WeakReference<BPDataContainer>(con);
			m_entry = entry;
		}

		public byte[] get()
		{
			return m_conref.get().useInputStream((in) ->
			{
				return ZipUtil.readEntry(in, m_entry);
			});
		}
	}

	public static class BPTreeCellRendererZip extends DefaultTreeCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1920621174539501544L;

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			Object v = value;
			if (v instanceof BPTreeNode)
				v = ((BPTreeNode) v).getUserObject();
			if (v != null)
			{
				if (v instanceof ZipEntry)
				{
					String name = ((ZipEntry) v).getName();
					if (name.endsWith("/"))
						name = name.substring(0, name.length() - 1);
					int vi = name.lastIndexOf("/");
					if (vi > -1 && vi < name.length() - 1)
					{
						name = name.substring(vi + 1);
					}
					v = name;
				}
			}
			return super.getTreeCellRendererComponent(tree, v, selected, expanded, leaf, row, hasFocus);
		}
	}
}
