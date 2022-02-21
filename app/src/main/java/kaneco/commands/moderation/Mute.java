package kaneco.commands.moderation;

import java.util.concurrent.TimeUnit;

import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Mute extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();
		Member memberMute = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
		TimeUnit unit = KanecoUtils.getTimeUnitFromStr(msgParams[2]);
		long time = Long.parseLong(msgParams[2].replaceAll("[^\\d]", ""));
		GuildConfig config = config(guild);

		if (time == 0) {
			sendMessageEmbeds(channel, embed.setDescription("O tempo não pode ser igual a 0").build());
			return;
		}

		guild.timeoutFor(memberMute, time, unit).complete();
		embed.setDescription(
				"Usuario " + memberMute.getAsMention() + " mutado por **" + time + " " + unit.name() + "**");
		sendMessageEmbeds(channel, embed.build());

		if (config.getMuteRole() == null) {
			return;
		}

		Role muteRole = guild.getRoleById(config.getMuteRole());

		if (muteRole == null) {
			return;
		}

		guild.addRoleToMember(memberMute, muteRole).queue();
		guild.removeRoleFromMember(memberMute, muteRole).queueAfter(time, unit);
	}

	@Override
	public int params() {
		return 2;
	}

	@Override
	public Permission hasPermission() {
		return Permission.MODERATE_MEMBERS;
	}

}
