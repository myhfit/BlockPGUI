package bp.ui.parallel;

import bp.event.BPEventUI;

public interface BPSyncGUIController
{
	void startSync();

	void stopSync();

	void setChannelID(int channelid);

	boolean checkSync();

	boolean checkSyncAndNoBlock();

	void blockSync(Runnable seg);

	void trigger(BPEventUI event);

	void clearResource();
}