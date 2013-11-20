package com.jaxrs.models;

import java.util.ArrayList;
import java.util.Map;

import com.jaxrs.helpers.*;

public class User extends Database {
	private String email;
	private String password;

	public ArrayList login(String email, String password){
		ArrayList record = new ArrayList();
		String sql = "SELECT * FROM users WHERE active = 1 AND email = '" + email + "' AND password = MD5(CONCAT('" + password + "', users.created_at))";
		ArrayList records = fetch_records(sql);
		if(records.size() == 1){
			Login login = new Login();
			records = login.generate_token(records);
		}
		return records;
	}
}
