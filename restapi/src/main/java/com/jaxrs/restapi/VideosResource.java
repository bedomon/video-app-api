package com.jaxrs.restapi;

import java.io.File;

import com.jaxrs.helpers.Login;
import com.jaxrs.models.Video;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.enterprise.inject.New;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
	@Path("/index")
	public String index() throws JsonGenerationException, JsonMappingException, IOException{
		ArrayList record = video.find_all();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(record);
		return json;
	}
	
	@GET
	@Path("/view/{id}")
	public String view(@PathParam("id") String id) throws JsonGenerationException, JsonMappingException, IOException{
		ArrayList record = video.find_by_id(id);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(record);
		return json;
	}

	@GET
	@Path("/my_videos")
	public String my_videos(@Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
		String token = request.getHeader("token");
		String json = new String();
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
	    if(user_id != null){
			ArrayList record = video.find_all_confirmed_by_user(user_id);
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
	public String admin_videos(@Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
		String token = request.getHeader("token");
		Login login = new Login();
		String json = new String();
		HttpSession session = request.getSession();
		String user_id = (String) session.getAttribute(token);
	    if(user_id != null){
			HashMap current_user = login.current_user(user_id);
			if(current_user.size() > 0){
				ArrayList record = video.find_all();
				ObjectMapper mapper = new ObjectMapper();
				json = mapper.writeValueAsString(record);
			}else{
				response.sendError(401);
			}
	    }else{
			response.sendError(401);
		}
		return json;
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam("id") Integer id, String jsonBody, @Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String token = request.getHeader("token");
		ArrayList updated_record = new ArrayList();
		String json = "";
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
	    if(user_id != null){
			jsonBody = jsonBody.replace("}", ",\"user_id\":\"" + user_id + "\"}");
			updated_record = video.update(jsonBody);
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(updated_record);
		}else{
			response.sendError(401);
		}
		return json;
	}


	@PUT
	@Path("/feature/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String feature(@PathParam("id") Integer id, String jsonBody, @Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String token = request.getHeader("token");
		ArrayList updated_record = new ArrayList();
		String json = "";
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
	    if(user_id != null){
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
	public String delete(@PathParam("id") Integer id, @Context HttpServletRequest request, @Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		boolean status = false;
		String token = request.getHeader("token");
		String json = "";
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
	    if(user_id != null){
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
			@Context HttpServletRequest request, 
			@Context HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException, NoSuchAlgorithmException, InterruptedException {
		String token = request.getHeader("token");
		ArrayList inserted_record = new ArrayList();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(inserted_record);
		HttpSession session = request.getSession(true);
		String user_id = (String) session.getAttribute(token);
	    if(user_id != null){
			String file_name = file_detail.getFileName();
			String digest = generate_digest(file_name);
			String extension = get_extension(file_name);
			String video_folder = generate_digest(file_name);
			
			String uploads_path = "/var/www/video-app-uploads/";
			String uploads_url = "http://localhost/video-app-uploads/";
			
			String segments_url = "http://localhost/video-app-segments/";
			String segments_path = "/var/www/video-app-segments/";
			
			File dir = new File(uploads_path + user_id + "/" + video_folder);
			dir.mkdir();
			dir = new File(segments_path + user_id + "/" + video_folder);
			dir.mkdir();
			
			String uploaded_file_location = uploads_path + user_id + "/" + video_folder + "/" + digest + "." + extension;
			String uploaded_file_url = uploads_url + user_id + "/" + video_folder + "/" + digest + "." + extension;
			String transcoded_file_location = uploads_path + user_id + "/" + video_folder + "/" + digest + ".transcoded.mp4";
			String transcoded_file_url = uploads_url + user_id + "/" + video_folder + "/" + digest + ".transcoded.mp4";
			String thumbnail_location = uploads_path + user_id + "/" + video_folder + "/" + digest + ".jpg";
			String thumbnail_url = uploads_url + user_id + "/" + video_folder + "/" + digest + ".jpg";
			String segments_file_location = segments_path + user_id + "/" + video_folder;
			String segments_file_url = segments_url + user_id + "/" + video_folder;
			
			writeToFile(uploaded_input_stream, uploaded_file_location);
			
			HashMap<String, String> record = new HashMap();
			
			record.put("title", file_name);
			record.put("uploaded_file_url", uploaded_file_url);
			record.put("uploaded_file_location", uploaded_file_location);
			
			record.put("thumbnail_url", thumbnail_url);
			record.put("thumbnail_location", thumbnail_location);
			
			record.put("transcoded_file_location", transcoded_file_location);
			record.put("transcoded_file_url", transcoded_file_url);

			record.put("segments_file_location", segments_file_location);
			record.put("segments_file_url", segments_file_url);
			
			inserted_record = video.create(record);
			json = mapper.writeValueAsString(inserted_record);
			
			transcode(inserted_record, uploaded_file_location, transcoded_file_location, segments_file_location);
			get_thumbnail(uploaded_file_location, thumbnail_location);
	    }else{
			response.sendError(401);
		}
		return json;
	}
	

	private void get_thumbnail(String uploaded_file_location, String thumbnail_location) throws IOException, InterruptedException {
		String command = "ffmpeg -itsoffset -4  -i " + uploaded_file_location + " -vcodec mjpeg -vframes 1 -an -f rawvideo -s 320x240 " + thumbnail_location;
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
	}

	private void transcode(final ArrayList video_data, final String uploaded_file_location, final String transcoded_file_location, final String segments_file_location) throws IOException, InterruptedException{
	/*	String command = "/home/vlatko/bin/ffmpeg -i " + uploaded_file_location + " -acodec copy -vcodec libx264 " + transcoded_file_location;
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		*/
		
		ProcessBuilder pb = new ProcessBuilder();
		pb.command("/home/vlatko/bin/ffmpeg", "-i", uploaded_file_location, "-acodec", "copy", "-vcodec", "libx264", transcoded_file_location);
		System.out.println(pb.command().toString());
		final Process p = pb.start();
	   
		new Thread() {
	      public void run() {
	    	try{
		        Scanner sc = new Scanner(p.getErrorStream());
		        Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
		        String dur = sc.findWithinHorizon(durPattern, 0);
		        if (dur == null)
		          throw new RuntimeException("Could not parse duration.");
		        String[] hms = dur.split(":");
		        double totalSecs = Integer.parseInt(hms[0]) * 3600
		                         + Integer.parseInt(hms[1]) *   60
		                         + Double.parseDouble(hms[2]);
		        System.out.println("Total duration: " + totalSecs + " seconds.");
		        Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
		        
		        String match;
		        String[] matchSplit;
		        while (null != (match = sc.findWithinHorizon(timePattern, 0))) {
		            matchSplit = match.split(":");
		            double progress = Integer.parseInt(matchSplit[0]) * 3600 +
		                Integer.parseInt(matchSplit[1]) * 60 +
		                Double.parseDouble(matchSplit[2]) / totalSecs;
		            System.out.printf("Progress: %.2f%%%n", progress * 100);
		        }
	    	}finally{
	    		System.out.println("test");
				try {
					segment_video(video_data, transcoded_file_location, segments_file_location);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	      }		
	    }.start();
	}
	
	private void segment_video(final ArrayList video_data, String transcoded_file_location, String segments_file_location) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] envp = null;
		File exec_path = new File(segments_file_location);
		Process pr = rt.exec("/home/vlatko/bin/ffmpeg -i " + transcoded_file_location + " -acodec copy -bsf:a h264_mp4toannexb -vcodec libx264 -vprofile baseline -maxrate 1000k -bufsize 1000k -s 960x540 -bsf:v dump_extra -map 0 -f segment -segment_format mpegts -segment_list playlist.m3u8 -segment_time 2 segment-%d.ts", envp, exec_path);
		System.out.println("/home/vlatko/bin/ffmpeg -i " + transcoded_file_location + " -acodec copy -bsf:a h264_mp4toannexb -vcodec libx264 -vprofile baseline -maxrate 1000k -bufsize 1000k -s 960x540 -bsf:v dump_extra -map 0 -f segment -segment_format mpegts -segment_list playlist.m3u8 -segment_time 2 segment-%d.ts");
	
		new Thread() {
	      public void run() {
	    	try{
	    		System.out.printf("Segmenting...");
	    	}finally{
	    		confirm_video(video_data);
	    	}
	      }		
	    }.start();
	}

	private void confirm_video(ArrayList video_data) {
		HashMap v = (HashMap) video_data.get(0);
		ArrayList updated_record = video.update("{ \"id\": \"" + v.get("id").toString() + "\", \"confirmed\": \"1\" }");
	}
	
	private String get_extension(String file_name) {
		String extension = "";
		int i = file_name.lastIndexOf('.');
		if (i > 0) {
		    extension = file_name.substring(i+1);
		}
		return extension;
	}

	private String generate_digest(String string) throws UnsupportedEncodingException, NoSuchAlgorithmException{ 
		java.util.Date date= new java.util.Date();
		string = string + new Timestamp(date.getTime());
		MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());
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
