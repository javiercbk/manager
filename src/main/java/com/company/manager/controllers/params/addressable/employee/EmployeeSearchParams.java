package com.company.manager.controllers.params.addressable.employee;

import java.util.Date;

import com.company.manager.controllers.params.addressable.AddressableSearchParams;
import com.company.manager.controllers.params.office.OfficeSearchParams;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class EmployeeSearchParams extends AddressableSearchParams{
	private EmployeeOrder order;
	private Date fromWorkingSince;
	private Date toWorkingSince;
	private OfficeSearchParams office;
	private boolean fetchOffice;
	
	
	public static enum EmployeeOrder {
		AscName("name"),
		AscEmail("email"),
		DescName("-name"),
		DescEmail("-email"),
		AscWorkingSince("workingSince"),
		DescWorkingSince("-workingSince");
		
		private final String orderName;
		
		private EmployeeOrder(String orderName){
			this.orderName = orderName;
		}
		
		@JsonCreator
	    public static EmployeeOrder forValue(String value) {
	        for(EmployeeOrder eo : values()){
	        	if(eo.orderName.equals(value)){
	        		return eo;
	        	}
	        }
	        return null;
	    }

	    @JsonValue
	    public String toValue() {
	        return this.orderName;
	    }
	}
	
	public Date getFromWorkingSince() {
		return fromWorkingSince;
	}
	public void setFromWorkingSince(Date fromWorkingSince) {
		this.fromWorkingSince = fromWorkingSince;
	}
	public Date getToWorkingSince() {
		return toWorkingSince;
	}
	public void setToWorkingSince(Date toWorkingSince) {
		this.toWorkingSince = toWorkingSince;
	}
	public OfficeSearchParams getOffice() {
		return office;
	}
	public void setOffice(OfficeSearchParams office) {
		this.office = office;
	}
	public EmployeeOrder getOrder() {
		return order;
	}
	public void setOrder(EmployeeOrder order) {
		this.order = order;
	}
	public boolean isFetchOffice() {
		return fetchOffice;
	}
	public void setFetchOffice(boolean fetchOffice) {
		this.fetchOffice = fetchOffice;
	}
}
