package com.pack.pack.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


/**
 * 
 * @author Saurav
 *
 */
@Entity
@Table(name="USER")
public class User {
	
	@TableGenerator(name="ID_GEN", table="ID_GEN_TABLE", 
			pkColumnName="ID_GEN_KEY", pkColumnValue="ID_GEN_VALUE",
			allocationSize=1)
	@Id
	@GeneratedValue(generator="ID_GEN", strategy=GenerationType.TABLE)
	private long id;

	@Column(name="NAME", nullable=false)
	private String name;
	
	@Column(name="PACK_IMG")
	private String packImage;
	
	@Column(name="PROFILE_PICTURE")
	private String profilePicture;
	
	@Column(name="USERNAME", nullable=false, unique=true)
	private String username;
	
	@Column(name="PWD")
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackImage() {
		return packImage;
	}

	public void setPackImage(String packImage) {
		this.packImage = packImage;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}