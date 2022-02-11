package kaneco.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import kaneco.data.GuildConfig;
import kaneco.data.UserData;
import kaneco.data.WarnObject;

public class KanecoRestConsumer {

	private String baseURL = "http://localhost:5000/api/v1";
	private Gson gson = new Gson();
	private String accessToken;

	public KanecoRestConsumer(String authToken) {
		accessToken = getAccessToken(authToken);
		System.out.println(accessToken);
	}

	public UserData getUserData(String userId) {
		HttpResponse hr = getApiResponse(baseURL + "/users/" + userId, "GET", accessToken, null);
		String json = null;
		try { json = EntityUtils.toString(hr.getEntity()); } catch( IOException e ) { e.printStackTrace(); return null; }
		return gson.fromJson(JsonParser.parseString(json).getAsJsonObject().get("data").getAsString(), UserData.class);
	}

	public boolean sendUserData(UserData data) {
		HttpResponse hr = getApiResponse(baseURL + "/users/" + data.getUser(), "POST", accessToken, new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public GuildConfig getGuildConfig(String guildId) {
		HttpResponse hr = getApiResponse(baseURL +"/guilds/" + guildId, "GET", accessToken, null);
		String json = null;

		try { json = EntityUtils.toString(hr.getEntity()); } catch ( IOException e ) { e.printStackTrace(); return null; }
		
		return gson.fromJson(JsonParser.parseString(json).getAsJsonObject().get("data").getAsString(), GuildConfig.class);
	}

	public boolean sendGuildConfig(GuildConfig cfg) {
		HttpResponse hr = getApiResponse(baseURL + "/guilds/" + cfg.getGuildId(), "POST", accessToken, new StringEntity(gson.toJson(cfg), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public List<WarnObject> getWarns(String userId) {
		HttpResponse hr = getApiResponse(baseURL + "/users/" + userId + "/warn", "GET", accessToken, null);
		String json = null;

		try { json = EntityUtils.toString(hr.getEntity()); } catch(IOException e ) { e.printStackTrace(); }
		
		if(json.contains("failed")){
			return new ArrayList<WarnObject>(); 
		}

		JsonArray jsonArray = JsonParser.parseString(json).getAsJsonObject().get("data").getAsJsonArray();

		return gson.fromJson(jsonArray, new TypeToken<List<WarnObject>>() {}.getType()); 
	}

	public boolean deleteWarn(String userId, String warnId) {
		HttpResponse hr = getApiResponse(baseURL + "/users/" + userId + "/warn/"+ warnId +"/delete", "POST", accessToken, null);
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public boolean sendWarn(WarnObject warn) {
		HttpResponse hr = getApiResponse(baseURL + "/users/" + warn.getUser() + "/warn", "POST", accessToken, new StringEntity(gson.toJson(warn), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	private String getAccessToken(String authToken) {
		HttpResponse hr = getApiResponse(baseURL + "/auth/token/access_token", "POST", authToken, null);
		String json = null;

		try { json = EntityUtils.toString(hr.getEntity()); } catch ( IOException e ){ e.printStackTrace(); return null; }

		return JsonParser.parseString(json).getAsJsonObject().get("data").getAsJsonObject().get("access_token").getAsString();
	}

	public HttpResponse getApiResponse(String url, String type, String authorizationValue, StringEntity entity) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest request = type == "POST" ? new HttpPost(url) : new HttpGet(url);
		request.addHeader("Authorization", authorizationValue);

		if ( entity != null && type == "POST" ) 
			((HttpPost) request).setEntity(entity);
    
		try { return client.execute(request); }
		catch ( Exception e ) { e.printStackTrace(); }

		return null;
	}
}
