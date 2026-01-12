package bp.ui.table;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import bp.format.BPFormatDir;
import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceHolder;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPTable;
import bp.util.FileUtil;

public class BPTableFuncsResourceFiles extends BPTableFuncsResource
{
	protected WeakReference<BPResource> m_base = null;

	public BPTableFuncsResourceFiles()
	{
		m_colnames = new String[] { "Name", "Type", "Size", "Last Modify" };
		m_cols = new Class<?>[] { String.class, String.class, Long.class, Long.class };
	}

	public Object getValue(BPResource res, int row, int col)
	{
		switch (col)
		{
			case 1:
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
				break;
			}
			case 2:
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
				break;
			}
			case 3:
			{
				if (res.isFileSystem())
				{
					BPResourceFileSystem fres = (BPResourceFileSystem) res;
					if (fres.isFile() || fres.isDirectory())
						return fres.getLastModified();
				}
				break;
			}
			default:
				return super.getValue(res, row, col);
		}
		return "";
	}

	public void setBaseResource(BPResource res)
	{
		m_base = new WeakReference<BPResource>(res);
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
