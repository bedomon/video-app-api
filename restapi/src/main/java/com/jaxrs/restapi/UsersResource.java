package com.jaxrs.restapi;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
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
import javax.servlet.http.HttpSession;

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
	public String view(@PathParam("id") Integer id, @Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String token = request.getHeader("token");
		ArrayList record = new ArrayList();
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
		if(user_id != null){
			record = user.find_by_id(user_id);
		}else{
			response.sendError(401);
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(record);
		return json;
	}

	@GET
	@Path("/index")
	public String index(@Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String token = request.getHeader("token");
        ArrayList records = new ArrayList();
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
		if(user_id != null){
			Login login = new Login();
			HashMap current_user = login.current_user(user_id);
			if(current_user.size() > 0 && current_user.get("user_type").equals("admin")){
				records = user.find_all();
			}else{
				response.sendError(401);
			}
		}else{
			response.sendError(401);
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
		HashMap last_user = new HashMap();
		last_user = (HashMap) inserted_record.get(0);
		File dir = new File("/var/www/video-app-uploads/" + last_user.get("id").toString());
		dir.mkdir();
		dir = new File("/var/www/video-app-segments/" + last_user.get("id").toString());
		dir.mkdir();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(inserted_record);
		return json;
	}

	@POST
	@Path("/login")
	public String login(@FormParam("email") String email, @FormParam("password") String password, @Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String user_id = user.login(email, password);
		ArrayList record = new ArrayList();
		String json = "";
		if(user_id != null){
			String token = Login.generate_token_string();
			HttpSession session = request.getSession(true);
			session.setAttribute(token, user_id);
			ObjectMapper mapper = new ObjectMapper();
			record = user.find_by_id(user_id);
			HashMap token_info = new HashMap();
			token_info.put("token", token);
			record.add(token_info);
			json = mapper.writeValueAsString(record);
		}
		return json;
	}

	@GET
	@Path("/logout")
	public String logout(@Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		HttpSession session = request.getSession(true);
		return "logout";
	}


}
