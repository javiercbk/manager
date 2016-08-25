package com.company.manager.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@MappedSuperclass
public abstract class Addressable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	protected Long id;
	@Column(name="name", nullable = false, length= 80)
	protected String name;
	@Column(name="email", nullable = false, length= 254)
	protected String email;
	@Column(name="phone", length= 50)
	protected String phone;
	@Column(name="creation", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	protected DateTime creation;
	
	public Addressable(){
		this.creation = new DateTime().toDateTime(DateTimeZone.UTC);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public DateTime getCreation() {
		return creation;
	}
	public void setCreation(DateTime creation) {
		this.creation = creation;
	}
}
