package kaneco.commands.user;

import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ServerInfo extends Command {
	@Override
	public void runCommand(Member author, TextChannel channel, Guild gd, String[] msgParams) {
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, gd.getSelfMember(), " ServerInfo");
		eb.setTitle(gd.getName());
		eb.setThumbnail(gd.getIconUrl());
		if(!gd.getBannerUrl().isBlank())
			eb.setImage(gd.getBannerUrl() + "?size=1024");
		eb.addField("ID", gd.getId(), true);
		eb.addField("👑Dono", gd.getOwner().getEffectiveName(), true);
		eb.addField("Criado em", gd.getTimeCreated().toString().replace("T", "  "), true);
		eb.addField("Membros", gd.getMemberCount()+"", true);
		eb.addField("Canais de Texto", gd.getTextChannels().size()+"", true);
		eb.addField("Canais de Voz", gd.getVoiceChannels().size()+"", true);

		sendMessageEmbeds(channel, eb.build());
	}	
}
