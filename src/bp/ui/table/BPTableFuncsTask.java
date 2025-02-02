package bp.ui.table;

import bp.task.BPTask;

public class BPTableFuncsTask extends BPTableFuncsBase<BPTask<?>>
{
	public BPTableFuncsTask()
	{
		m_colnames = new String[] { "Name", "Status", "Progress" };
		m_cols = new Class<?>[] { String.class, String.class, Float.class };
	}

	public String[] getColumnNames()
	{
		return m_colnames;
	}

	public Class<?>[] getColumnClasses()
	{
		return m_cols;
	}

	public Object getValue(BPTask<?> task, int row, int col)
	{
		Object rc = null;
		switch (col)
		{
			case 0:
			{
				return nvl(task.toString());
			}
			case 1:
			{
				return nvl(task.getStatus());
			}
			case 2:
			{
				return task.getProgress();
			}
		}
		return rc;
	}
}
