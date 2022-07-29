package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Contact;
import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Не знаю как создана таблица, то нужен индекс что бы findBy работал быстро. Иначе будет full scan select
    @Query("SELECT c FROM Contact c WHERE c.group = :group")
    List<Contact> findByGroup(@Param("group") Group group, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :pattern, '%'))"
			+ "or LOWER(c.surname) LIKE LOWER(CONCAT('%', :pattern, '%'))"
			+ "or LOWER(c.phone) LIKE LOWER(CONCAT('%', :pattern, '%'))"
			+ "or LOWER(c.email) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<Contact> getContactsByPattern(@Param("pattern") String pattern, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.group = :group")
    long countByGroup(@Param("group") Group group);

    // jpa такие простые запросы умеет сам делать если правильно метод написать
    long countAllByGroup(Group group);
    
}
