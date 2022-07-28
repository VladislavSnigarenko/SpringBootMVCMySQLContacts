package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Contact;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.ContactService;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.MainService;

@Controller
@RequestMapping("/contact")
public class ContactController {

	@Autowired
	MainService mainService;
	
	@Autowired
	ContactService contactService; 
	
	@RequestMapping
	public String contactList(Model model, @RequestParam(required = false, defaultValue = "1") Integer page) {
		
    	final int ITEMS_PER_PAGE = mainService.getItemsPerPage();

	    PageRequest pageRequest = PageRequest.of(page - 1, ITEMS_PER_PAGE, Sort.Direction.ASC, "name");
	    List<Contact> contacts;
	    Long countContacts;
	    contacts = contactService.getAllContacts(pageRequest);
	    countContacts = contactService.countContacts();
	
		model.addAttribute("groups", contactService.findGroups());
	    model.addAttribute("contacts", contacts);

	    Long totalCount = contactService.countContacts();
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("allPages", getPageCount(totalCount));
	    
	    model.addAttribute("onPageItems", contacts.size());
	    model.addAttribute("totalItems", countContacts);
	    model.addAttribute("startPage", 1);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", getPageCount(countContacts));
		
		return "contacts-list";
	}

	@RequestMapping("/add")
	public String contactAddPage(Model model) {
		model.addAttribute("groups", contactService.findGroups());
		model.addAttribute("contact", new Contact());
		return "add-contact";
	}

    @RequestMapping(value="/add", method = RequestMethod.POST)
	public String contactAdd(@RequestParam(value = "group") long groupId, 
							@ModelAttribute("contact") Contact contact, Model model) {

        final int DEFAULT_GROUP_ID = mainService.getDefaultGroupId();
    	
        Group group = (groupId != DEFAULT_GROUP_ID) ? contactService.findGroupById(groupId) : null;
        contact.setGroup(group);
		try {
			contactService.saveContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/contact";
	}

	@RequestMapping("/{id}/edit")
	public String contactEdit(@PathVariable(value = "id") Long id, Model model) {
		if (!contactService.ContactExistsById(id)) {
			return "redirect:/contact";
		}
		model.addAttribute("contact", contactService.findContactById(id));
	    model.addAttribute("groups", contactService.findGroups());
	
		return "edit-contact";
	}

	@PostMapping("/save")
	public String contactSave(@ModelAttribute("contact") Contact contact){
		try {
			contactService.saveContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/contact";
	}	
	
	@RequestMapping("/{id}/delete")
	public String contactDelete(@PathVariable(value = "id") Long id) {
		if (!contactService.ContactExistsById(id)) {
			return "redirect:/contact";
		}
		try {
			Contact contact = contactService.findContactById(id);  
			contactService.deleteContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/contact";
	}
    
	@RequestMapping("/group/{id}")
	public String listGroup(@PathVariable(value = "id") Long groupId,
			@RequestParam(required = false, defaultValue = "1") Integer page, Model model) {

        final int DEFAULT_GROUP_ID = mainService.getDefaultGroupId();
    	final int ITEMS_PER_PAGE = mainService.getItemsPerPage();
		
		if(groupId == DEFAULT_GROUP_ID){
			return "redirect:/contact";
		}
		
        Group group = contactService.findGroupById(groupId);
		PageRequest pageRequest = PageRequest.of(page - 1, ITEMS_PER_PAGE, Sort.Direction.ASC, "name");
				
		List<Contact> contacts = contactService.findByGroup(group, pageRequest);
	    Long countContacts = contactService.countContactsByGroup(group);

		model.addAttribute("groups", contactService.findGroups());
	    model.addAttribute("contacts", contacts);
	    model.addAttribute("groupId", groupId);

        model.addAttribute("byGroupPages", getPageCount(contactService.countContactsByGroup(group)));
	    
	    model.addAttribute("onPageItems", contacts.size());
	    model.addAttribute("totalItems", countContacts);
	    model.addAttribute("startPage", 1);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", getPageCount(countContacts));
		
	    return "contacts-list";
	}

	@RequestMapping("/search")
	public String contactsSearch(@RequestParam String keyword, Model model) {
		model.addAttribute("groups", contactService.findGroups());
	    model.addAttribute("contacts", contactService.getContactsByPattern(keyword, null));
	    return "contacts-list";
	}
	
	@PostMapping("/delete_all")
	public ResponseEntity<Void> contactDeleteAll(@RequestBody JSONObject jsonObject){

		ArrayList<String> datablock = (ArrayList<String>) jsonObject.get("block");
		datablock.forEach(element -> {
			if (contactService.ContactExistsById(Integer.valueOf(element).longValue())) {
				try {
					contactService.deleteContact(contactService.findContactById(Integer.valueOf(element).longValue()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return new ResponseEntity<>(HttpStatus.OK);
	}
	    
    private long getPageCount(long totalCount) {
    	final int ITEMS_PER_PAGE = mainService.getItemsPerPage();
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }
	
}
