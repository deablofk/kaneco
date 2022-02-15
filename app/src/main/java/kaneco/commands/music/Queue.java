package kaneco.commands.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import kaneco.utils.KanecoUtils;
import kaneco.data.PagedEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Queue extends Command {

	public static HashMap<String, PagedEmbed> interactiveMessages = new HashMap<>();

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);
		List<AudioTrack> songs = new ArrayList<>(manager.scheduler.getQueue());
		StringBuilder messages = new StringBuilder();
		PagedEmbed pagedEmbed = new PagedEmbed();

		if (manager.player.getPlayingTrack() == null) {
			messages.append("** Musica Atual: Nenhuma**\n\n");
		} else {
			messages.append("**Musica Atual: **[" + manager.player.getPlayingTrack().getInfo().title + "]("
					+ manager.player.getPlayingTrack().getInfo().uri + ")\n\n");
		}

		if (songs.isEmpty()) {
			messages.append("Queue Vazia\n");
			sendMessageEmbeds(channel, KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Queue").build());

			sendMessageEmbeds(channel, new EmbedBuilder().setDescription(messages.toString()).build());
		} else {
			List<List<AudioTrack>> smallerSongsList = Lists.partition(songs, 10);
			int counter = 0;
			for (int i = 0; i < smallerSongsList.size(); i++) {
				List<AudioTrack> currentList = smallerSongsList.get(i);
				StringBuilder smallList = new StringBuilder();
				smallList.append(messages.toString());
				smallList.append("Total de músicas: " + songs.size() + "\n\n");
				smallList.append("Posição - Link\n\n");
				for (int j = 0; j < currentList.size(); j++) {
					smallList.append(counter + " - [" + currentList.get(j).getInfo().title + "]("
							+ currentList.get(j).getInfo().uri + ")\n");
					counter++;
				}
				pagedEmbed.addPage(new EmbedBuilder().setDescription(smallList.toString()).build());
			}
			List<ItemComponent> comps = new ArrayList<>();
			comps.add(Button.primary("prevpage", "prev page"));
			comps.add(Button.primary("nextpage", "next page"));
			String messageId = null;

			if (hook() == null)
				messageId = channel.sendMessageEmbeds(pagedEmbed.getCurrentPage()).setActionRow(comps).complete()
						.getId();
			else
				messageId = hook().editOriginalEmbeds(pagedEmbed.getCurrentPage()).setActionRow(comps).complete()
						.getId();

			interactiveMessages.put(messageId, pagedEmbed);
		}
	}
}
