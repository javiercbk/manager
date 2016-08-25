package com.company.manager.repositories.patch.applier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.company.manager.controllers.exceptions.UnprocessableEntityException;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.models.Employee;
import com.company.manager.models.Office;
import com.company.manager.repositories.EmployeeRepository;
import com.company.manager.repositories.OfficeRepository;

public class EmployeeApplier extends AddressableApplier<Employee>{
	private static final String OFFICES = "/offices";
	private static final String PATH_WORKING_SINCE = "/workingSince";
	private final Employee employee;
	private final OfficeRepository officeRepository;

	public EmployeeApplier(Employee employee, EmployeeRepository employeeRepository, OfficeRepository officeRepository) {
		super(employee, employeeRepository);
		this.employee = employee;
		this.officeRepository = officeRepository;
	}

	@Override
	public void apply(Patch<Object> patch) {
		// TODO implement patch
		if(patch.getPath().equals(PATH_WORKING_SINCE)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(patch.getValue() == null){
				throw new UnprocessableEntityException();
			}
			switch(patch.getOp()){
			case Replace:
				Date parsed = null;
				try {
					parsed = sdf.parse(patch.getValue().toString());
				} catch (ParseException e) {
					throw new UnprocessableEntityException();
				}
				this.employee.setWorkingSince(parsed);
				break;
			case Test:
				String wsStr = sdf.format(this.employee.getWorkingSince());
				if(!wsStr.equals(patch.getValue())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else if(patch.getPath().equals(OFFICES)){			
			if(patch.getValue() == null){
				throw new UnprocessableEntityException();
			}
			Long id = null;
			try{
				id = Long.parseLong(patch.getValue().toString());
			}catch(NumberFormatException nfe){
				throw new UnprocessableEntityException();
			}
			switch(patch.getOp()){
			case Replace:
				Office office = officeRepository.getById(id);
				if(office == null){
					throw new UnprocessableEntityException();
				}
				this.employee.setOffice(office);
				break;
			case Test:
				if(!this.employee.getOffice().getId().toString().equals(id.toString())){
					throw new UnprocessableEntityException();
				}
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else{
			super.apply(patch);
		}
	}

}
