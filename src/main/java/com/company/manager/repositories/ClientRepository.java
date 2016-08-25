package com.company.manager.repositories;

import java.util.List;

import com.company.manager.controllers.params.addressable.client.ClientSearchParams;
import com.company.manager.models.Client;

public interface ClientRepository extends AddressableRepository<Client> {
	public List<Client> search(ClientSearchParams params);
}
