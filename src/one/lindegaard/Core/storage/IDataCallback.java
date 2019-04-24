package one.lindegaard.Core.storage;

public interface IDataCallback<T>
{
	void onCompleted(T data);
	
	void onError(Throwable error);
}
