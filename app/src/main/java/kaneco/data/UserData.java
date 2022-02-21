package kaneco.data;

import java.util.List;

public class UserData {

	private long user;
	private String banurl;
	private List<WarnObject> warns;

	private int xp;
	private int level;
	private int messages;

	public UserData(long user) {
		this.user = user;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public String getBanurl() {
		return banurl;
	}

	public void setBanurl(String banurl) {
		this.banurl = banurl;
	}

	public List<WarnObject> getWarns() {
		return warns;
	}

	public void setWarns(List<WarnObject> warns) {
		this.warns = warns;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMessages() {
		return messages;
	}

	public void setMessages(int messages) {
		this.messages = messages;
	}

}
