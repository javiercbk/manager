package com.company.manager.repositories;

import java.io.Serializable;

public interface GenericRepository<T, PK extends Serializable> {
	public T reference(PK entityId);
	public void merge(T entity);
	public void detach(T entity);
	public T getById(PK entityId);
	public void persist(T entity);
	public void remove(PK entityId);
	public void remove(T entity);
	public void flush();
}
