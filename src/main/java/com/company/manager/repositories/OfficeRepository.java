package com.company.manager.repositories;

import java.util.List;

import com.company.manager.models.Office;

public interface OfficeRepository extends GenericRepository<Office, Long>{
	public Office search(String location);
	public List<Office> searchSimilar(String location);
	public List<Office> all();
}
