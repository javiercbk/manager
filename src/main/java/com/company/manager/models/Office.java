package com.company.manager.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name="offices")
@Entity
public class Office {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="location", nullable=false, length=100)
	private String location;
	@Column(name="creation", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime creation;
	@Column(name="opened", nullable = false)
	private Date opened;
	@Column(name="closed")
	private Date closed;
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, mappedBy="office", orphanRemoval=true, cascade=CascadeType.ALL)
	private List<Employee> employees;
	
	public Office(){
		this.creation = new DateTime().toDateTime(DateTimeZone.UTC);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public DateTime getCreation() {
		return creation;
	}
	public void setCreation(DateTime creation) {
		this.creation = creation;
	}
	public Date getOpened() {
		return opened;
	}
	public void setOpened(Date opened) {
		this.opened = opened;
	}
	public Date getClosed() {
		return closed;
	}
	public void setClosed(Date closed) {
		this.closed = closed;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
}
