package com.jaxrs.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jaxrs.helpers.*;

public class User extends Database {
	private String email;
	private String password;

	public String login(String email, String password){
		ArrayList record = new ArrayList();
		String sql = "SELECT * FROM users WHERE active = 1 AND email = '" + email + "' AND password = MD5(CONCAT('" + password + "', users.created_at))";
		ArrayList records = fetch_records(sql);
		String user_id = "";
		if(records.size() == 1){
			HashMap row = (HashMap) records.get(0);
			user_id = row.get("id").toString();
		}
		return user_id;
	}
}
