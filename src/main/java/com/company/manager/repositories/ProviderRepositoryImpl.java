package com.company.manager.repositories;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.company.manager.models.Provider;
import com.company.manager.models.Provider_;

@Repository
@Transactional(readOnly = false)
public class ProviderRepositoryImpl extends AbstractRepositoryImpl<Provider, Long> implements ProviderRepository {
	
	@Override
	public Provider search(String name) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Provider> query = criteriaBuilder.createQuery(Provider.class);
		Root<Provider> officeRoot = query.from(Provider.class);
		query.where(criteriaBuilder.equal(officeRoot.get(Provider_.name), name));
		try{
			return this.entityManager.createQuery(query).setMaxResults(1).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<Provider> searchSimilar(String name) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Provider> query = criteriaBuilder.createQuery(Provider.class);
		Root<Provider> providerRoot = query.from(Provider.class);
		if(name != null){
			String escapedName = super.escapeString(name);
			query.where(criteriaBuilder.like(providerRoot.get(Provider_.name), escapedName + "%", AbstractRepositoryImpl.ESCAPE_CHAR));
		}
		return this.entityManager.createQuery(query).getResultList();
	}

	@Override
	public List<Provider> all() {
		return searchSimilar(null);
	}
}
