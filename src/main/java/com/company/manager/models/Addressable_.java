package com.company.manager.models;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.joda.time.DateTime;

@StaticMetamodel(Addressable.class)
public class Addressable_ {
	public static volatile SingularAttribute<Addressable, Long> id;
	public static volatile SingularAttribute<Addressable, String> name;
	public static volatile SingularAttribute<Addressable, String> email;
	public static volatile SingularAttribute<Addressable, String> phone;
	public static volatile SingularAttribute<Addressable, DateTime> creation;
}
