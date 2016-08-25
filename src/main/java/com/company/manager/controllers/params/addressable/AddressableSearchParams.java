package com.company.manager.controllers.params.addressable;

import javax.validation.constraints.Size;

public abstract class AddressableSearchParams {
	protected Long id;
	protected Integer limit;
	@Size(min=1, max=80)
	protected String name;
	@Size(min=1, max=254)
	protected String email;

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
