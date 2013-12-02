package com.jaxrs.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import com.jaxrs.helpers.Database;

public class Login extends Database{
	
	DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public ArrayList generate_token(ArrayList records) {
		HashMap user = (HashMap) records.get(0);
		String user_id = user.get("id").toString();
		Date last_date = get_last_date();
		String sql = "SELECT * FROM logins WHERE user_id = '" + user_id.toString() + "' AND updated_at > '" + date_format.format(last_date).toString() + "'";
		ArrayList logins = fetch_records(sql);
		String token = "";
		if(logins.size() == 0){
			token = UUID.randomUUID().toString();
			sql = "DELETE FROM logins WHERE user_id = '" + user_id.toString() + "'";
			execute_sql(sql);
		}else{
			HashMap login = (HashMap) logins.get(0);
			token = login.get("token").toString();
			int id = Integer.parseInt(login.get("id").toString());
			delete(id);
		}
		update_token(user_id, token);
		HashMap hash_row = new HashMap();
		hash_row.put("token", token);
		hash_row.put("email", user.get("email"));
		hash_row.put("name", user.get("name"));
		hash_row.put("user_type", user.get("user_type"));
		records.add(hash_row);
		return records;
	}
	
	public HashMap current_user(String token){
		Date last_date = get_last_date();
		String sql = "SELECT * FROM logins WHERE token = '" + token + "' AND updated_at > '" + date_format.format(last_date).toString() + "'";
		ArrayList logins = fetch_records(sql);
		ArrayList users = new ArrayList();
		HashMap current_user = new HashMap();
		if(logins.size() == 1){
			HashMap login = (HashMap) logins.get(0);
			String user_id = login.get("user_id").toString();
			sql = "SELECT * FROM users WHERE id = '" + user_id + "'";
			users = fetch_records(sql);
			if(users.size() == 1){
				current_user = (HashMap) users.get(0);
			}
		}
		return current_user;
	}
	
	protected Boolean is_logged_in(Integer user_id, String token){
		Date last_date = get_last_date();
		String sql = "SELECT * FROM logins WHERE user_id = '" + user_id.toString() + "' AND token = '" + token + "' AND updated_at > '" + date_format.format(last_date).toString() + "'";
		ArrayList logins = fetch_records(sql);
		Boolean is_logged_in = false;
		if(logins.size() > 0){
			is_logged_in = true;
		}
		return false;
	}
	
	private void update_token(String user_id, String token){
		HashMap<String, String> updated_login = new HashMap();
		updated_login.put("user_id", user_id.toString());
		updated_login.put("token", token.toString());
		create(updated_login); 
	}
	
	private Date get_last_date(){
		Date current_date = new Date();
		final long hours_in_millis = 60L * 60L * 1000L;
		Date last_date = new Date(current_date.getTime() - (1L * hours_in_millis));
		return last_date;
	}
	
}
