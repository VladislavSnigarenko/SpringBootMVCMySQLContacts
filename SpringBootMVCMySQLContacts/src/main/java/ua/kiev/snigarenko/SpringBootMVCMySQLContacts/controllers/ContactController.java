package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.MainServiceConfiguration;

@Controller
@RequestMapping("/contact")
public class ContactController {

	private final String redirectURL = "redirect:/contact"; 

	private final MainServiceConfiguration mainServiceConfiguration;
	private final ContactService contactService;

	public ContactController(MainServiceConfiguration mainServiceConfiguration, ContactService contactService) {
		this.mainServiceConfiguration = mainServiceConfiguration;
		this.contactService = contactService;
	}

	@RequestMapping
	public String contactList(Model model, @RequestParam(required = false, defaultValue = "1") Integer page) {
		
    	final int ITEMS_PER_PAGE = mainServiceConfiguration.getItemsPerPage();

	    PageRequest pageRequest = PageRequest.of(page - 1, ITEMS_PER_PAGE, Sort.Direction.ASC, "name");
	    List<Contact> contacts;
	    long countContacts;
	    contacts = contactService.getAllContacts(pageRequest);
	    countContacts = contactService.countContacts();
	
		model.addAttribute("groups", contactService.findGroups());
	    model.addAttribute("contacts", contacts);

	    long totalCount = contactService.countContacts();
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

        final int DEFAULT_GROUP_ID = mainServiceConfiguration.getDefaultGroupId();
    	
        Group group = (groupId != DEFAULT_GROUP_ID) ? contactService.findGroupById(groupId) : null;
        contact.setGroup(group);
		try {
			contactService.saveContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return redirectURL;
	}

	@RequestMapping("/{id}/edit")
	public String contactEdit(@PathVariable(value = "id") long id, Model model) {
		if (!contactService.contactExistsById(id)) {
			return "redirect:/contact";
		}
		model.addAttribute("contact", contactService.getContactById(id));
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
		return redirectURL;
	}	
	
	@RequestMapping("/{id}/delete")
	public String contactDelete(@PathVariable(value = "id") long id) {
		if (!contactService.contactExistsById(id)) {
			return redirectURL;
		}
		contactService.deleteById(id);
		return redirectURL;
	}
    
	@RequestMapping("/group/{id}")
	public String listGroup(@PathVariable(value = "id") long groupId,
			@RequestParam(required = false, defaultValue = "1") Integer page, Model model) {

        final int DEFAULT_GROUP_ID = mainServiceConfiguration.getDefaultGroupId();
    	final int ITEMS_PER_PAGE = mainServiceConfiguration.getItemsPerPage();
		
		if(groupId == DEFAULT_GROUP_ID){
			return redirectURL;
		}
		
        Group group = contactService.findGroupById(groupId);
		PageRequest pageRequest = PageRequest.of(page - 1, ITEMS_PER_PAGE, Sort.Direction.ASC, "name");
				
		List<Contact> contacts = contactService.findByGroup(group, pageRequest);
	    long countContacts = contactService.countContactsByGroup(group);

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
	
	@PostMapping(value = "/delete_all", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> contactDeleteAll(@RequestBody List<Integer> Ids){
		Ids.forEach((element) -> {
			if(contactService.contactExistsById(element.longValue())) {
				contactService.deleteById(element.longValue());
			}else {
				System.out.println("Not founf Contact with ID = " + element);
			}				
		});
		return new ResponseEntity<>(HttpStatus.OK);
	}
	    
    private long getPageCount(long totalCount) {
    	final int ITEMS_PER_PAGE = mainServiceConfiguration.getItemsPerPage();
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }
	
}
