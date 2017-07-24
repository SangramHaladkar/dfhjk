package ism.android.utils;

public class ServerException extends Exception
{
	private static final long serialVersionUID = 1L;
	String errorMessage;

	public ServerException()
	{
		super();
		errorMessage = "unknown";
	}

	public ServerException(String err)
	{
		super(err);
		errorMessage = err;
	}

	public String getError()
	{
		return errorMessage;
	}
}
