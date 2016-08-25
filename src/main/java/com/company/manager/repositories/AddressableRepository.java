package com.company.manager.repositories;

import com.company.manager.models.Addressable;

public interface AddressableRepository<T extends Addressable> extends GenericRepository<T, Long>{
	public T searchPrevious(String email);
}
