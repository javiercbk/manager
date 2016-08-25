package com.company.manager.repositories.patch.applier;

import java.util.Iterator;
import java.util.List;

import com.company.manager.controllers.exceptions.UnprocessableEntityException;
import com.company.manager.controllers.params.patch.Patch;
import com.company.manager.models.Client;
import com.company.manager.models.Provider;
import com.company.manager.repositories.ClientRepository;
import com.company.manager.repositories.ProviderRepository;
import com.google.common.collect.Lists;

public class ClientApplier extends AddressableApplier<Client>{
	private static final String PATH_PROVIDERS = "/providers";
	private final Client client;
	
	private final ProviderRepository providerRepository;

	
	public ClientApplier(Client client, ClientRepository clientRepository, ProviderRepository providerRepository) {
		super(client, clientRepository);
		this.providerRepository = providerRepository;
		this.client = client;
	}
	
	private Provider extractProviderFromPatch(Patch<Object> patch){
		Long providerId = null;
		try{
			if(patch.getValue() != null){
				providerId = Long.parseLong(patch.getValue().toString());
			}else{
				String[] splitted = patch.getPath().split("/");
				providerId = Long.parseLong(splitted[2]);
			}
		}catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
			throw new UnprocessableEntityException();
		}
		return this.providerRepository.getById(providerId);
	}

	@Override
	public void apply(Patch<Object> patch) {
		if(patch.getPath().startsWith(PATH_PROVIDERS)){
			final Provider provider = extractProviderFromPatch(patch);
			if(provider == null){
				// provider given was not found
				throw new UnprocessableEntityException();
			}
			List<Provider> providers = null;
			switch(patch.getOp()){
			case Replace:
				this.client.setProviders(Lists.newArrayList(provider));
				break;
			case Add:
				providers = this.client.getProviders();
				if(providers == null){
					providers = Lists.newArrayList();
				}
				if(!providers.contains(provider)){
					providers.add(provider);
				}
				break;
			case Remove:
				providers = this.client.getProviders();
				Iterator<Provider> iterator = providers.iterator();
				while(iterator.hasNext()){
					Provider p = iterator.next();
					if(p.getId().equals(provider.getId())){
						iterator.remove();
					}
				}
				this.client.setProviders(providers);
				// if provider is not found, then do nothing
				break;
			default:
				throw new UnprocessableEntityException();
			}
		}else{
			super.apply(patch);
		}
	}
}
