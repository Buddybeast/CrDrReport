package in.trident.crdr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.UserRepository;

@Controller
public class AppController {
	
	//TODO Implement Email notification of login feature -- Spring Email 
	//TODO Implement Session management via cookies
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("")
	public String showHomePage() {
		return "index";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user",new User());
		return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
		String encodedPass = passEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);
		userRepo.save(user);
		//TODO Check if user already exists by email id
		return "register_success";
	}
	@GetMapping("/users")
	public String listUsers(Model model) {
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList",userList);
		return "users";
	}
}
