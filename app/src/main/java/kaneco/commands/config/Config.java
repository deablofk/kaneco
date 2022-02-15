package kaneco.commands.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Config extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {

		GuildConfig cfg = config(guild);
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Config");

		if (msgParams.length > 1) {
			boolean updated = false;
			String subcmd = msgParams[1].toLowerCase();
			switch (subcmd) {
				case "welcome":
					String subsubcmd = msgParams[2].toLowerCase();
					switch (subsubcmd) {
						case "msg":
							if (msgParams.length > 3) {
								String msg = String.join(" ", Arrays.copyOfRange(msgParams, 3, msgParams.length));
								cfg.setWelcomeMessage(msg);
								updated = true;
								eb.setDescription("Mensagem de boas vindas setadas para: " + msg);
							} else {
								eb.setDescription(
										"É necessário que você escreva as mensagens de boas vindas no comando.");
							}
							break;
						case "channel":
							if (msgParams.length > 3) {
								TextChannel welcomeChannel = guild
										.getTextChannelById(msgParams[3].replaceAll("[<#>]", ""));
								if (welcomeChannel != null) {
									cfg.setWelcomeChannel(welcomeChannel.getId());
									updated = true;
									eb.setDescription(
											"O canal de boas vindas foi setado para " + welcomeChannel.getAsMention());
								} else {
									eb.setDescription("O canal especificado não foi encontrado.");
								}
							} else {
								eb.setDescription("É necessário que você especifique o canal de boas vindas.");
							}
							break;
						case "role":
							if (msgParams.length > 3) {
								Role role = guild.getRoleById(msgParams[3].replaceAll("[<@&>]", ""));
								if (role != null) {
									cfg.setWelcomeRole(role.getId());
									updated = true;
									eb.setDescription("O cargo de boas vindas foi setado para: " + role.getAsMention());
								} else {
									eb.setDescription("O cargo especificado não foi encontrado.");
								}
							} else {
								eb.setDescription("É necessário que você especifique um cargo de boas vindas.");
							}
							break;
						default:
							eb.setDescription("O subcomando " + subsubcmd + " não foi encontrado.");
							break;
					}
					break;
				case "banchannel":
					if (msgParams.length > 2) {
						TextChannel banChannel = guild.getTextChannelById(msgParams[2].replaceAll("[<#>]", ""));
						if (banChannel != null) {
							cfg.setBanChannel(banChannel.getId());
							updated = true;
							eb.setDescription("O canal de banimentos foi setado para: " + banChannel.getAsMention());
						} else {
							eb.setDescription("O canal específicado não foi encontrado.");
						}
					} else {
						eb.setDescription("É necessário que você específique um canal para banimentos.");
					}
					break;
				case "muterole":
					if (msgParams.length > 2) {
						Role role = guild.getRoleById(msgParams[2].replaceAll("[<@&>]", ""));
						if (role != null) {
							cfg.setMuteRole(role.getId());
							updated = true;
							eb.setDescription("O cargo de mute foi setado para: " + role.getAsMention());
						} else {
							eb.setDescription("O cargo específicado não foi encontrado.");
						}
					} else {
						eb.setDescription("É necessário que você específique o cargo para servir como mute.");
					}
					break;
				case "alertroles":
					if (msgParams.length == 5) {
						Role role1 = guild.getRoleById(msgParams[2].replaceAll("[<@&>]", ""));
						Role role2 = guild.getRoleById(msgParams[3].replaceAll("[<@&>]", ""));
						Role role3 = guild.getRoleById(msgParams[4].replaceAll("[<@&>]", ""));

						if (role1 != null && role2 != null && role3 != null) {
							List<String> alertRoles = new ArrayList<>(3);
							alertRoles.add(role1.getId());
							alertRoles.add(role2.getId());
							alertRoles.add(role3.getId());
							cfg.setAlertRoles(alertRoles);
							updated = true;
							eb.setDescription("Alert Roles: " + role1.getAsMention() + " " + role2.getAsMention() + " "
									+ role3.getAsMention());
						} else {
							eb.setDescription(
									"Um dos cargos específicados não existe ou não foi encontrado. É necessário haver 1 espaço entre cada cargo.");
						}
					} else {
						eb.setDescription(
								"É necessário que você especifique todos os 3 cargos de alerta com 1 espaço entre cada.");
					}
					break;
				case "prefix":
					if (msgParams.length > 2) {
						String newPrefix = msgParams[2];
						if (!newPrefix.equals(cfg.getGuildPrefix())) {
							if (newPrefix.length() > 0 && newPrefix.length() < 5) {
								cfg.setStringPrefix(newPrefix);
								updated = true;
								eb.setDescription("O prefixo de comando foi setado para: " + newPrefix);
							} else {
								eb.setDescription("O novo prefixo não pode ser menor que 1 e maior que 4");
							}
						} else {
							eb.setDescription("O novo prefixo deve ser diferente do prefixo atual.");
						}
					} else {
						eb.setDescription("É necessário que você especifique um prefixo de tamanho 1~4");
					}
					break;
				case "blacklist":
					// if(msgParams.length > 2){
					// TextChannel banChannel =
					// guild.getTextChannelById(msgParams[2].replaceAll("[<#>]", ""));
					// if(banChannel != null){
					// cfg.setBanChannel(banChannel.getId());
					// updated = true;
					// eb.setDescription("O canal de banimentos foi setado para: " +
					// banChannel.getAsMention());
					// }
					// else {
					// eb.setDescription("O canal específicado não foi encontrado.");
					// }
					// }
					// else {
					// eb.setDescription("É necessário que você específique um canal para
					// banimentos.");
					// }
					break;
			}

			if (updated) {
				Kaneco.restApi.putGuildConfig(cfg);
				Kaneco.configCache.refresh(guild.getId());
			}
		} else {
			StringBuilder builder = new StringBuilder();

			builder.append("\n<:black_arrow_right:940445321652215829> **Prefixo do Servidor:**\n" + cfg.getGuildPrefix()
					+ "\n");
			builder.append(
					"\n<:black_arrow_right:940445321652215829> **Cargo de Mute:**\n<@&" + cfg.getMuteRole() + ">\n");
			builder.append("\n<:black_arrow_right:940445321652215829> **Canal de Banimentos:**\n<#"
					+ cfg.getBanChannel() + ">\n");
			builder.append("\n<:black_arrow_right:940445321652215829> **Canal de boas vindas:**\n<#"
					+ cfg.getWelcomeChannel() + ">\n");
			builder.append("\n<:black_arrow_right:940445321652215829> **Mensagem de boas-vindas:**\n"
					+ cfg.getWelcomeMessage() + "\n");
			builder.append("\n<:black_arrow_right:940445321652215829> **Cargo de boas-vindas:**\n<@&"
					+ cfg.getWelcomeRole() + ">\n");

			List<String> alertRoles = cfg.getAlertRoles();
			String rolesInLine = "";
			if (alertRoles != null) {
				for (int i = 0; i < alertRoles.size(); i++) {
					rolesInLine += " <@&" + alertRoles.get(i) + ">";
				}
			} else {
				rolesInLine = "Nenhum";
			}
			builder.append("\n<:black_arrow_right:940445321652215829> **Cargos de Alerta:**\n" + rolesInLine);

			eb.setTitle("Configurações Atuais");
			if (guild.getIconUrl() != null) {
				eb.setThumbnail(guild.getIconUrl());
			}
			if (guild.getBannerUrl() != null) {
				eb.setImage(guild.getBannerUrl() + "?size=1024");
			}
			String desc = builder.toString().replace("<@&null>", "Nenhum").replace("<#null>", "Nenhum").replace("null",
					"Nenhum");
			eb.setDescription(desc);
		}

		if (hook() != null) {
			hook().editOriginalEmbeds(eb.build()).queue();
		} else {
			channel.sendMessageEmbeds(eb.build()).queue();
		}
	}

	@Override
	public Permission hasPermission() {
		return Permission.ADMINISTRATOR;
	}
}
