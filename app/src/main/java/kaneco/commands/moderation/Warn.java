package kaneco.commands.moderation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.data.WarnObject;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Warn extends Command{

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		Member member = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Warn"); 

		if(member == null) {
			sendMessageEmbeds(channel, eb.setDescription("Usuário não encontrado.").build());
			return;
		}

		String reason = String.join(" ", Arrays.copyOfRange(msgParams, 2, msgParams.length));
		eb.setTitle(member.getUser().getName());
		eb.addField("Servidor:", guild.getName(), false);
		eb.addField("Motivo:", reason, false);

		WarnObject obj = new WarnObject(member.getId(), guild.getId(), author.getId(), reason, null);
		Kaneco.restApi.sendWarn(obj);
		member.getUser().openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();

		GuildConfig cfg = config(guild);
		if(!cfg.getAlertRoles().isEmpty()){
			List<Role> memberRoles = member.getRoles();
			List<String> alertRolesId = cfg.getAlertRoles();

			int warnRoles = 0;

			for(int i = 0; i < memberRoles.size(); i++) {
				for(int j = 0; j < alertRolesId.size(); j++) {
					if(memberRoles.get(i).getId().equals(alertRolesId.get(j))) {
						warnRoles++;
					}
				}
			}
			
			if( warnRoles != 3){
				Role warnRole = guild.getRoleById(alertRolesId.get(warnRoles));
				if(warnRole != null){
					guild.addRoleToMember(member, warnRole).queue();
				}
			}
		}

		if(hook() == null)
			channel.sendMessageEmbeds(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
		else 
			hook().editOriginalEmbeds(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
	}

	@Override
	public int params() {
		return 1;
	}

	@Override
	public Permission hasPermission() {
		return Permission.MODERATE_MEMBERS;
	}

}
