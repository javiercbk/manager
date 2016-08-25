package com.company.manager.controllers;

import java.util.List;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.manager.controllers.exceptions.DuplicatedResourceException;
import com.company.manager.controllers.exceptions.ResourceNotFoundException;
import com.company.manager.controllers.params.provider.ProviderProspect;
import com.company.manager.models.Provider;
import com.company.manager.repositories.ProviderRepository;

@RestController
@RequestMapping("/api/providers")
@Transactional
public class ProviderController {
	private final ProviderRepository providerRepository;
	
	@Autowired
	public ProviderController(ProviderRepository providerRepository){
		this.providerRepository = providerRepository;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public List<Provider> allProviders(){
		return providerRepository.all();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Provider postNewProvider(@RequestBody @Valid ProviderProspect providerProspect){
		//Inferred business rule: there cannot be two providers with the same name.
		Provider previousProvider = providerRepository.search(providerProspect.getName());
		if(previousProvider != null){
			throw new DuplicatedResourceException();
		}
		Provider provider = new Provider();
		provider.setName(providerProspect.getName());
		provider.setCreation(new DateTime().toDateTime(DateTimeZone.UTC));
		this.providerRepository.persist(provider);
		return provider;
	}
	
	@RequestMapping(path="/{providerId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Provider deleteProvider(@PathVariable Long providerId){
		Provider providerFound = this.providerRepository.getById(providerId);
		if(providerFound == null){
			throw new ResourceNotFoundException();
		}
		this.providerRepository.remove(providerId);
		return providerFound;
	}
	
	@RequestMapping(path="/{providerId}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void patchClient(@PathVariable Long providerId, @RequestBody @Valid ProviderProspect providerProspect){
		Provider providerFound = this.providerRepository.getById(providerId);
		if(providerFound == null){
			throw new ResourceNotFoundException();
		}
		providerFound.setName(providerProspect.getName());
		this.providerRepository.persist(providerFound);
	}
}
