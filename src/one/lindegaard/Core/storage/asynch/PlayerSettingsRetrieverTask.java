package one.lindegaard.Core.storage.asynch;

import java.util.HashSet;

import org.bukkit.OfflinePlayer;

import one.lindegaard.Core.BagOfGoldCore;
import one.lindegaard.Core.PlayerSettings;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.Core.storage.IDataStore;
import one.lindegaard.Core.storage.UserNotFoundException;

public class PlayerSettingsRetrieverTask implements IDataStoreTask<PlayerSettings> {

	private OfflinePlayer mPlayer;
	private HashSet<Object> mWaiting;

	public PlayerSettingsRetrieverTask(OfflinePlayer player, HashSet<Object> waiting) {
		mPlayer = player;
		mWaiting = waiting;
	}

	public PlayerSettings run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			try {
				return store.loadPlayerSettings(mPlayer);
			} catch (UserNotFoundException e) {
				BagOfGoldCore.getInstance().getMessages().debug("Insert new PlayerSettings for %s to database.",
						mPlayer.getName());
				String worldgroup = mPlayer.isOnline()
						? BagOfGoldCore.getInstance().getWorldGroupManager().getCurrentWorldGroup(mPlayer)
						: BagOfGoldCore.getInstance().getWorldGroupManager().getDefaultWorldgroup();
				PlayerSettings ps = new PlayerSettings(mPlayer, worldgroup,
						BagOfGoldCore.getInstance().getConfigManager().learningMode, false);
				try {
					store.insertPlayerSettings(ps);
				} catch (DataStoreException e1) {
					e1.printStackTrace();
				}
				return ps;
			} catch (DataStoreException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}
}
