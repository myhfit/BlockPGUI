package bp.ui.parallel;

public interface BPSyncGUI
{
	default void startSyncStatus()
	{
		BPSyncGUIController c = getSyncStatusController();
		if (c != null)
			c.startSync();
	}

	default void stopSyncStatus()
	{
		BPSyncGUIController c = getSyncStatusController();
		if (c != null)
			c.stopSync();
	}

	BPSyncGUIController getSyncStatusController();

	default BPSyncGUIController getSyncActionController()
	{
		return null;
	}
}
