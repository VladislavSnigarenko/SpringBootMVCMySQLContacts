package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Contact;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository.ContactRepository;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository.GroupRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;

    public ContactService(ContactRepository contactRepository, GroupRepository groupRepository) {
		this.contactRepository = contactRepository;
		this.groupRepository = groupRepository;
	}

	// Group section
    public void saveGroup(Group group) {
        groupRepository.save(group);
    }

    public List<Group> findGroups() {
        return (List<Group>) groupRepository.findAll();
    }

    public Group findGroupById(Long id) {
    	return groupRepository.findById(id).orElse(null);
    }    

    // Contact section
    public List<Contact> findByGroup(Group group, Pageable pageable) {
        return contactRepository.findByGroup(group, pageable);
    }

    public List<Contact> findAllContacts() {
    	return contactRepository.findAll();
    }

    public Contact saveContact(Contact contact) {
    	return contactRepository.save(contact);
    }

    public void deleteContact(Contact contact) {
    	contactRepository.delete(contact);
    }

    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }    
    
    public Optional<Contact> findContactById(Long id) {
    	return contactRepository.findById(id);
    }    
    
    public Contact getContactById(Long id) {
    	return contactRepository.findById(id).orElseGet(null);
    }    
    
    public boolean contactExistsById(Long id) {
    	return contactRepository.existsById(id);
    }    
    
    public List<Contact> getContactsByPattern(String keyword, Pageable pageable) {
    	return contactRepository.getContactsByPattern(keyword, pageable);
    }

    public List<Contact> getContactsByPattern(String keyword) {
    	return contactRepository.getContactsByPattern(keyword);
    }

    public long countContacts() {
        return contactRepository.count();
    }
    
    public long countContacts(String keyword) {
    	return contactRepository.getContactsByPattern(keyword, null).size();
    }
    
    public long countContactsByGroup(Group group) {
        return contactRepository.countByGroup(group);
    }

    public List<Contact> getAllContacts(Pageable pageable) {
    	return contactRepository.findAll(pageable).getContent();
    }
    
}
