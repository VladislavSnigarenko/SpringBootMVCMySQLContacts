package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.ContactService;


@Controller
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private ContactService contactService;
	
	@RequestMapping("/add")
	public String contactList(Model model) {
		model.addAttribute("group", new Group());
		return "add-group";
	}
	
	@PostMapping("/add")
	public String groupAddPost(@ModelAttribute("Group") Group group, Model model) {
		contactService.saveGroup(group);
		return "redirect:/contact";
	}
	
}
