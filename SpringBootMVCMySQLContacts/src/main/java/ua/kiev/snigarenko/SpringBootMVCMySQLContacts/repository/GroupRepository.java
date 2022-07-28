package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import ua.kiev.snigarenko.SpringBootMVCMySQLContacts.models.Group;

public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {
    
}
