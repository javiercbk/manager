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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.manager.controllers.exceptions.DuplicatedResourceException;
import com.company.manager.controllers.exceptions.ResourceNotFoundException;
import com.company.manager.controllers.exceptions.UnprocessableEntityException;
import com.company.manager.controllers.params.addressable.client.ClientProspect;
import com.company.manager.controllers.params.addressable.client.ClientSearchParams;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.controllers.params.patch.Patches;
import com.company.manager.models.Client;
import com.company.manager.models.Provider;
import com.company.manager.repositories.ClientRepository;
import com.company.manager.repositories.ProviderRepository;
import com.company.manager.repositories.patch.applier.ClientApplier;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/api/clients")
@Transactional
public class ClientController {
	private final ClientRepository clientRepository;
	private final ProviderRepository providerRepository;
	
	@Autowired
	public ClientController(ClientRepository clientRepository, ProviderRepository providerRepository){
		this.clientRepository = clientRepository;
		this.providerRepository = providerRepository;
	}

	@RequestMapping(method = RequestMethod.GET, produces="application/json")
	public  List<Client> searchClients(@Valid @ModelAttribute ClientSearchParams clientSearchParams){
		if(clientSearchParams == null){
			clientSearchParams = new ClientSearchParams();
		}
		clientSearchParams.setFetchProviders(true);
		return clientRepository.search(clientSearchParams);
	}
	
	@RequestMapping(value="/{clientId}", method = RequestMethod.GET, produces="application/json")
	public  Client getClient(@PathVariable Long clientId){
		ClientSearchParams clientSearchParams = new ClientSearchParams();
		clientSearchParams.setId(clientId);
		clientSearchParams.setFetchProviders(true);
		List<Client> clients = clientRepository.search(clientSearchParams);
		if(clients.isEmpty()){
			throw new ResourceNotFoundException();
		}
		return clients.get(0);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces="application/json")
	public Client postNewClient(@RequestBody @Valid @NotNull ClientProspect prospectClient){
		//Inferred business rule: there cannot be two clients with the same name and email.
		Client previousClient = clientRepository.searchPrevious(prospectClient.getEmail());
		if(previousClient != null){
			throw new DuplicatedResourceException();
		}
		Client newClient = new Client();
		newClient.setEmail(prospectClient.getEmail());
		newClient.setName(prospectClient.getName());
		newClient.setPhone(prospectClient.getPhone());
		if(prospectClient.getProviders() != null && !prospectClient.getProviders().isEmpty()){
			newClient.setProviders(Lists.transform(prospectClient.getProviders(), new Function<Long, Provider>(){
				@Override
				public Provider apply(Long providerId) {
					//I could use a reference but Hibernate will try to insert after the controller's return
					//and the exception handling would be "messy"
					Provider provider = providerRepository.getById(providerId);
					if(provider == null){
						throw new UnprocessableEntityException();
					}
					return provider;
				}
			}));
		}
		this.clientRepository.persist(newClient);
		return newClient;
	}
	
	@RequestMapping(value="/{clientId}", method = RequestMethod.DELETE, produces="application/json")
	public Client deleteClient(@PathVariable Long clientId){
		Client clientFound = this.clientRepository.getById(clientId);
		if(clientFound == null){
			throw new ResourceNotFoundException();
		}
		this.clientRepository.remove(clientFound);
		//avoid lazy load exception on sending response.
		clientFound.setProviders(null);
		return clientFound;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/{clientId}", method = RequestMethod.PATCH)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void patchClient(@PathVariable Long clientId,
			@RequestBody @Valid Patches patches){
		ClientSearchParams clientSearchParams = new ClientSearchParams();
		clientSearchParams.setId(clientId);
		clientSearchParams.setFetchProviders(true);
		List<Client> clients = this.clientRepository.search(clientSearchParams);
		if(clients.isEmpty()){
			throw new ResourceNotFoundException();
		}
		Client clientToUpdate = clients.get(0);
		this.clientRepository.detach(clientToUpdate);
		ClientApplier clientApplier = new ClientApplier(clientToUpdate, clientRepository, providerRepository);
		for(Patch<Object> patch: patches.getPatches()){
			clientApplier.apply(patch);
		}
		this.clientRepository.merge(clientToUpdate);
	}
}
