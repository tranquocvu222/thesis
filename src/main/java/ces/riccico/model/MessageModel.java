package ces.riccico.model;

public class MessageModel {

	private Object data;

	private String error;

	private String message;

	private int status;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return status;
	}

	public void setStatusCode(int statusCode) {
		this.status = statusCode;
	}

}
