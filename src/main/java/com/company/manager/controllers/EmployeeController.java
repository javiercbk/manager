package com.company.manager.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.manager.controllers.exceptions.DuplicatedResourceException;
import com.company.manager.controllers.exceptions.ResourceNotFoundException;
import com.company.manager.controllers.exceptions.UnprocessableEntityException;
import com.company.manager.controllers.params.addressable.employee.EmployeeProspect;
import com.company.manager.controllers.params.addressable.employee.EmployeeSearchParams;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.controllers.params.patch.Patches;
import com.company.manager.models.Employee;
import com.company.manager.models.Office;
import com.company.manager.repositories.EmployeeRepository;
import com.company.manager.repositories.OfficeRepository;
import com.company.manager.repositories.patch.applier.EmployeeApplier;

@RestController
@RequestMapping("/api/employees")
@Transactional
public class EmployeeController {
	private final EmployeeRepository employeeRepository;
	private final OfficeRepository officeRepository;
	
	@Autowired
	public EmployeeController(EmployeeRepository employeeRepository, OfficeRepository officeRepository){
		this.employeeRepository = employeeRepository;
		this.officeRepository = officeRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<Employee> searchEmployees(@Valid @ModelAttribute EmployeeSearchParams employeeParams){
		if(employeeParams == null){
			employeeParams = new EmployeeSearchParams();
		}
		employeeParams.setFetchOffice(true);
		return employeeRepository.search(employeeParams);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Employee postNewEmployee(@RequestBody @Valid @NotNull EmployeeProspect prospectEmployee){
		//Inferred business rule: there cannot be two employees with the same name and email.
		Employee previousEmployee = employeeRepository.searchPrevious(prospectEmployee.getEmail());
		if(previousEmployee != null){
			throw new DuplicatedResourceException();
		}
		Office office =  this.officeRepository.getById(prospectEmployee.getOffice());
		if(office == null){
			throw new UnprocessableEntityException();
		}
		Employee newEmployee = new Employee();
		newEmployee.setEmail(prospectEmployee.getEmail());
		newEmployee.setName(prospectEmployee.getName());
		newEmployee.setPhone(prospectEmployee.getPhone());
		// assuming working since 
		newEmployee.setWorkingSince(prospectEmployee.getWorkingSince());
		newEmployee.setOffice(office);
		this.employeeRepository.persist(newEmployee);
		return newEmployee;
	}
	
	@RequestMapping(path="/{employeeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Employee deleteEmployee(@PathVariable Long employeeId){
		Employee employeeFound = this.employeeRepository.getById(employeeId);
		if(employeeFound == null){
			throw new ResourceNotFoundException();
		}
		this.employeeRepository.remove(employeeFound);
		//avoid lazy load exception on sending response.
		employeeFound.setOffice(null);
		return employeeFound;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(path="/{employeeId}", method = RequestMethod.PATCH)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void patchEmployee(@PathVariable Long employeeId, @RequestBody @Valid Patches patches){
		EmployeeSearchParams employeeSearchParam = new EmployeeSearchParams();
		employeeSearchParam.setId(employeeId);
		employeeSearchParam.setFetchOffice(true);
		List<Employee> employees = this.employeeRepository.search(employeeSearchParam);
		if(employees.isEmpty()){
			throw new ResourceNotFoundException();
		}
		Employee employeeToUpdate = employees.get(0);
		this.employeeRepository.detach(employeeToUpdate);
		EmployeeApplier employeeApplier = new EmployeeApplier(employeeToUpdate, employeeRepository, officeRepository);
		for(Patch patch : patches.getPatches()){
			employeeApplier.apply(patch);
		}
		this.employeeRepository.merge(employeeToUpdate);
	}
}
