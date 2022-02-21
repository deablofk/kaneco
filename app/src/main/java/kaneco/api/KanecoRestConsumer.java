package kaneco.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import kaneco.data.GuildConfig;
import kaneco.data.UserData;
import kaneco.data.WarnObject;

public class KanecoRestConsumer {

	private String baseURL = "http://localhost:8000/api/v2";
	private Gson gson = new Gson();
	private String accessToken;

	public KanecoRestConsumer(String authToken) {
		accessToken = getAccessToken(authToken);
		System.out.println(accessToken);
	}

	public UserData getUserData(long userId) {
		HttpResponse hr = getApiResponse(baseURL + "/users/" + userId, "GET", accessToken, null);
		int statusCode = hr.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			String json = null;
			try {
				json = EntityUtils.toString(hr.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return gson.fromJson(json, UserData.class);
		} else if (statusCode == 404) {
			UserData userData = new UserData(userId);
			if (postUserData(userData))
				return userData;
		}

		return null;
	}

	public boolean postUserData(UserData data) {
		HttpResponse hr = getApiResponse(baseURL + "/users/", "POST", accessToken,
				new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public boolean sendUserData(UserData data) {
		HttpResponse hr = getApiResponse(baseURL + "/users/", "POST", accessToken,
				new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public GuildConfig getGuildConfig(long guildId) {
		HttpResponse hr = getApiResponse(baseURL + "/guilds/" + guildId, "GET", accessToken, null);

		int statusCode = hr.getStatusLine().getStatusCode();

		if (statusCode == 200) {
			String json = null;
			try {
				json = EntityUtils.toString(hr.getEntity());
			} catch (IOException e) {
				System.out.println("KanecoRestConsumer.java, error on getting guild config from api.");
			}
			return gson.fromJson(json, GuildConfig.class);
		} else if (statusCode == 404) {
			GuildConfig cfg = new GuildConfig(guildId, "./");
			postGuildConfig(cfg);
			return cfg;
		}

		return null;
	}

	public boolean postGuildConfig(GuildConfig cfg) {
		HttpResponse hr = getApiResponse(baseURL + "/guilds/", "POST", accessToken,
				new StringEntity(gson.toJson(cfg), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public boolean putGuildConfig(GuildConfig cfg) {
		HttpResponse hr = getApiResponse(baseURL + "/guilds/" + cfg.getGuildId(), "PUT", accessToken,
				new StringEntity(gson.toJson(cfg), ContentType.APPLICATION_JSON));
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public List<WarnObject> getWarns(String userId) {
		HttpResponse hr = getApiResponse(baseURL + "/users/warn/" + userId, "GET", accessToken, null);
		String json = null;

		try {
			json = EntityUtils.toString(hr.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (hr.getStatusLine().getStatusCode() == 404 || hr.getStatusLine().getStatusCode() == 400) {
			return new ArrayList<WarnObject>();
		}

		return gson.fromJson(json, new TypeToken<List<WarnObject>>() {
		}.getType());
	}

	public boolean deleteWarn(String userId, String warnId) {
		HttpResponse hr = getApiResponse(baseURL + "/users/warn/" + userId + "/" + warnId, "DELETE",
				accessToken, null);
		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	public boolean sendWarn(long userid, WarnObject warn) {
		String json = gson.toJson(warn);
		HttpResponse hr = getApiResponse(baseURL + "/users/warn", "POST", accessToken,
				new StringEntity(json, ContentType.APPLICATION_JSON));

		if (hr.getStatusLine().getStatusCode() == 400) {
			sendUserData(new UserData(userid));
			hr = getApiResponse(baseURL + "/users/warn", "POST", accessToken,
					new StringEntity(json, ContentType.APPLICATION_JSON));
		}

		int statusCode = hr.getStatusLine().getStatusCode();
		return statusCode == 200 ? true : false;
	}

	private String getAccessToken(String authToken) {
		HttpResponse hr = getApiResponse(baseURL + "/auth/access_token", "POST", authToken, null);
		String json = null;

		try {
			json = EntityUtils.toString(hr.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return JsonParser.parseString(json).getAsJsonObject().get("data").getAsJsonObject().get("access_token")
				.getAsString();
	}

	public HttpResponse getApiResponse(String url, String type, String authorizationValue, StringEntity entity) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest request = null;
		switch (type) {
			case "POST":
				request = new HttpPost(url);
				if (entity != null) {
					((HttpPost) request).setEntity(entity);
				}
				break;
			case "GET":
				request = new HttpGet(url);
				break;
			case "PUT":
				request = new HttpPut(url);
				if (entity != null) {
					((HttpPut) request).setEntity(entity);
				}
				break;
			case "DELETE":
				request = new HttpDelete(url);
				break;
		}

		request.addHeader("Authorization", "Bearer " + authorizationValue);

		try {
			return client.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
