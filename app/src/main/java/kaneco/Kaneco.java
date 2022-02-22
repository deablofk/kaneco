package kaneco;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import kaneco.api.KanecoRestConsumer;
import kaneco.data.GuildConfig;
import kaneco.data.UserData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import se.michaelthelin.spotify.SpotifyApi;

public class Kaneco {

	public static SpotifyApi spotifyApi;
	public static KanecoRestConsumer restApi;
	public static LoadingCache<Long, GuildConfig> configCache;
	public static LoadingCache<Long, UserData> userCache;
	public static LoadingCache<Long, Boolean> cantGetXp;

	public static void main(String[] args) throws Exception {
		Properties cfg = getConfigProperties("config.properties");
		newTokenAfterHour(cfg.getProperty("API_TOKEN"), cfg.getProperty("SPOTIFY_CLIENT_ID"),
				cfg.getProperty("SPOTIFY_CLIENT_SECRET"));
		initializeCaches();
		createJDA(cfg.getProperty("BOT_TOKEN"));
	}

	private static Properties getConfigProperties(String fileName) throws Exception {
		Properties props = new Properties();
		InputStream is = new FileInputStream(fileName);

		if (is != null) {
			props.load(is);
			is.close();
		}

		return props;
	}

	private static void initializeApis(String token, String clientId, String clientSecret) {
		restApi = new KanecoRestConsumer(token);
		spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
		spotifyApi.setAccessToken(spotifyApi.getAccessToken());
	}

	private static void newTokenAfterHour(String token, String clientId, String clientSecret) {
		Timer timer = new Timer();
		TimerTask hourlyTask = new TimerTask() {
			@Override
			public void run() {
				initializeApis(token, clientId, clientSecret);
			}
		};
		timer.schedule(hourlyTask, 0l, 1000 * 60 * 60);
	}

	private static void initializeCaches() {
		configCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, GuildConfig>() {
					@Override
					public GuildConfig load(Long guildID) {
						GuildConfig guildCfg = restApi.getGuildConfig(guildID);
						return guildCfg;
					}
				});

		userCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, UserData>() {
					@Override
					public UserData load(Long userId) throws Exception {
						return restApi.getUserData(userId);
					}
				});

		cantGetXp = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, Boolean>() {
					@Override
					public Boolean load(Long key) throws Exception {
						return true;
					}
				});
	}

	private static JDA createJDA(String token) throws LoginException {
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.addEventListeners(new KanecoListener());
		builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
		builder.setActivity(Activity.playing("Arte feita por Ly.#1605"));
		return builder.build();
	}
}
