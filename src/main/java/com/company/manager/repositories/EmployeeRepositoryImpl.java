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

import com.company.manager.controllers.params.addressable.employee.EmployeeSearchParams;
import com.company.manager.models.Employee;
import com.company.manager.models.Employee_;
import com.google.common.collect.Lists;

@Repository
@Transactional(readOnly = false)
public class EmployeeRepositoryImpl extends AbstractRepositoryImpl<Employee, Long> implements EmployeeRepository {

	@Override
	public Employee searchPrevious(String email) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
		Root<Employee> clientRoot = query.from(Employee.class);
		Predicate emailPredicate = criteriaBuilder.equal(clientRoot.get(Employee_.email), email);
		query.where(emailPredicate);
		try{
			return this.entityManager.createQuery(query).setMaxResults(1).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<Employee> search(EmployeeSearchParams employeeSearchParams) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
		int limit = -1;
		if(employeeSearchParams != null){
			limit = employeeSearchParams.getLimit() != null? employeeSearchParams.getLimit(): -1;
			List<Predicate> predicates = Lists.newArrayList();
			Root<Employee> employeeRoot = query.from(Employee.class);
			if(employeeSearchParams.isFetchOffice()){
				employeeRoot.fetch(Employee_.office, JoinType.INNER);
				query.select(employeeRoot);
			}
			if(employeeSearchParams.getEmail() != null){
				String escapedEmail = super.escapeString(employeeSearchParams.getEmail());
				predicates.add(criteriaBuilder.like(employeeRoot.get(Employee_.email), escapedEmail + "%", AbstractRepositoryImpl.ESCAPE_CHAR));
			}
			if(employeeSearchParams.getName() != null){
				String escapedName = super.escapeString(employeeSearchParams.getName());
				predicates.add(criteriaBuilder.like(employeeRoot.get(Employee_.name), escapedName + "%", AbstractRepositoryImpl.ESCAPE_CHAR));
			}
			query.where(predicates.toArray(new Predicate[predicates.size()]));
			if(employeeSearchParams.getOrder() != null){
				switch(employeeSearchParams.getOrder()){
				case AscEmail:
					query.orderBy(criteriaBuilder.asc(employeeRoot.get(Employee_.email)));
					break;
				case AscName:
					query.orderBy(criteriaBuilder.asc(employeeRoot.get(Employee_.name)));
					break;
				case DescEmail:
					query.orderBy(criteriaBuilder.desc(employeeRoot.get(Employee_.email)));
					break;
				case DescName:
					query.orderBy(criteriaBuilder.desc(employeeRoot.get(Employee_.name)));
					break;
				case AscWorkingSince:
					query.orderBy(criteriaBuilder.asc(employeeRoot.get(Employee_.workingSince)));
					break;
				default:
					query.orderBy(criteriaBuilder.desc(employeeRoot.get(Employee_.workingSince)));
					break;
				}
			}
		}
		TypedQuery<Employee> typedQuery = this.entityManager.createQuery(query);
		if(limit > 0){
			typedQuery.setMaxResults(limit);
		}
		return typedQuery.getResultList();
	}

}
