package com.company.manager.models;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.joda.time.DateTime;

@StaticMetamodel(Office.class)
public class Office_ {
	public static volatile SingularAttribute<Office, Long> id;
	public static volatile SingularAttribute<Office, String> location;
	public static volatile SingularAttribute<Office, DateTime> creation;
	public static volatile SingularAttribute<Office, Date> opened;
	public static volatile SingularAttribute<Office, Date> closed;
	public static volatile ListAttribute<Client, Employee> employees;
}
