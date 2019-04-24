package one.lindegaard.Core.storage.asynch;

import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.Core.storage.IDataStore;

public interface IDataStoreTask<T>
{
	public T run(IDataStore store) throws DataStoreException;
	
	public boolean readOnly();
}
