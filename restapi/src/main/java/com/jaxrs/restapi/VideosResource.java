package com.jaxrs.restapi;

import java.io.File;

import com.jaxrs.helpers.Login;
import com.jaxrs.models.Video;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.New;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/videos")
@Produces("application/json")
public class VideosResource extends MyApplication{
	
	Video video = new Video();

	@GET
	@Path("/view/{id}")
	public String view(@PathParam("id") Integer id, @QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException{
		ArrayList record = video.find_by_id(id);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(record);
		return json;
	}

	@GET
	@Path("/my_videos")
	public String my_videos(@QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException{
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String json = new String();
		if(current_user.size() > 0){
			ArrayList record = video.find_all_confirmed_by_user(token);
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(record);
		}
		return json;
	}
	
	@GET
	@Path("/featured")
	public String featured() throws JsonGenerationException, JsonMappingException, IOException{
		String json = new String();
		ArrayList record = video.find_all_featured();
		ObjectMapper mapper = new ObjectMapper();
		json = mapper.writeValueAsString(record);
		return json;
	}
	
	@GET
	@Path("/admin_videos")
	public String admin_videos(@QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException{
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String json = new String();
		if(current_user.size() > 0){
			ArrayList record = video.find_all();
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(record);
		}
		return json;
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@QueryParam("token") String token, @PathParam("id") Integer id, String jsonBody, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		ArrayList updated_record = new ArrayList();
		String json = "";
		if(current_user.size() > 0){
			jsonBody = jsonBody.replace("}", ",\"user_id\":\"" + current_user.get("id") + "\"}");
			updated_record = video.update(jsonBody);
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(updated_record);
		}
		return json;
	}

	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String delete(@QueryParam("token") String token, @PathParam("id") Integer id, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		boolean status = false;
		String json = "";
		if(current_user.size() > 0){
			status = video.delete(id);
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(status);
		}
		return json;
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String create(
			@FormDataParam("file") InputStream uploaded_input_stream,
			@FormDataParam("file") FormDataContentDisposition file_detail, 
			@QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException {
		
		ArrayList inserted_record = new ArrayList();
		ObjectMapper mapper = new ObjectMapper();
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String json = mapper.writeValueAsString(inserted_record);
		if(current_user.size() > 0){
			String file_name = file_detail.getFileName();
			String uploaded_file_location = "/home/vlatko/" + file_name;
			writeToFile(uploaded_input_stream, uploaded_file_location);
			String output = "File uploaded to : " + uploaded_file_location;
			HashMap<String, String> record = new HashMap();
			record.put("title", file_name);
			record.put("video_file", file_name);
			inserted_record = video.create(record);
			json = mapper.writeValueAsString(inserted_record);
		}	
		return json;
	}
	

	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
