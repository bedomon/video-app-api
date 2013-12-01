package com.jaxrs.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Database {

	public String table = getClass().getSimpleName().toLowerCase() + "s";
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/myapp";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";
	Connection conn = null;

	public Connection connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public ArrayList fetch_records(String sql) {
		Connection conn = connect();
		ArrayList records = new ArrayList();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				HashMap hash_row = new HashMap();
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					String column_name = rsmd.getColumnName(i);
					String value = "";
					if(rs.getObject(column_name) != null){
						value = rs.getObject(column_name).toString();
					}						
//					if(column_name.equals("password")){
//						value = "[FILTERED]";
//					}
					hash_row.put(column_name, value);
				}
				records.add(hash_row);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return records;
	}

	public ArrayList find_all() {
		String sql = "SELECT * FROM " + table;
		ArrayList records = fetch_records(sql);
		return records;
	}

	public ArrayList find_by_id(int id) {
		String sql = "SELECT * FROM " + table + " WHERE id = " + id;
		ArrayList records = fetch_records(sql);
		return records;
	}
	
	public ArrayList find_all_order(String order) {
		String sql = "SELECT * FROM " + table + " ORDER BY " + order;
		ArrayList records = fetch_records(sql);
		return records;
	}

	public Boolean destroy(int id) {
		Boolean status = false;
		Connection conn = connect();
		String sql = "DELETE FROM " + table + " WHERE id = " + id;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			status = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;
	}

	public ArrayList create(HashMap record){
		ArrayList records = new ArrayList();
		//Map<String, Object> map = parse_request(s);
		String sql = "INSERT INTO " + table;
		ArrayList columns = new ArrayList();
		ArrayList values = new ArrayList();
		
		Date date = new Date();
		DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Iterator it = record.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pairs = (Map.Entry)it.next();
	    	String value = (String) pairs.getValue();
	    	String key = (String) pairs.getKey();
	    	if(table.equals("users") && key.equals("password")){
				value = encrypt_password(value, date, date_format);
			}
			columns.add(key);
			values.add("'" + value.toString().replaceAll("'","''") + "'");
	        it.remove(); 
	    }
		
		if(table.equals("users") && !columns.contains("active")){
			columns.add("active");
			values.add("1");
		}
		
		columns.add("created_at");
		columns.add("updated_at");
		values.add("'" + date_format.format(date).toString() + "'");
		values.add("'" + date_format.format(date).toString() + "'");

		String s_columns = columns.toString();
		String s_values = values.toString();
		s_columns = s_columns.replace("[", "(").replace("]", ")");
		s_values = s_values.replace("[", "(").replace("]", ")");
		sql = sql + " " + s_columns + " VALUES " + s_values + ";";
		Statement stmt;
		try {
			Connection conn = connect();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			String sql_last_insert = "SELECT * FROM " + table + " ORDER BY id DESC LIMIT 1";
			records = fetch_records(sql_last_insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return records;
	}

	public ArrayList update(String s) {
		ArrayList records = new ArrayList();
		Map<String, Object> map = parse_request(s);
		String sql = "UPDATE " + table + " SET ";
		ArrayList updates = new ArrayList();
		Object id = "";
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getKey() != "id") {
				updates.add(entry.getKey() + " = '" + entry.getValue().toString().replaceAll("'","''") + "' ");
			} else {
				id = entry.getValue().toString().replaceAll("'","''");
			}
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		updates.add("updated_at = '" + dateFormat.format(date).toString()
				+ "' ");

		String s_updates = updates.toString();
		s_updates = s_updates.replace("[", "").replace("]", "");
		sql = sql + " " + s_updates + " WHERE id = " + id.toString() + ";";
		Statement stmt;
		try {
			Connection conn = connect();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			String sql_last_insert = "SELECT * FROM " + table + " WHERE id = "
					+ id.toString();
			records = fetch_records(sql_last_insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return records;
	}

	private String encrypt_password(String password, Date date, DateFormat date_format) {
		String formated_date = date_format.format(date).toString();
		password = password + formated_date;
		byte[] bytes_of_message;
		String encrypted = "";
			try {
				bytes_of_message = password.getBytes("UTF-8");
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] digest = md.digest(bytes_of_message);
				StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < digest.length; i++) {
		        	sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		        }
		        encrypted = sb.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
		return encrypted;
	}

	protected Map parse_request(String s){
		Map<String, Object> map = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(s, Map.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	protected Boolean execute_sql(String sql){
		Boolean status = false;
		Statement stmt;
		try {
			Connection conn = connect();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			status = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
