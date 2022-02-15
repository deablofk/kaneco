package kaneco.data;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.TextChannel;

public class GuildConfig {

	private String guild;
	private String owner;
	private List<TextChannel> blackListChannles = new ArrayList<>();
	private String prefix;
	private String welcomeMessage;
	private String welcomeChannel;
	private String welcomeRole;
	private String muteRole;
	private String banChannel;
	private List<String> alertRoles = new ArrayList<>();

	public GuildConfig(String guildId, String ownerId, String prefix) {
		this.guild = guildId;
		this.owner = ownerId;
		this.prefix = prefix;
	}

	public String getGuildId() {
		return guild;
	}

	public String getOwnerId() {
		return owner;
	}

	public List<TextChannel> getBlackListChannles() {
		return blackListChannles;
	}

	public boolean addBlackListChannel(TextChannel txtChannel) {
		if (!blackListChannles.contains(txtChannel)) {
			blackListChannles.add(txtChannel);
			return true;
		}

		return false;
	}

	public String getGuildPrefix() {
		return prefix;
	}

	public void setStringPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	public String getWelcomeChannel() {
		return welcomeChannel;
	}

	public void setWelcomeChannel(String welcomeChannel) {
		this.welcomeChannel = welcomeChannel;
	}

	public String getWelcomeRole() {
		return welcomeRole;
	}

	public void setWelcomeRole(String welcomeRole) {
		this.welcomeRole = welcomeRole;
	}

	public String getMuteRole() {
		return muteRole;
	}

	public void setMuteRole(String muteRole) {
		this.muteRole = muteRole;
	}

	public String getBanChannel() {
		return banChannel;
	}

	public void setBanChannel(String banChannel) {
		this.banChannel = banChannel;
	}

	public List<String> getAlertRoles() {
		return alertRoles;
	}

	public void setAlertRoles(List<String> alertRoles) {
		this.alertRoles = alertRoles;
	}
}
