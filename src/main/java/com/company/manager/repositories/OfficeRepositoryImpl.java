package com.company.manager.repositories;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.company.manager.models.Office;
import com.company.manager.models.Office_;

@Repository
@Transactional(readOnly = false)
public class OfficeRepositoryImpl extends AbstractRepositoryImpl<Office, Long> implements OfficeRepository {
	
	public OfficeRepositoryImpl(){
		super();
	}
	
	@Override
	public Office search(String location) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Office> query = criteriaBuilder.createQuery(Office.class);
		Root<Office> officeRoot = query.from(Office.class);
		query.where(criteriaBuilder.equal(officeRoot.get(Office_.location), location));
		try{
			return this.entityManager.createQuery(query).setMaxResults(1).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<Office> searchSimilar(String location) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Office> query = criteriaBuilder.createQuery(Office.class);
		Root<Office> officeRoot = query.from(Office.class);
		if(location != null && location.trim().length() > 0){
			String escapedLocation = super.escapeString(location.trim());
			query.where(criteriaBuilder.like(officeRoot.get(Office_.location), escapedLocation + "%", AbstractRepositoryImpl.ESCAPE_CHAR));
		}
		return this.entityManager.createQuery(query).getResultList();
	}

	@Override
	public List<Office> all() {
		return searchSimilar(null);
	}	
}