package kaneco.commands.moderation;

import java.util.ArrayList;
import java.util.List;

import kaneco.api.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionContainer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class LockChannel extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();
		Role everyioneRole = guild.getPublicRole();
		TextChannel lockChannel = channel;

		if (msgParams.length > 1) {
			lockChannel = guild.getTextChannelById(msgParams[1].replaceAll("[<#>]", ""));
		}

		if (lockChannel == null) {
			sendMessageEmbeds(channel, embed.setDescription("Canal não encontrado").build());
			return;
		}

		IPermissionContainer container = lockChannel.getPermissionContainer();
		PermissionOverride po = lockChannel.getPermissionOverride(everyioneRole);

		List<Permission> perms = new ArrayList<>();
		if (po != null && po.getDenied().contains(Permission.MESSAGE_SEND)) {
			if (po.getDenied().contains(Permission.VIEW_CHANNEL)) {
				perms.add(Permission.VIEW_CHANNEL);
			}

			container.putPermissionOverride(everyioneRole).setAllow(Permission.MESSAGE_SEND).setDeny(perms).queue();
			embed.setDescription("Canal " + lockChannel.getAsMention() + " desbloqueado.");
		} else {
			perms.add(Permission.MESSAGE_SEND);
			if (po.getDenied().contains(Permission.VIEW_CHANNEL)) {
				perms.add(Permission.VIEW_CHANNEL);
			}
			container.upsertPermissionOverride(everyioneRole).setDeny(perms).queue();
			embed.setDescription("Canal " + lockChannel.getAsMention() + " bloqueado.");
		}

		sendMessageEmbeds(channel, embed.build());
	}

	@Override
	public Permission hasPermission() {
		return Permission.MANAGE_CHANNEL;
	}
}
