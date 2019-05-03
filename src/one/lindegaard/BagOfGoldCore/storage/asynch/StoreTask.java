package one.lindegaard.BagOfGoldCore.storage.asynch;

import java.util.LinkedHashSet;
import java.util.Set;

import one.lindegaard.BagOfGoldCore.PlayerSettings;
import one.lindegaard.BagOfGoldCore.storage.DataStoreException;
import one.lindegaard.BagOfGoldCore.storage.IDataStore;

public class StoreTask implements IDataStoreTask<Void> {
	private LinkedHashSet<PlayerSettings> mWaitingPlayerSettings = new LinkedHashSet<PlayerSettings>();

	public StoreTask(Set<Object> waiting) {
		synchronized (waiting) {
			mWaitingPlayerSettings.clear();

			for (Object obj : waiting) {
				if (obj instanceof PlayerSettings)
					mWaitingPlayerSettings.add((PlayerSettings) obj);
			}

			waiting.clear();
		}
	}

	@Override
	public Void run(IDataStore store) throws DataStoreException {
		if (!mWaitingPlayerSettings.isEmpty())
			store.savePlayerSettings(mWaitingPlayerSettings, true);

		return null;
	}

	@Override
	public boolean readOnly() {
		return false;
	}
}
