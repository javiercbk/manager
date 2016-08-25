package com.company.manager.controllers.params.addressable.client;

import java.util.List;

import com.company.manager.controllers.params.addressable.AddressableProspect;

public class ClientProspect extends AddressableProspect{
	private List<Long> providers;
	
	public List<Long> getProviders() {
		return providers;
	}
	public void setProviders(List<Long> providers) {
		this.providers = providers;
	}
}
