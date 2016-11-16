package auth.permission.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import app.dao.UserDAO;
import app.dao.UserRoleDAO;
import app.entity.User;
import app.entity.UserRole;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDAO userRepository;

	@Autowired
	private UserRoleDAO userRoleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<User> users = userRepository.findByLogin(username, new PageRequest(0, 100)).getContent();
		if (users.isEmpty())
			throw new UsernameNotFoundException("Usuário não encontrado!");
		User user = users.get(0);

		Set<GrantedAuthority> authorities = new HashSet<>();

		Pageable pageable = new PageRequest(0, 100);
		List<UserRole> roles = userRoleRepository.findByLogin(user.getLogin(), pageable).getContent();
		for (UserRole userRole : roles) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.getRole().getName());
			authorities.add(grantedAuthority);
		}

		return new org.springframework.security.core.userdetails.User(user.getLogin(), "password", true, true, true,
				true, authorities);
	}

}
