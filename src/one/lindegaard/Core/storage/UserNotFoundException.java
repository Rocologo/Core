package one.lindegaard.Core.storage;

public class UserNotFoundException extends DataStoreException {

	private static final long serialVersionUID = -5372745162081653416L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}
}
