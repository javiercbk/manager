package com.company.manager.repositories;

import java.util.List;

import com.company.manager.models.Provider;

public interface ProviderRepository extends GenericRepository<Provider, Long>{
	public Provider search(String name);
	public List<Provider> searchSimilar(String name);
	public List<Provider> all();
}
