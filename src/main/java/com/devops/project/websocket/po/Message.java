package com.devops.project.websocket.po;

public class Message<T> {

    private String type;
    private T data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
