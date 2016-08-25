package com.company.manager.controllers.params.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OperationType {
	Test,
    Remove,
    Add,
    Replace,
    Move,
    Copy;
	
	@JsonCreator
    public static OperationType forValue(String value) {
        for(OperationType ot : values()){
        	if(ot.name().toLowerCase().equals(value)){
        		return ot;
        	}
        }
        return null;
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
