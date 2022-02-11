package kaneco.commands.moderation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.data.WarnObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Warn extends Command{

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();
		String reason = String.join(" ", Arrays.copyOfRange(msgParams, 2, msgParams.length));

		Member member = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
		embed.setTitle("Warn |" + member.getUser().getName());
		embed.addField("Motivo", reason, false);
		if(member != null) {
			WarnObject obj = new WarnObject(member.getId(), guild.getId(), author.getId(), reason, null);
			Kaneco.restApi.sendWarn(obj);

			member.getUser().openPrivateChannel().complete().sendMessageEmbeds(embed.build()).queue();

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

				embed.addField("TotalWarns", "" + warnRoles, false);
			}
		}

		if(hook() == null){
			channel.sendMessageEmbeds(embed.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
		}
		else {
			hook().editOriginalEmbeds(embed.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
		}
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
