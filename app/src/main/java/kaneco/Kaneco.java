package kaneco;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import kaneco.api.KanecoRestConsumer;
import kaneco.data.GuildConfig;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import se.michaelthelin.spotify.SpotifyApi;

public class Kaneco {

	public static SpotifyApi spotifyApi;
	public static KanecoRestConsumer restApi;
	public static LoadingCache<String, GuildConfig> configCache;

	public static void main(String[] args) throws Exception {
		String[] tokens = readTokenFiles();
		String botToken = tokens[0].split("=")[1];
		String apiToken = tokens[1].split("=")[1];
		String spotClientId = tokens[2].split("=")[1];
		String spotClientSecret = tokens[3].split("=")[1];

		spotifyApi = new SpotifyApi.Builder().setClientId(spotClientId).setClientSecret(spotClientSecret).build();

		Timer timer = new Timer();
		TimerTask hourlyTask = new TimerTask() {
			@Override
			public void run() {
				restApi = new KanecoRestConsumer(apiToken);
				try {
					spotifyApi.setAccessToken(spotifyApi.clientCredentials().build().execute().getAccessToken());
				} catch (Exception e) {
					System.out.println("Not possible to get spotify access token");
				}
			}
		};
		timer.schedule(hourlyTask, 0l, 1000 * 60 * 59);

		configCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<String, GuildConfig>() {
					@Override
					public GuildConfig load(String guildID) {
						GuildConfig guildCfg = restApi.getGuildConfig(guildID);
						return guildCfg;
					}
				});

		JDABuilder builder = JDABuilder.createDefault(botToken);
		builder.addEventListeners(new KanecoListener());
		builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
		builder.setActivity(Activity.playing("Arte feita por Ly.#1605"));
		builder.build();
	}

	public static String[] readTokenFiles() {
		File file = new File("config.txt");
		if (file.exists()) {
			File myObj = new File("config.txt");
			Scanner myReader;
			try {
				String data = "";
				myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) {
					data += myReader.nextLine() + "\n";
				}
				myReader.close();
				return data.split("\n");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("É necessário criar um arquivo no mesmo diretório da apliacação contendo\n"
					+ "botToken=seu token\napiToken=seu token\nspotifyClientId=seu client id\nspotifyClientSecret=seu client secret");
		}
		return null;
	}
}
