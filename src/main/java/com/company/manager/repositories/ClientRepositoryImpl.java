package com.company.manager.repositories;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.company.manager.controllers.params.addressable.client.ClientSearchParams;
import com.company.manager.models.Client;
import com.company.manager.models.Client_;
import com.google.common.collect.Lists;

@Repository
@Transactional(readOnly = false)
public class ClientRepositoryImpl extends AbstractRepositoryImpl<Client, Long> implements ClientRepository{
	

	@Override
	public List<Client> search(ClientSearchParams clientSearchParams) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Client> query = criteriaBuilder.createQuery(Client.class);
		int limit = -1;
		Root<Client> clientRoot = query.from(Client.class);
		if(clientSearchParams != null){
			limit = clientSearchParams.getLimit() != null? clientSearchParams.getLimit(): -1;
			List<Predicate> predicates = Lists.newArrayList();
			if(clientSearchParams.isFetchProviders()){
				clientRoot.fetch(Client_.providers, JoinType.LEFT);
				query.distinct(true);
				query.select(clientRoot);
			}
			if(clientSearchParams.getId() != null){
				predicates.add(criteriaBuilder.equal(clientRoot.get(Client_.id), clientSearchParams.getId()));
			}
			if(clientSearchParams.getEmail() != null){
				String escapedEmail = super.escapeString(clientSearchParams.getEmail());
				predicates.add(criteriaBuilder.like(clientRoot.get(Client_.email), escapedEmail + "%", AbstractRepositoryImpl.ESCAPE_CHAR));
			}
			if(clientSearchParams.getName() != null){
				String escapedName = super.escapeString(clientSearchParams.getName());
				predicates.add(criteriaBuilder.like(clientRoot.get(Client_.name), escapedName + "%", AbstractRepositoryImpl.ESCAPE_CHAR));
			}
			query.where(predicates.toArray(new Predicate[predicates.size()]));
			if(clientSearchParams.getOrder() != null){
				switch(clientSearchParams.getOrder()){
				case AscEmail:
					query.orderBy(criteriaBuilder.asc(clientRoot.get(Client_.email)));
					break;
				case AscName:
					query.orderBy(criteriaBuilder.asc(clientRoot.get(Client_.name)));
					break;
				case DescEmail:
					query.orderBy(criteriaBuilder.desc(clientRoot.get(Client_.email)));
					break;
				default:
					//DescName
					query.orderBy(criteriaBuilder.desc(clientRoot.get(Client_.name)));
					break;
				}
			}
		}
		TypedQuery<Client> typedQuery = this.entityManager.createQuery(query);
		if(limit > 0){
			typedQuery.setMaxResults(limit);
		}
		return typedQuery.getResultList();
	}

	@Override
	public Client searchPrevious(String email) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Client> query = criteriaBuilder.createQuery(Client.class);
		Root<Client> clientRoot = query.from(Client.class);
		Predicate emailPredicate = criteriaBuilder.equal(clientRoot.get(Client_.email), email);
		query.where(emailPredicate);
		try{
			return this.entityManager.createQuery(query).setMaxResults(1).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
}
