package com.pge.drmi.config;

import org.springframework.data.repository.CrudRepository;

public interface ConfigurationRepository extends CrudRepository<Configuration, Integer>{
	
	 Configuration findByProcessName(String status);

}
