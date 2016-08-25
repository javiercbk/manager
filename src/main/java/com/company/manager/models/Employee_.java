package com.company.manager.models;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Employee.class)
public class Employee_ extends Addressable_{
	public static volatile SingularAttribute<Employee, Date> workingSince;
	public static volatile SingularAttribute<Employee, Date> lastDay;
	public static volatile SingularAttribute<Employee, Office> office;
}
