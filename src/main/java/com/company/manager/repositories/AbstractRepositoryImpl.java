package com.company.manager.repositories;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.company.manager.repositories.exceptions.NonExistingEntityException;

public abstract class AbstractRepositoryImpl<T, PK extends Serializable> implements GenericRepository<T, PK>{
	public static final Character ESCAPE_CHAR = Character.valueOf('!');
	@PersistenceContext
	protected EntityManager entityManager;
	private final Class<T> persistentClass;
	
	protected String escapeString(String toEscape){
		return toEscape.replaceAll("!", "!!").replaceAll("%", "!%");
	}
    
    @SuppressWarnings("unchecked")
    public AbstractRepositoryImpl(){
        this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    public void flush(){
    	this.entityManager.flush();
    }
    
    @Override
    public void merge(T entity){
    	this.entityManager.merge(entity);
    }
    
    @Override
    public void detach(T entity){
    	this.entityManager.detach(entity);
    }
    
    @Override
    public T reference(PK entityId){
    	return this.entityManager.getReference(this.persistentClass, entityId);
    }

	@Override
	public T getById(PK entityId) {
		return this.entityManager.find(this.persistentClass, entityId);
	}

	@Override
	public void persist(T entity) {
		this.entityManager.persist(entity);
	}

	@Override
	public void remove(PK entityId) {
		T found = this.entityManager.find(this.persistentClass, entityId);
		if(found != null){
			this.entityManager.remove(found);
		}else{
			throw new NonExistingEntityException(this.persistentClass, entityId);
		}
	}
	
	@Override
	public void remove(T entity) {
		this.entityManager.remove(entity);
	}
}
