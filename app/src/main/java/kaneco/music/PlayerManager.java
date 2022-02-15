package kaneco.music;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class PlayerManager {

	private static PlayerManager INSTANCE;
	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;

	public PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}

	public GuildMusicManager getGuildMusicManger(Guild guild) {
		GuildMusicManager musicManager = musicManagers.get(guild.getIdLong());
		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager);
			musicManagers.put(guild.getIdLong(), musicManager);
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		return musicManager;
	}

	public void loadAndPlay(InteractionHook hook, TextChannel channel, Member member, String trackUrl,
			boolean sendMessage) {
		KanecoALRH alrh = new KanecoALRH(trackUrl, hook, channel, member, getGuildMusicManger(channel.getGuild()),
				sendMessage);
		playerManager.loadItemOrdered(getGuildMusicManger(channel.getGuild()), trackUrl, alrh);
	}

	public static boolean isURI(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	public static PlayerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PlayerManager();
		}

		return INSTANCE;
	}

}
