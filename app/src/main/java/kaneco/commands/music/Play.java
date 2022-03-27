package kaneco.commands.music;

import java.time.OffsetDateTime;
import java.util.Arrays;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.music.PlayerManager;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class Play extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		AudioChannel authorChannel = author.getVoiceState().getChannel();
		AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();

		if (authorChannel == null) {
			sendMessageEmbeds(channel, KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Play")
					.setDescription("É necessário que você esteja em um canal de voz.").build());
			return;
		}
		if (botChannel != null && authorChannel != botChannel) {
			sendMessageEmbeds(channel, KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Play")
					.setDescription("É necessário que você esteja no mesmo canal do bot.").build());
			return;
		}

		String trackUrl = String.join(" ", Arrays.copyOfRange(msgParams, 1, msgParams.length));

		if (trackUrl.contains("spotify.com")) {
			String spot = trackUrl.replace("https://open.spotify.com/", "");
			String[] linkData = spot.substring(0, spot.indexOf("?si")).split("/");

			switch (linkData[0]) {
				case "track":
					try {
						Track track = Kaneco.spotifyApi.getTrack(linkData[1]).build().execute();
						PlayerManager.getInstance().loadAndPlay(hook(), channel, author,
								"ytsearch:" + track.getArtists()[0].getName() + track.getName(), true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case "playlist":
					try {
						PlaylistTrack[] playlistTracks = Kaneco.spotifyApi.getPlaylistsItems(linkData[1]).build()
								.execute().getItems();

						String[] ids = new String[playlistTracks.length];
						for (int i = 0; i < playlistTracks.length; i++) {
							ids[i] = playlistTracks[i].getTrack().getId();
						}
						Track[] tracks = Kaneco.spotifyApi.getSeveralTracks(ids).build().execute();

						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("Playlist carregada.");
						embed.setDescription("Foram inseridas: " + tracks.length + " músicas na fila.");
						embed.setTimestamp(OffsetDateTime.now());

						sendMessageEmbeds(channel, embed.build());

						for (int i = 0; i < tracks.length; i++) {
							PlayerManager.getInstance().loadAndPlay(hook(), channel, author,
									"ytsearch:" + tracks[i].getArtists()[0].getName() + " " + tracks[i].getName(),
									false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case "album":
					try {
						TrackSimplified[] playlistTracks = Kaneco.spotifyApi.getAlbumsTracks(linkData[1]).build()
								.execute().getItems();

						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("Playlist carregada.");
						embed.setDescription("Foram inseridas: " + playlistTracks.length + " músicas na fila.");
						embed.setTimestamp(OffsetDateTime.now());

						sendMessageEmbeds(channel, embed.build());

						for (int i = 0; i < playlistTracks.length; i++) {
							PlayerManager.getInstance().loadAndPlay(hook(), channel, author, "ytsearch:"
									+ playlistTracks[i].getArtists()[0].getName() + " " + playlistTracks[i].getName(),
									false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			}
		} else if (!PlayerManager.isURI(trackUrl)) {
			PlayerManager.getInstance().loadAndPlay(hook(), channel, author, "ytsearch:" + trackUrl, true);
		} else {
			if(trackUrl.contains("/shorts/"))
				trackUrl = trackUrl.replace("youtube.com/shorts/", "youtu.be/");

			PlayerManager.getInstance().loadAndPlay(hook(), channel, author, trackUrl, true);
		}

		PlayerManager.getInstance().getGuildMusicManger(channel.getGuild()).scheduler.setTextChannel(channel);
	}

	@Override
	public int params() {
		return 1;
	}

}
