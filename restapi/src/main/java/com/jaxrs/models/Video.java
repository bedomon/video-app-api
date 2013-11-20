package com.jaxrs.models;

import java.util.ArrayList;
import java.util.HashMap;

import com.jaxrs.helpers.Database;
import com.jaxrs.helpers.Login;

public class Video extends Database {

	public ArrayList find_all_by_user(String token) {
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String sql = "SELECT * FROM videos WHERE user_id = " + current_user.get("id");
		ArrayList records = fetch_records(sql);
		return records;
	}

}
