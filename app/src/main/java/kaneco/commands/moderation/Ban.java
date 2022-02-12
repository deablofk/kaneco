package kaneco.commands.moderation;

import java.util.Arrays;

import kaneco.api.Command;
import kaneco.data.GuildConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Ban extends Command{

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();
		Member memberBan = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();

		if(memberBan != null) {
			guild.ban(memberBan, 0, "Banido by " + author.getNickname()).queue();
			embed.setDescription("Usuário **" + memberBan.getUser().getName() + "** foi banido por **" + author.getNickname() + "**");
			
			String motivo = String.join(" ", Arrays.copyOfRange(msgParams, 2, msgParams.length));
			EmbedBuilder ban = new EmbedBuilder().setTitle("Banimento | " + memberBan.getUser().getName()).addField("Motivo", motivo, false);

			GuildConfig cfg = config(guild);

			if(cfg.getBanChannel() != null) {
				TextChannel banChannel = guild.getTextChannelById(cfg.getBanChannel());
				if(banChannel != null) {
					banChannel.sendMessageEmbeds(ban.build()).queue();
				}
			}

			memberBan.getUser().openPrivateChannel().complete().sendMessageEmbeds(ban.build()).queue();
		}
		else {
			embed.setDescription("Usuário não encontrado");
		}

		if(hook() == null)
			channel.sendMessageEmbeds(embed.build()).queue();
		else
			hook().editOriginalEmbeds(embed.build()).queue();
	}

	@Override
	public int params() {
		return 1;
	}

	@Override
	public Permission hasPermission() {
		return Permission.BAN_MEMBERS;
	}
}

