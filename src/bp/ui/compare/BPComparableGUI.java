package bp.ui.compare;

import bp.compare.BPDataComparator;
import bp.compare.BPDataComparator.BPDataCompareResult;

public interface BPComparableGUI<D, R extends BPDataCompareResult>
{
	BPDataComparator<D, R> getComparator();

	default boolean canCompareInUI()
	{
		return false;
	}

	default void setCompareResult(R result)
	{
	}
}
