package kaneco.commands.music;

import java.time.OffsetDateTime;
import java.util.Arrays;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

public class Play extends Command {
	
	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
        if(author.getVoiceState().getChannel() != null) {
			String trackUrl = String.join(" ", Arrays.copyOfRange(msgParams, 1, msgParams.length));

			if(trackUrl.contains("spotify.com")) {
				String spot = trackUrl.replace("https://open.spotify.com/", "");
				String[] linkData = spot.substring(0, spot.indexOf("?si")).split("/");
			
				switch(linkData[0]) {
					case "track":
						try { 
							String name = Kaneco.spotifyApi.getTrack(linkData[1]).build().execute().getName();
							PlayerManager.getInstance().loadAndPlay(hook(), channel, author, "ytsearch:" +name, true);
						} catch (Exception e) {};
						break;
					case "playlist":
						try {
							PlaylistTrack[] playlistTracks = Kaneco.spotifyApi.getPlaylistsItems(linkData[1]).build().execute().getItems();

							EmbedBuilder embed = new EmbedBuilder();
							embed.setTitle("Playlist carregada.");
							embed.setDescription("Foram inseridas: " + playlistTracks.length + " músicas na fila.");
							embed.setTimestamp(OffsetDateTime.now());


							if(hook() == null){
								channel.sendMessageEmbeds(embed.build()).queue();
							}
							else {
								hook().editOriginalEmbeds(embed.build()).queue();
							}

							for(int i = 0; i < playlistTracks.length; i++) {
								PlayerManager.getInstance().loadAndPlay(hook(), channel, author, "ytsearch:" + playlistTracks[i].getTrack().getName(), false);
							}
						}
						catch(Exception e) { e.printStackTrace(); }
						break;
				}
			}
			else if(!PlayerManager.isURI(trackUrl)){
				PlayerManager.getInstance().loadAndPlay(hook(), channel, author, "ytsearch:" + trackUrl, true);
			}
			else {
				PlayerManager.getInstance().loadAndPlay(hook(), channel, author, trackUrl, true);
			}

            PlayerManager.getInstance().getGuildMusicManger(channel.getGuild()).scheduler.setTextChannel(channel);
        }
        else {
			if(hook() == null)
				channel.sendMessageEmbeds(new EmbedBuilder().setDescription("É necessário que você esteja em um canal de voz.").build()).queue();
			else
				hook().editOriginalEmbeds(new EmbedBuilder().setDescription("É necessário que você esteja em um canal de voz.").build()).queue();
        }
	}
	
	@Override
	public int params() {
		return 1;
	}

}
