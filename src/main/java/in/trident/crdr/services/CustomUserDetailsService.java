package in.trident.crdr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.UserRepository;


public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(username);
		System.out.println("finding user from repositories...");
		if (user == null) {
			System.out.println("user not found");
			throw new UsernameNotFoundException("User not found");
		}
		return new CustomUserDetails(user);	
	}

}
