package com.xforceplus.wapp.modules.rednotification.model;

public class Response<T> {
    public static final Integer OK = 1;
    public static final Integer Fail = 0;
    private Integer code;
    private String message;
    private T result;

    public Response() {
    }

	public static <T> Response<T> ok(String message) {
		Response<T> response = new Response<>();
		response.setCode(OK);
		response.setMessage(message);
		return response;
	}

	public static <T> Response<T> ok(String message, T result) {
		Response<T> response = new Response<>();
		response.setCode(OK);
		response.setMessage(message);
		response.result = result;
		return response;
	}

	public static <T> Response<T> failed(String message) {
		Response<T> response = new Response<>();
		response.setCode(Fail);
		response.setMessage(message);
		return response;
	}

    @SuppressWarnings("rawtypes")
    public static Response from(Integer code, String message) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Response<T> from(Integer code, String message, T result) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setResult(result);
        return response;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String toString() {
        return "Response{code=" + this.code + ", message='" + this.message + '\'' + ", result=" + this.result + '}';
    }
}

