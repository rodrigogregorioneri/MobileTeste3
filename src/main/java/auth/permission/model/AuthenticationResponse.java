package auth.permission.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AuthenticationResponse {

	private String username;
	private String token;
	private Long expires;

	public AuthenticationResponse() {
		super();
	}

	public AuthenticationResponse(String username, String token, Long expires) {
		this.setUsername(username);
		this.setToken(token);
		this.setExpires(expires);
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
