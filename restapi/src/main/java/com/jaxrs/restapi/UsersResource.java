package com.jaxrs.restapi;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jaxrs.helpers.*;
import com.jaxrs.models.User;

@Path("/users")
@Produces("application/json")
@Consumes({"application/xml", "application/json", "application/x-www-form-urlencoded"})
public class UsersResource extends MyApplication {
	
	User user = new User();
	
	@GET
	@Path("/view/{id}")
	public String view(@PathParam("id") Integer id, @QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException {
		ArrayList record = new ArrayList();
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		if(current_user.size() > 0){
			record = user.find_by_id(id);
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(record);
		return json;
	}
	
	@GET
	@Path("/index")
	public String index(@QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException {
		ArrayList records = new ArrayList();
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		if(current_user.size() > 0){
			records = user.find_all();
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(records);
		return json;
	}
	
	@POST
	@Path("/register")
	public String create(@FormParam("email") String email, @FormParam("password") String password, @FormParam("name") String name) throws JsonGenerationException, JsonMappingException, IOException {
		HashMap record = new HashMap();
		record.put("email", email);
		record.put("name", name);
		record.put("password", password);
		ArrayList inserted_record = user.create(record);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(inserted_record);
		return json;
	}

	@POST
	@Path("/login")
	public String login(@FormParam("email") String email, @FormParam("password") String password, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
	    ArrayList records = user.login(email, password);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(records);
		return json;
	}

	
}
