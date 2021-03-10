package in.trident.crdr.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;

/**
 * @author Nandhakumar Subramanian
 */

public class CustomUserDetails implements UserDetails {


	private static final long serialVersionUID = 1L;

	private User user;
	
	public CustomUserDetails(User user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		System.out.println("getting Authorities..");
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			for (Role role : roles) {
				System.out.println("Roles/Autorities: " +role.getRoleName());
				authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
			}
			System.out.println("got Authorities..");
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

	public String getFullName () {
		return user.getFirstName()+" "+user.getLastName();
	}
}
