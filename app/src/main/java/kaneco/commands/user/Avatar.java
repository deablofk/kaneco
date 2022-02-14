package kaneco.commands.user;

import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Avatar extends Command{

	@Override
	public void runCommand(Member au, TextChannel ch, Guild gd, String[] msgParams) {
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(au, gd.getSelfMember(), " Avatar");
		if(msgParams.length > 1) {
			Member member = gd.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
			eb.setImage(member.getEffectiveAvatarUrl() + "?size=1024");
		}
		else {
			eb.setImage(au.getEffectiveAvatarUrl() + "?size=1024");
		}

		sendMessageEmbeds(ch, eb.build());
	}

}
