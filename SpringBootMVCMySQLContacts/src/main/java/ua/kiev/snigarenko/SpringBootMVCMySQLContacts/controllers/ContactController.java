package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Contact;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.ContactService;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.MainServiceConfiguration;

@Controller
@RequestMapping("/contact")
public class ContactController {

	@Autowired // в общем @Autowired не рекомендуется, советую прочитать про альтернативу
	MainServiceConfiguration mainServiceConfiguration;
	
	@Autowired
	ContactService contactService; 
	
	@RequestMapping
	public String contactList(Model model, @RequestParam(required = false, defaultValue = "1") Integer page) {
		
    	final int ITEMS_PER_PAGE = mainServiceConfiguration.getItemsPerPage();

	    PageRequest pageRequest = PageRequest.of(page - 1, ITEMS_PER_PAGE, Sort.Direction.ASC, "name");
		List<Contact> contacts = contactService.getAllContacts(pageRequest);
	    long countContacts = contactService.countContacts();
	
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

		// в общем такую логику лучше делать в service
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
			return "error"; // такое делается с помощью ControllerAdvice
		}
		return "redirect:/contact";
	}	
	
	@RequestMapping("/{id}/delete")
	public String contactDelete(@PathVariable(value = "id") Long id) {
		if (!contactService.contactExistsById(id)) {
			return "redirect:/contact";
		}
		try {
			Contact contact = contactService.getContactById(id);
			contactService.deleteContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			return "error"; // такое делается с помощью ControllerAdvice
		}
		return "redirect:/contact";
	}
    
	@RequestMapping("/group/{id}")
	public String listGroup(@PathVariable(value = "id") Long groupId,
			@RequestParam(required = false, defaultValue = "1") Integer page, Model model) {

        final int DEFAULT_GROUP_ID = mainServiceConfiguration.getDefaultGroupId();
    	final int ITEMS_PER_PAGE = mainServiceConfiguration.getItemsPerPage();
		
		if(groupId == DEFAULT_GROUP_ID){
			return "redirect:/contact";
		}
		
        Group group = contactService.findGroupById(groupId);
		PageRequest pageRequest = PageRequest.of(page - 1, ITEMS_PER_PAGE, Sort.Direction.ASC, "name");
				
		List<Contact> contacts = contactService.findByGroup(group, pageRequest);
	    long countContacts = contactService.countContactsByGroup(group); // всегда используем примитивы где можно

		model.addAttribute("groups", contactService.findGroups());
	    model.addAttribute("contacts", contacts);
	    model.addAttribute("groupId", groupId);

		// дубликат вызова contactService.countContactsByGroup(group)
        model.addAttribute("byGroupPages", getPageCount(countContacts));
	    
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

	// тут виду помешаны котреллены для фронта и реста. такие вещи лучше разделить
	@PostMapping("/delete_all")
//	@DeleteMapping(value = "/delete_all", consumes = MediaType.APPLICATION_JSON_VALUE) для операции delete есть отдельный REST метод, плюс можно явно указать что мы принимаем json
	public ResponseEntity<Void> contactDeleteAll(@RequestBody JSONObject jsonObject){
		// @RequestBody JSONObject jsonObject - плохой вариант, можно сделать @RequestBody ВащJavaТип body, и спринг сам десериализироет json на обьект java
		// в таком случае ArrayList<String>) jsonObject.get("block") будет не нужно
		List<String> datablock = (ArrayList<String>) jsonObject.get("block");
		datablock.forEach(element -> {
			// если нам нужен long то можно пулучить сразу long
			long contactId = Long.parseLong(element);
			if (contactService.contactExistsById(contactId)) {
				try {
					contactService.deleteContact(contactService.getContactById(contactId)); // плюс не считаем два раза а выносим в отдельную переменную
				} catch (Exception e) {
					e.printStackTrace(); // в этом try catch нет смысла. советую прочитать про ControllerAdvise в Spring. С помощью него можно организовать обработку ошибок (например вывод в консоль)
				}
			}
		});
		return new ResponseEntity<>(HttpStatus.OK);
	}
	    
    private long getPageCount(long totalCount) {
    	final int ITEMS_PER_PAGE = mainServiceConfiguration.getItemsPerPage();
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }
	
}
