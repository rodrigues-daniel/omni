package org.omni.admin;

import java.io.Serializable;
import javax.enterprise.inject.Model;

@Model
public class AdminBean implements Serializable {
	public void save() {
		System.out.println("salvo no admin");
	}
}