package one.lindegaard.BagOfGoldCore.storage;

public interface IDataCallback<T>
{
	void onCompleted(T data);
	
	void onError(Throwable error);
}
