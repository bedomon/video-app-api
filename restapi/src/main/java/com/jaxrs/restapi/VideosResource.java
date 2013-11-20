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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
public class VideosResource {
	
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
	@Path("/index")
	public String index(@QueryParam("token") String token) throws JsonGenerationException, JsonMappingException, IOException{
		ArrayList record = video.find_all_by_user(token);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(record);
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
			String uploaded_file_location = "/home/vlatko/"
					+ file_name;
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
