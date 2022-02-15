package kaneco.music;

import java.time.OffsetDateTime;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class KanecoALRH implements AudioLoadResultHandler {

	private final String trackUrl;
	private final AudioChannel audioChannel;
	private final TextChannel channel;
	private final GuildMusicManager musicManager;
	private final Member member;
	private boolean sendMessage;
	private final InteractionHook hook;

	public KanecoALRH(String trackUrl, InteractionHook hook, TextChannel channel, Member member,
			GuildMusicManager musicManager, boolean sendMessage) {
		this.trackUrl = trackUrl;
		this.channel = channel;
		this.member = member;
		this.audioChannel = member.getVoiceState().getChannel();
		this.musicManager = musicManager;
		this.sendMessage = sendMessage;
		this.hook = hook;
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		if (hook != null) {
			hook.editOriginalEmbeds(KanecoUtils.defaultTrackEmbed(track, member).build())
					.setActionRow(KanecoUtils.defaultTrackButtons()).queue();
		} else {
			channel.sendMessageEmbeds(KanecoUtils.defaultTrackEmbed(track, member).build())
					.setActionRow(KanecoUtils.defaultTrackButtons()).queue();
		}
		channel.getGuild().getAudioManager().openAudioConnection(audioChannel);
		musicManager.scheduler.queue(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		if (trackUrl.contains("ytsearch:")) {
			AudioTrack track = playlist.getTracks().get(0);
			if (sendMessage) {
				if (hook == null) {
					channel.sendMessageEmbeds(KanecoUtils.defaultTrackEmbed(track, member).build())
							.setActionRow(KanecoUtils.defaultTrackButtons()).queue();
				} else {
					hook.editOriginalEmbeds(KanecoUtils.defaultTrackEmbed(track, member).build())
							.setActionRow(KanecoUtils.defaultTrackButtons()).queue();
				}
			}
			channel.getGuild().getAudioManager().openAudioConnection(audioChannel);
			musicManager.scheduler.queue(track);
		} else {
			channel.getGuild().getAudioManager().openAudioConnection(audioChannel);
			for (AudioTrack track : playlist.getTracks()) {
				musicManager.scheduler.queue(track);
			}
			if (hook == null) {
				channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Playlist carregada.")
						.setDescription("Foram inseridas: " + playlist.getTracks().size() + " musicas na fila.")
						.setTimestamp(OffsetDateTime.now()).build()).queue();
			} else {
				hook.editOriginalEmbeds(new EmbedBuilder().setTitle("Playlist carregada.")
						.setDescription("Foram inseridas: " + playlist.getTracks().size() + " musicas na fila.")
						.setTimestamp(OffsetDateTime.now()).build()).queue();
			}
		}
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		if (hook == null) {
			channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Erro ao carregar música.")
					.setDescription("Não foi possível carregar a música. ERRO:\n" + exception.getMessage()).build())
					.queue();
		} else {
			hook.editOriginalEmbeds(new EmbedBuilder().setTitle("Erro ao carregar música.")
					.setDescription("Não foi possível carregar a música. ERRO:\n" + exception.getMessage()).build())
					.queue();
		}
	}

	@Override
	public void noMatches() {
		if (hook == null) {
			channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Música não encontrada.")
					.setDescription("Não foi encontrada uma música para " + trackUrl).build()).queue();
		} else {
			hook.editOriginalEmbeds(new EmbedBuilder().setTitle("Música não encontrada.")
					.setDescription("Não foi encontrada uma música para " + trackUrl).build()).queue();
		}
	}
}
