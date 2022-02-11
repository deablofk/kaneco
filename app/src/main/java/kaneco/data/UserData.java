package kaneco.data;

import java.util.List;

public class UserData {

	private String user;
	private String banurl;
	private List<WarnObject> warns;

	public UserData(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
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

}
