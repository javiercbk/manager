package com.company.manager.controllers.params.addressable.client;

import com.company.manager.controllers.params.addressable.AddressableSearchParams;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ClientSearchParams extends AddressableSearchParams{
	private ClientOrder order;
	private boolean fetchProviders;
	
	public static enum ClientOrder {
		AscName("name"),
		AscEmail("email"),
		DescName("-name"),
		DescEmail("-email");
		
		private final String orderName;
		
		private ClientOrder(String orderName){
			this.orderName = orderName;
		}
		
		@JsonCreator
	    public static ClientOrder forValue(String value) {
	        for(ClientOrder co : values()){
	        	if(co.orderName.equals(value)){
	        		return co;
	        	}
	        }
	        return null;
	    }

	    @JsonValue
	    public String toValue() {
	        return this.orderName;
	    }
	}
	
	public boolean isFetchProviders() {
		return fetchProviders;
	}

	public void setFetchProviders(boolean fetchProviders) {
		this.fetchProviders = fetchProviders;
	}
	
	public ClientOrder getOrder() {
		return order;
	}

	public void setOrder(ClientOrder order) {
		this.order = order;
	}
}
