package com.jaxrs.restapi;

import java.io.File;

import com.jaxrs.helpers.Login;
import com.jaxrs.models.Video;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	public String my_videos(@QueryParam("token") String token, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String json = new String();
		if(current_user.size() > 0){
			ArrayList record = video.find_all_confirmed_by_user(token);
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(record);
		}else{
			response.sendError(401);
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
	public String admin_videos(@QueryParam("token") String token, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String json = new String();
		if(current_user.size() > 0){
			ArrayList record = video.find_all();
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(record);
		}else{
			response.sendError(401);
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
		}else{
			response.sendError(401);
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
		}else{
			response.sendError(401);
		}
		return json;
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String create(
			@FormDataParam("file") InputStream uploaded_input_stream,
			@FormDataParam("file") FormDataContentDisposition file_detail, 
			@QueryParam("token") String token, 
			@Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException, NoSuchAlgorithmException {
		
		ArrayList inserted_record = new ArrayList();
		ObjectMapper mapper = new ObjectMapper();
		Login login = new Login();
		HashMap current_user = login.current_user(token);
		String json = mapper.writeValueAsString(inserted_record);
		if(current_user.size() > 0){
			String file_name = file_detail.getFileName();
			String digest = generate_digest(file_name);
			String extension = get_extension(file_name);
			String uploaded_file_location = "/var/www/video-app-uploads/" + current_user.get("id").toString() + "/" + digest + "." + extension;
			String uploaded_file_url = "http://localhost/video-app-uploads/" + current_user.get("id").toString() + "/" + digest + "." + extension;
			writeToFile(uploaded_input_stream, uploaded_file_location);
			
			HashMap<String, String> record = new HashMap();
			record.put("title", file_name);
			record.put("video_file", uploaded_file_url);
			inserted_record = video.create(record);
			json = mapper.writeValueAsString(inserted_record);
		}else{
			response.sendError(401);
		}
		return json;
	}
	
	private String get_extension(String file_name) {
		String extension = "";
		int i = file_name.lastIndexOf('.');
		if (i > 0) {
		    extension = file_name.substring(i+1);
		}
		return extension;
	}

	private String generate_digest(String file_name) throws UnsupportedEncodingException, NoSuchAlgorithmException{ 
		MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(file_name.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        StringBuffer hexString = new StringBuffer();
    	for (int i=0;i<byteData.length;i++) {
    		String hex=Integer.toHexString(0xff & byteData[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
    	}
		return hexString.toString();
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
