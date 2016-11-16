package auth.permission.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AuthenticationRequest  {

	private String username;
	private String password;

	public AuthenticationRequest() {
		super();
	}

	public AuthenticationRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
