package one.lindegaard.Core.storage.asynch;

import java.util.LinkedHashSet;
import java.util.Set;

import one.lindegaard.Core.PlayerSettings;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.Core.storage.IDataStore;

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
