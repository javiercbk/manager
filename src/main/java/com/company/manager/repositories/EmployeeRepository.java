package com.company.manager.repositories;

import java.util.List;

import com.company.manager.controllers.params.addressable.employee.EmployeeSearchParams;
import com.company.manager.models.Employee;

public interface EmployeeRepository extends AddressableRepository<Employee>{
	public List<Employee> search(EmployeeSearchParams params);
}
