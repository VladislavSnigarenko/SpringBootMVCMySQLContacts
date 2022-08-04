package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.exception.ContactNotFoundException;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Contact;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository.ContactRepository;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services.ContactService;

@RestController
@RequestMapping("/api")
public class ContactRestController {

	private final ContactService contactService;
	
	public ContactRestController(ContactService contactService, ContactRepository contactRepository) {
		this.contactService = contactService;
	}

	@GetMapping
	public String listContact(@RequestParam(required = false) String keyword){
		List<Contact> contacts;
		if (keyword != null) {
			contacts = contactService.getContactsByPattern(keyword);  
		} else {
			contacts = contactService.findAllContacts();  
		}
		return contacts.toString();  
	}
	
	@GetMapping("/{id}")
	Contact oneContact(@PathVariable Long id){
		return contactService.findContactById(id).
				orElseThrow(() -> new ContactNotFoundException(id));
		
	}

	@PostMapping
	public void createContact(@RequestBody Contact contact)  
	{  
		contactService.saveContact(contact);  
	}
	
	@DeleteMapping("/{id}")
	public void deleteContact(@PathVariable Long id){
		if(contactService.contactExistsById(id)){
			contactService.deleteById(id);
		}else {
			throw new ContactNotFoundException(id);
		}
	}

	@PutMapping(value = "{id}")
	public Contact updateContact(@RequestBody Contact newContact, @PathVariable Long id){
		 return contactService.findContactById(id)
			      .map(contact -> {
			    	  contact.setName(newContact.getName());
			    	  contact.setSurname(newContact.getSurname());
			    	  contact.setPhone(newContact.getPhone());
			    	  contact.setEmail(newContact.getEmail());
			    	  contact.setGroup(newContact.getGroup());
			    	  return contactService.saveContact(contact);
			      })
			      .orElseGet(() -> {
			    	  newContact.setId(id);
			    	  return contactService.saveContact(newContact);
			      });
	}	
	
}
