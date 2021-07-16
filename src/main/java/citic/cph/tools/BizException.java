package citic.cph.tools;

/**
 * 业务异常
 *
 * @author SPL
 * @since 2020-02-04 10:25
 */
public class BizException extends RuntimeException {

	private int code;
	private String message;


	public BizException(String message) {
		this.code = 500;
		this.message = message;
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	public BizException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}