package com.company.manager.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public class TestingRepository {
	@PersistenceContext(unitName="test")
	private EntityManager entityManager;
	
	public void wipeTablesData(){
		StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("wipe_tables_data");
		storedProcedure.execute();
	}
}
