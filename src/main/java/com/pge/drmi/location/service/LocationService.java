package com.pge.drmi.location.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;

@Service
public class LocationService {

	@Autowired
	private LocationRepository locationRepository;

	public String getLocation(int id) {

		Location location = locationRepository.findBylocationId(id);
		if (location != null) {
			return location.toString();
		}
		return null;
	}

	   public List<String> getDistinctSubLaps() {

	        return locationRepository.findDistinctSublapId();

	    }
}
