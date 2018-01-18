package com.pge.drmi.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom")
public class LSEMapping {
	private Map<String, String> pathMapper;

	public Map<String, String> getPathMapper() {
		return pathMapper;
	}

	public void setPathMapper(Map<String, String> pathMapper) {
		this.pathMapper = pathMapper;
	}

}
