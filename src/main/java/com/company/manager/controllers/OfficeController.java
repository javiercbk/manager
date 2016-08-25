package com.company.manager.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.manager.controllers.exceptions.ResourceNotFoundException;
import com.company.manager.controllers.params.office.OfficeProspect;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.controllers.params.patch.Patches;
import com.company.manager.models.Office;
import com.company.manager.repositories.OfficeRepository;
import com.company.manager.repositories.patch.applier.OfficeApplier;

@RestController
@RequestMapping("/api/offices")
@Transactional
public class OfficeController {
	private final OfficeRepository officeRepository;
	
	@Autowired
	public OfficeController(OfficeRepository officeRepository){
		this.officeRepository = officeRepository;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public List<Office> getOffices(@RequestParam(required=false) String location){
		return this.officeRepository.searchSimilar(location);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Office postNewOffice(@RequestBody @NotNull @NotEmpty OfficeProspect officeProspect){
		//Inferred business rule: there cannot be two providers with the same name.
		Office office = new Office();
		office.setLocation(officeProspect.getLocation());
		office.setOpened(officeProspect.getOpened());
		office.setClosed(officeProspect.getClosed());
		office.setCreation(new DateTime().toDateTime(DateTimeZone.UTC));
		this.officeRepository.persist(office);
		return office;
	}
	
	@RequestMapping(path="/{officeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Office deleteProvider(@PathVariable Long officeId){
		Office officeFound = this.officeRepository.getById(officeId);
		if(officeFound == null){
			throw new ResourceNotFoundException();
		}
		this.officeRepository.remove(officeFound);
		return officeFound;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(path="/{officeId}", method = RequestMethod.PATCH)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void patchClient(@PathVariable Long officeId, @RequestBody @Valid Patches patches){
		Office officeToUpdate = this.officeRepository.getById(officeId);
		if(officeToUpdate == null){
			throw new ResourceNotFoundException();
		}
		this.officeRepository.detach(officeToUpdate);
		OfficeApplier officeApplier = new OfficeApplier(officeToUpdate, this.officeRepository);
		for(Patch patch : patches.getPatches()){
			officeApplier.apply(patch);
		}
		this.officeRepository.merge(officeToUpdate);
	}
}
