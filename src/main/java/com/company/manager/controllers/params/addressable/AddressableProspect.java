package com.company.manager.controllers.params.addressable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public abstract class AddressableProspect {
	@NotNull @NotEmpty @Size(max=80)
	protected String name;
	@Email @NotNull @Size(max=254)
	protected String email;
	@Size(max=50)
	protected String phone;
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
