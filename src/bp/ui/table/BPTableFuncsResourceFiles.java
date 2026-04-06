package bp.ui.table;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import bp.format.BPFormatDir;
import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleHelpers;
import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceHolder;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPTable;
import bp.util.FileUtil;

public class BPTableFuncsResourceFiles extends BPTableFuncsResource
{
	protected WeakReference<BPResource> m_base;
	protected String m_basepath;

	public BPTableFuncsResourceFiles()
	{
		initColumns();
	}

	protected void initColumns()
	{
		m_colnames = new String[] { "Name", "Path", "Type", "Size", "Last Modify" };
		m_collabels = new String[] { BPLocaleHelpers.getValue(BPLocaleConstCC.NAME), BPLocaleHelpers.getValue(BPLocaleConstCC.PATH), BPLocaleHelpers.getValue(BPLocaleConstCC.TYPE), BPLocaleHelpers.getValue(BPLocaleConstCC.SIZE),
				BPLocaleHelpers.getValue(BPLocaleConstCC.LAST_MODIFIED) };
		m_cols = new Class<?>[] { String.class, String.class, String.class, Long.class, Long.class };
	}

	public Object getValue(BPResource res, int row, int col)
	{
		switch (col)
		{
			case 1:
				return getRelativePath(res);
			case 2:
				return getResourceType(res);
			case 3:
				return getResourceSize(res);
			case 4:
				return getResourceLastModified(res);
			default:
				return super.getValue(res, row, col);
		}
	}

	protected String getRelativePath(BPResource res)
	{
		if (res.isFileSystem())
		{
			String fullpath = ((BPResourceFileSystem) res).getFileFullName();
			String name = res.getName();
			if (fullpath.startsWith(m_basepath))
				return fullpath.substring(m_basepath.length() + 1, fullpath.length() - name.length());
			else
				return fullpath.substring(0, fullpath.length() - name.length());
		}
		return null;
	}

	protected Object getResourceLastModified(BPResource res)
	{
		if (res.isFileSystem())
		{
			BPResourceFileSystem fres = (BPResourceFileSystem) res;
			if (fres.isFile() || fres.isDirectory())
				return fres.getLastModified();
		}
		return null;
	}

	protected Object getResourceSize(BPResource res)
	{

		if (res.isFileSystem())
		{
			BPResourceFileSystem fres = (BPResourceFileSystem) res;
			if (fres.isFile())
			{
				BPResourceFile f = (BPResourceFile) fres;
				return f.getSize();
			}
			else
			{
				return null;
			}
		}
		else if (res.isVirtual() && res instanceof BPResourceHolder)
		{
			BPResourceHolder hres = (BPResourceHolder) res;
			byte[] bs = hres.getData();
			if (bs != null)
			{
				return bs.length;
			}
			else
			{
				return null;
			}
		}
		return null;
	}

	protected String getResourceType(BPResource res)
	{
		if (res.isFileSystem())
		{
			BPResourceFileSystem fres = (BPResourceFileSystem) res;
			if (fres.isFile())
			{
				return FileUtil.getExt(res.getName());
			}
			else if (fres.isDirectory())
			{
				return BPFormatDir.FORMAT_DIR;
			}
		}
		else
		{
			return FileUtil.getExt(res.getName());
		}
		return null;
	}

	public void setBaseResource(BPResource res)
	{
		m_base = new WeakReference<BPResource>(res);
		m_basepath = res.isFileSystem() ? ((BPResourceFileSystem) res).getFileFullName() : "";
	}

	public BPResource getBaseResource()
	{
		return m_base.get();
	}

	public List<Action> getActions(BPTable<BPResource> table, List<BPResource> datas, int[] rows, int r, int c)
	{
		List<Action> rc = new ArrayList<Action>();
		if (datas.size() > 0)
		{
			BPResource[] dataarr = datas.toArray(new BPResource[datas.size()]);
			rc.add(m_acts.getNewFileAction(m_base.get(), m_channelid));
			rc.add(m_acts.getOpenFileAction(dataarr, m_channelid));
			rc.add(m_acts.getOpenFileAsAction(dataarr, m_channelid));
			rc.add(m_acts.getOpenFileExternalAction(dataarr, m_channelid));
			rc.add(m_acts.getOpenFileWithToolAction(dataarr, m_channelid));
			rc.add(BPAction.separator());
			rc.add(m_acts.getDeleteResourcesAction(dataarr, m_channelid));
			rc.add(m_acts.getRenameResAction(datas.get(0), m_channelid));
			rc.add(BPAction.separator());
			rc.add(m_acts.getPropertyAction(dataarr, m_channelid));
		}
		return rc;
	}
}
