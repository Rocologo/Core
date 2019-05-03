package one.lindegaard.BagOfGoldCore.storage.asynch;

import one.lindegaard.BagOfGoldCore.storage.DataStoreException;
import one.lindegaard.BagOfGoldCore.storage.IDataStore;

public interface IDataStoreTask<T>
{
	public T run(IDataStore store) throws DataStoreException;
	
	public boolean readOnly();
}
