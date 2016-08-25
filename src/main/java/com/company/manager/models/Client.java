package com.company.manager.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Table(name="clients")
@Entity
public class Client extends Addressable{
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinTable(name="providers_clients",
			joinColumns=@JoinColumn(name="client_id", referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="provider_id", referencedColumnName="id"))
	private List<Provider> providers;
	
	public Client(){
		super();
		providers = Lists.newArrayList();
	}

	public List<Provider> getProviders() {
		return providers;
	}

	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}
}
