package com.company.manager.models;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.joda.time.DateTime;

@StaticMetamodel(Provider.class)
public class Provider_ {
	public static volatile SingularAttribute<Provider, Long> id;
	public static volatile SingularAttribute<Provider, String> name;
	public static volatile SingularAttribute<Provider, DateTime> creation;
	public static volatile ListAttribute<Provider, Client> clients;
}
