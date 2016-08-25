package com.company.manager.controllers.params.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Patch<T> {
	private OperationType op;
	private String from;
	private String path;
	private T value;
	
	public Patch(){}
	
	@JsonCreator
    public static <T> Patch<T> createPatch(@JsonProperty("op") OperationType op,
    		@JsonProperty("from") String from,
    		@JsonProperty("path") String path,
    		@JsonProperty("value") T value) {
		Patch<T> patch = new Patch<T>();
		patch.setFrom(from);
		patch.setPath(path);
		patch.setOp(op);
		patch.setValue(value);
		return patch;
    }
	
	public OperationType getOp() {
		return op;
	}
	public void setOp(OperationType op) {
		this.op = op;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
