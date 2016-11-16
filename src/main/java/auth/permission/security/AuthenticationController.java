package auth.permission.security;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.dao.UserDAO;
import app.entity.User;
import auth.permission.model.AuthenticationRequest;
import auth.permission.model.AuthenticationResponse;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private String tokenHeader = TokenUtils.AUTH_HEADER_NAME;


	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserDAO userRepository;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> authenticationRequest(@RequestBody AuthenticationRequest authenticationRequest,
			Device device) throws AuthenticationException {

		String formPassword = authenticationRequest.getPassword();

		List<User> users = userRepository.findByLogin(authenticationRequest.getUsername(), new PageRequest(0, 100))
				.getContent();

		if (users.isEmpty())
			throw new UsernameNotFoundException("User not Found!");

		User user = users.get(0);
		if (passwordEncoder.matches(formPassword, user.getPassword())) {

			// Reload password post-authentication so we can generate token
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
			String token = this.tokenUtils.generateToken(userDetails, device);
      Date expires = this.tokenUtils.getExpirationDateFromToken(token);
			// Add security context
			SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities()));

			// Return the token
			return ResponseEntity.ok(new AuthenticationResponse(user.getLogin(), token, expires.getTime()));
		} else {
			throw new BadCredentialsException("Invalid Password!");
		}
	}

	@RequestMapping(value = "refresh", method = RequestMethod.GET)
	public ResponseEntity<?> authenticationRequest(HttpServletRequest request) {
		String token = request.getHeader(this.tokenHeader);
		Date expires = this.tokenUtils.getExpirationDateFromToken(token);
		if (this.tokenUtils.canTokenBeRefreshed(token, expires)) {
			String refreshedToken = this.tokenUtils.refreshToken(token);
		  expires = this.tokenUtils.getExpirationDateFromToken(token);
		  String username = this.tokenUtils.getUsernameFromToken(token);
			return ResponseEntity.ok(new AuthenticationResponse(username, refreshedToken, expires.getTime()));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

}
