package com.company.manager.controllers.params.provider;

import javax.validation.constraints.Size;

public class ProviderProspect {
	@Size(min=1, max= 100)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
