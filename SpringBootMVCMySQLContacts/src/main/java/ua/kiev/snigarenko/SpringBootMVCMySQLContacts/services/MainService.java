package ua.kiev.snigarenko.SpringBootMVCMySQLContacts.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class MainService {

	@Value("${welcome.message}")
	private String welcomeMessage;

	@Value("${items.per.page}")
	private int itemsPerPage;

	@Value("${default.group.id}")
	private int defaultGroupId;
	
	public String getWelcomeMessage(){
		return welcomeMessage; 
	}
	
	public int getItemsPerPage(){
		return itemsPerPage; 
	}		

	public int getDefaultGroupId(){
		return defaultGroupId; 
	}		
	
}
