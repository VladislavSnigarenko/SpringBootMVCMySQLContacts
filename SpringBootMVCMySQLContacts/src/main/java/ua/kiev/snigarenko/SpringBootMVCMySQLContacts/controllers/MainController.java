package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.MainServiceConfiguration;

@Controller
public class MainController {

	@Autowired 
	private MainServiceConfiguration mainService;
	
	@RequestMapping
	public String mainPage(Model model) {
		model.addAttribute("welcomeMessage", mainService.getWelcomeMessage());
		return "index";
	}
	
	@RequestMapping("about")
	public String about() {
		return "about";
	}
	
}
