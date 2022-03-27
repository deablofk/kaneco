package kaneco;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.api.Command;
import kaneco.commands.config.Config;
import kaneco.commands.levelling.Rank;
import kaneco.commands.moderation.Ban;
import kaneco.commands.moderation.Clear;
import kaneco.commands.moderation.LockChannel;
import kaneco.commands.moderation.MassBan;
import kaneco.commands.moderation.Mute;
import kaneco.commands.moderation.RemoveWarn;
import kaneco.commands.moderation.Slowmode;
import kaneco.commands.moderation.Unmute;
import kaneco.commands.moderation.Warn;
import kaneco.commands.moderation.Warns;
import kaneco.commands.music.Controls;
import kaneco.commands.music.Disconnect;
import kaneco.commands.music.LoopQueue;
import kaneco.commands.music.Move;
import kaneco.commands.music.NP;
import kaneco.commands.music.Play;
import kaneco.commands.music.Queue;
import kaneco.commands.music.Remove;
import kaneco.commands.music.Skip;
import kaneco.commands.user.Avatar;
import kaneco.commands.user.Help;
import kaneco.commands.user.ServerInfo;
import kaneco.data.GuildConfig;
import kaneco.data.PagedEmbed;
import kaneco.data.UserData;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class KanecoListener extends ListenerAdapter {

	public static HashMap<String, Command> COMMANDS = new HashMap<>();

	static {
		Play play = new Play();
		Remove remove = new Remove();
		Skip skip = new Skip();
		Move move = new Move();
		Disconnect disconnect = new Disconnect();

		COMMANDS.put("avatar", new Avatar());
		COMMANDS.put("serverinfo", new ServerInfo());
		COMMANDS.put("play", play);
		COMMANDS.put("queue", new Queue());
		COMMANDS.put("remove", remove);
		COMMANDS.put("skip", skip);
		COMMANDS.put("np", new NP());
		COMMANDS.put("move", move);
		COMMANDS.put("disconnect", disconnect);
		COMMANDS.put("loop", new LoopQueue());
		COMMANDS.put("controls", new Controls());
		COMMANDS.put("p", play);
		COMMANDS.put("rm", remove);
		COMMANDS.put("s", skip);
		COMMANDS.put("mv", move);
		COMMANDS.put("dc", disconnect);
		COMMANDS.put("warn", new Warn());
		COMMANDS.put("warns", new Warns());
		COMMANDS.put("mute", new Mute());
		COMMANDS.put("unmute", new Unmute());
		COMMANDS.put("rmwarn", new RemoveWarn());
		COMMANDS.put("lock", new LockChannel());
		COMMANDS.put("clear", new Clear());
		COMMANDS.put("help", new Help());
		COMMANDS.put("ban", new Ban());
		COMMANDS.put("config", new Config());
		COMMANDS.put("slowmode", new Slowmode());
		COMMANDS.put("rank", new Rank());
		COMMANDS.put("massban", new MassBan());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromGuild()) {
			if (event.getAuthor().isBot())
				return;

			String msg = event.getMessage().getContentRaw();
			GuildConfig cfg = null;
			try {
				cfg = Kaneco.configCache.get(event.getGuild().getIdLong());
			} catch (ExecutionException e) {
				System.out.println("onMessageReceived, error on getting guild config.");
			}

			if (msg.startsWith(cfg.getGuildPrefix())) {
				String[] args = msg.split(" ");
				String strCmd = args[0].replace(cfg.getGuildPrefix(), "").toLowerCase();
				if (COMMANDS.containsKey(strCmd)) {
					Command cmd = COMMANDS.get(strCmd);
					if ((args.length - 1) >= cmd.params()) {
						if (event.getMember().hasPermission(cmd.hasPermission()))
							cmd.runCommand(event.getMember(), event.getTextChannel(), event.getGuild(), args);
					}
				}
			}

			long memberID = event.getMember().getIdLong();
			if (Kaneco.cantGetXp.getIfPresent(memberID) == null) {
				try {
					Kaneco.cantGetXp.get(memberID);
					UserData userData = Kaneco.userCache.get(memberID);
					userData.setXp(userData.getXp() + KanecoUtils.randomXp(15, 45));
					Kaneco.userCache.put(memberID, userData);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (COMMANDS.containsKey(event.getName())) {
			Command cmd = COMMANDS.get(event.getName());
			if (event.getMember().hasPermission(cmd.hasPermission())) {
				String[] args = new String[event.getOptions().size() + 1];
				args[0] = "./" + event.getName();

				for (int i = 0; i < event.getOptions().size(); i++) {
					args[i + 1] = event.getOptions().get(i).getAsString();
				}

				cmd.setHook(event.getHook());
				event.deferReply(false).queue();
				cmd.runCommand(event.getMember(), event.getTextChannel(), event.getGuild(), args);
				cmd.setHook(null);
			}
		}
	}

	@Override
	public void onReady(ReadyEvent event) {
		JDA gd = event.getJDA();

		gd.upsertCommand("avatar", "mostra imagem do usuário.")
				.addOption(OptionType.USER, "user", "usuário a mostrar imagem.").queue();
		gd.upsertCommand("serverinfo", "mostra informações do servidor.").queue();

		gd.upsertCommand("play", "toca uma música.")
				.addOption(OptionType.STRING, "music", "url ou nome da música.", true).queue();
		gd.upsertCommand("queue", "mostra a fila de músicas.").queue();
		gd.upsertCommand("remove", "remove uma música da fila.")
				.addOption(OptionType.NUMBER, "posição", "número onde a música se encontra na fila.", true).queue();
		gd.upsertCommand("skip", "pula para a próxima música.")
				.addOption(OptionType.NUMBER, "quantia", "Quantidade de músicas a ser pulada.").queue();
		gd.upsertCommand("np", "mostra o tempo da música atual.").queue();
		gd.upsertCommand("move", "move uma música de posição.")
				.addOption(OptionType.NUMBER, "antiga", "posição atual da música", true)
				.addOption(OptionType.NUMBER, "nova", "nova posição da música", true).queue();
		gd.upsertCommand("disconnect", "desconecta o bot do canal de voz e limpa a queue.").queue();
		gd.upsertCommand("loop", "habilita modo repetição.").queue();
		gd.upsertCommand("controls", "Exibe controles de música.").queue();

		gd.upsertCommand("warn", "avisa o usuário sobre determinada infração.")
				.addOption(OptionType.USER, "user", "usuário a tomar warn", true)
				.addOption(OptionType.STRING, "motivo", "motivo do warn", true).queue();
		gd.upsertCommand("warns", "mostra os avisos que o usuário recebeu.")
				.addOption(OptionType.USER, "user", "usuário a tomar warn", true).queue();
		gd.upsertCommand("mute", "castiga o usuário por determinado tempo.")
				.addOption(OptionType.USER, "membro", "Membro a ser castígado.", true)
				.addOption(OptionType.STRING, "tempo", "Tempo de castigo, ex: 10m", true)
				.addOption(OptionType.STRING, "motivo", "Motivo do castigo.", true).queue();
		gd.upsertCommand("unmute", "desmuta um usuário castigado.")
				.addOption(OptionType.USER, "user", "usuário a ser desmutado", true).queue();
		gd.upsertCommand("rmwarn", "remove a warn de um usuário.")
				.addOption(OptionType.USER, "id", "número randomico da warn que aparece em /warns", true).queue();
		gd.upsertCommand("lock", "bloqueia/desbloqueia o canal para a role @everyone.")
				.addOption(OptionType.CHANNEL, "channel", "canal a ser bloqueado/desbloqueado").queue();
		gd.upsertCommand("clear", "deleta uma quantia de mensagens anteriores ao comando.")
				.addOption(OptionType.INTEGER, "quantia", "quantia de mensanges a serem deletadas", true).queue();
		gd.upsertCommand("help", "comando de ajuda do bot").queue();
		gd.upsertCommand("ban", "bane um determinado usuário.")
				.addOption(OptionType.USER, "user", "usuário a ser banido", true)
				.addOption(OptionType.STRING, "motivo", "motivo do banimento", true).queue();
		gd.upsertCommand("config", "mostra as configurações do servidor.").queue();
		gd.upsertCommand("slowmode", "aplica modo lento no canal.")
				.addOption(OptionType.INTEGER, "tempo", "Tempo no modo lento setado em segundos. Ex: 1").queue();
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		AudioChannel memberChannel = event.getMember().getVoiceState().getChannel();
		AudioChannel botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

		if (memberChannel != null && botChannel != null && memberChannel == botChannel) {
			if (Queue.interactiveMessages.containsKey(event.getMessage().getId())) {
				PagedEmbed pagedEmbed = Queue.interactiveMessages.get(event.getMessage().getId());
				switch (event.getButton().getId()) {
					case "prevpage":
						event.editMessageEmbeds(pagedEmbed.prevPage()).queue();
						break;
					case "nextpage":
						event.editMessageEmbeds(pagedEmbed.nextPage()).queue();
						break;
				}
			} else {
				EmbedBuilder eb = new EmbedBuilder(event.getMessage().getEmbeds().get(0));
				GuildMusicManager gmm = PlayerManager.getInstance().getGuildMusicManger(event.getGuild());
				String name = event.getUser().getName();
				String avatar = event.getMember().getEffectiveAvatarUrl();
				AudioTrack track = gmm.player.getPlayingTrack();
				switch (event.getButton().getId()) {
					case "next":
						eb.setFooter("Pulada por: " + name, avatar);
						gmm.scheduler.nextTrack();
						event.editMessageEmbeds(eb.build()).queue();
						break;
					case "playpause":
						eb.setFooter((gmm.player.isPaused() + " por: " + name).replace("true", "Despausado")
								.replace("false", "Pausado"), avatar);
						gmm.scheduler.pauseTrack();
						event.editMessageEmbeds(eb.build()).queue();
						break;
					case "loop":
						eb.setFooter(("Loop " + !gmm.scheduler.isLoopQueue() + " por: " + name)
								.replace("false", "desativado").replace("true", "ativado"), avatar);
						gmm.scheduler.setLoopQueue(!gmm.scheduler.isLoopQueue());
						event.editMessageEmbeds(eb.build()).queue();
						break;
					case "+10s":
						track.setPosition(track.getPosition() + 10000);
						eb.setFooter(name + " avançou 10s. Posição:" + (track.getPosition() / 1000) / 60 + ":"
								+ (track.getPosition() / 1000) % 60, avatar);
						event.editMessageEmbeds(eb.build()).queue();
						break;
					case "-10s":
						track.setPosition(track.getPosition() - 10000);
						eb.setFooter(name + " voltou 10s. Posição:" + (track.getPosition() / 1000) / 60 + ":"
								+ (track.getPosition() / 1000) % 60, avatar);
						event.editMessageEmbeds(eb.build()).queue();
						break;
				}
			}
		}
		event.deferEdit().queue();
	}

	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		if (event.getSelectMenu().getId().equals("helpmenu")) {
			String label = event.getSelectedOptions().get(0).getLabel();
			EmbedBuilder builder = new EmbedBuilder(event.getMessage().getEmbeds().get(0));
			switch (label) {
				case "music":
					builder.setTitle("Musica");
					builder.setDescription(
							"<:black_arrow_right:940445321652215829> play <url ou musica> - adiciona uma musica na queue.\n\n"
									+ "<:black_arrow_right:940445321652215829> disconnect - desconecta o bota da call.\n\n"
									+ "<:black_arrow_right:940445321652215829> move <posição anterior> <posição nova> - move uma música de posição.\n\n"
									+ "<:black_arrow_right:940445321652215829> queue - mostra as musicas e suas posições.\n\n"
									+ "<:black_arrow_right:940445321652215829> remove <posição>- remove uma musica da queue.\n\n"
									+ "<:black_arrow_right:940445321652215829> skip - pula para a próxima música\n\n"
									+ "<:black_arrow_right:940445321652215829> loop - ativa repetição da playlist atual.\n\n");
					break;
				case "moderation":
					builder.setTitle("Moderação");
					builder.setDescription(
							"<:black_arrow_right:940445321652215829> warn <@id> motivo - avisa um usuario sobre determinada infração.\n\n"
									+ "<:black_arrow_right:940445321652215829> warns <@id> - mostra os warns que o membro tem.\n\n"
									+ "<:black_arrow_right:940445321652215829> rmwarn <@id> <warn-id> - remove warn especificada do membro.\n\n"
									+ "<:black_arrow_right:940445321652215829> mute <@id> tempo motivo - castiga o usuario por determidado tempo.\n\n"
									+ "<:black_arrow_right:940445321652215829> unmute <@id> - remove o mute do usuário.\n\n"
									+ "<:black_arrow_right:940445321652215829> ban <@id> motivo - bane o usuario com menção ou id.\n\n"
									+ "<:black_arrow_right:940445321652215829> lock <#channel> - tranca o canal atual o canal mencionado.\n\n"
									+ "<:black_arrow_right:940445321652215829> clear <1-100> - limpa mensagens do canal.\n\n");
					break;
				case "users":
					builder.setTitle("Usuário");
					builder.setDescription(
							"<:black_arrow_right:940445321652215829> avatar <@id> - mostra o avatar de um usuário ou o seu.\n\n"
									+ "<:black_arrow_right:940445321652215829> userinfo <@id> - mostra informações sobre um usuário ou o seu.\n\n"
									+ "<:black_arrow_right:940445321652215829> serverinfo - mostra informações sobre o servidor.\n\n"
									+ "<:black_arrow_right:940445321652215829> help - exibe essa mensagem de ajuda.\n\n");
					break;
			}
			event.editMessageEmbeds(builder.build()).queue();
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		try {
			GuildConfig config = Kaneco.configCache.get(event.getGuild().getIdLong());
			if (config.getWelcomeMessage() != null) {
				TextChannel channel = event.getGuild().getTextChannelById(config.getWelcomeChannel());
				if (channel != null) {
					channel.sendMessage(event.getMember().getAsMention() + " " + config.getWelcomeMessage()).complete()
							.delete().queueAfter(30, TimeUnit.SECONDS);
				}
			}

			if (config.getWelcomeRole() != null) {
				Role role = event.getGuild().getRoleById(config.getWelcomeRole());
				if (role != null) {
					event.getGuild().addRoleToMember(event.getMember().getId(), role).queue();
				}
			}
		} catch (Exception e) {
			System.out.println("Error on welcome message or in welcome role");
		}
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		AudioChannel botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

		if (botChannel == null)
			return;

		if (botChannel != event.getChannelLeft())
			return;

		if (event.getChannelLeft().getMembers().size() > 1)
			return;

		GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(event.getGuild());
		manager.scheduler.purgeQueue();
		manager.scheduler.stop();
		manager.scheduler.setLoopQueue(false);
		event.getGuild().getAudioManager().closeAudioConnection();
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if (!event.getGuild().getAudioManager().isSelfDeafened()) {
			if (event.getMember().equals(event.getGuild().getSelfMember()))
				event.getMember().deafen(true).queue();
		}
	}

}
