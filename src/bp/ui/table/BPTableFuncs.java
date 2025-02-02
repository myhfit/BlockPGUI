package bp.ui.table;

import java.util.List;

import javax.swing.Action;

import bp.ui.scomp.BPTable;

public interface BPTableFuncs<T>
{
	String[] getColumnNames();

	String getColumnName(int col);

	Class<?>[] getColumnClasses();

	Class<?> getColumnClass(int col);

	Object getValue(T o, int row, int col);

	boolean isEditable(T o, int row, int col);

	void setValue(Object v, T o, int row, int col);

	List<Action> getActions(BPTable<T> table, List<T> datas, int[] rows, int r, int c);

	default Action getOpenAction(BPTable<T> table, T data, int row, int col)
	{
		return null;
	}

	default List<Action> getEmptySelectionActions(BPTable<T> table)
	{
		return null;
	}

	default String getTooltip(T data, int row, int col)
	{
		return null;
	}

	default boolean allowTooltip()
	{
		return false;
	}

	default boolean allowClickCell()
	{
		return false;
	}

	default void clickCell(T data, int row, int col)
	{
	}
}
