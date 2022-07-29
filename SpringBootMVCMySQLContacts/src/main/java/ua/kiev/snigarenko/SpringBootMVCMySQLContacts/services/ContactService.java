package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Contact;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository.ContactRepository;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository.GroupRepository;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private GroupRepository groupRepository;

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

    public void saveContact(Contact contact) {
    	contactRepository.save(contact);
    }

    public void deleteContact(Contact contact) {
    	contactRepository.delete(contact);
    }

    // есть find to Optional<>
    public Contact getContactById(Long id) {
    	return contactRepository.findById(id).orElse(null);
    }    

    // метод с малой буквы
    public boolean contactExistsById(Long id) {
    	return contactRepository.existsById(id);
    }    
    
    public List<Contact> getContactsByPattern(String keyword, Pageable pageable) {
    	return contactRepository.getContactsByPattern(keyword, pageable);
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
