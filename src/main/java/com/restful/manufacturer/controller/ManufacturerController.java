package com.restful.manufacturer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.exception.InvalidManufacturerException;
import com.restful.manufacturer.exception.ManufacturerNotFoundException;
import com.restful.manufacturer.service.ManufacturerService;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
@RequestMapping("/api/manufacturer")
public class ManufacturerController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(ManufacturerController.class);
	
    @Autowired
    private ManufacturerService manufacturerService;
    
    public ManufacturerController() {
    	_LOGGER.info(">>> ManufacturerController LOADED. <<<");
    }
    
    @PostMapping
    public ResponseEntity<Manufacturer> createManufacturer(@RequestBody Manufacturer manufacturer) {
    	_LOGGER.info(">>> Inside createManufacturer. <<<");
        if (manufacturer.getMfgName() == null || manufacturer.getMfgName().isBlank()) {
            throw new InvalidManufacturerException("Manufacturer name must not be empty");
        }

        Manufacturer saved = manufacturerService.saveOrUpdate(manufacturer);
        
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		_LOGGER.info(">>> Inside deleteManufacturer. <<<");		
		
		if (id == null || id <= 0) {
			throw new InvalidManufacturerException("Manufacturer id must not be empty");
		}
		
		try {
			manufacturerService.deleteByManufacturerId(id);
			return ResponseEntity.ok("Manufacturer deleted successfully.");		
	    } catch (InvalidManufacturerException ex) {
	        // ID not found in DB
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Manufacturer not found with id: " + id);
	    } catch (Exception ex) {
	        _LOGGER.error("Error deleting manufacturer with id {}: {}", id, ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Unexpected error occurred: " + ex.getMessage());
	    }
		
	}

    @GetMapping
    public ResponseEntity<List<Manufacturer>> getAll() {
    	_LOGGER.info(">>> Inside getAll. <<<");
    	List<Manufacturer> manufacturers = manufacturerService.findAllManufacturers();
    	
    	if (manufacturers.isEmpty()) {
            //throw new ManufacturerNotFoundException("No manufacturers found");
        }

        return ResponseEntity.ok(manufacturers);
    	
    }
    
    @GetMapping("/id/{manufacturerId}")
	public ResponseEntity<Manufacturer> getById(@PathVariable Long manufacturerId) {
    	_LOGGER.info(">>> Inside getById. <<<");
    	
    	if (manufacturerId == null || (manufacturerId != null && manufacturerId.intValue() <= 0)) {
			throw new InvalidManufacturerException("Manufacturer id must not be empty");
		}
    	
		Manufacturer manufacturer = manufacturerService.findByManufacturerId(manufacturerId)
				 .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found with id: " + manufacturerId));
		return ResponseEntity.ok(manufacturer);
    }
    
    //@GetMapping("/search/{manufacturerNameLike}")
    //public ResponseEntity<List<Manufacturer>> searchByName(@PathVariable String manufacturerNameLike) {
    @GetMapping("/search")
    public ResponseEntity<List<Manufacturer>> searchByName(@RequestParam(name="mfgName", required=false) String manufacturerNameLike) {
    	_LOGGER.info(">>> Inside searchByName. <<<");
    	
    	if (manufacturerNameLike == null || manufacturerNameLike.isBlank()) {
            //throw new InvalidManufacturerException("Manufacturer name like must not be empty");
        }
    	
		List<Manufacturer> manufacturers = null;
		if (manufacturerNameLike == null || manufacturerNameLike.isBlank()) {
			manufacturers = manufacturerService.findAllManufacturers();
		} else {
			manufacturers = manufacturerService.findByManufacturerNameLike(manufacturerNameLike);
		}
    	
    	if (manufacturers.isEmpty()) {
            //throw new ManufacturerNotFoundException("No manufacturers found for manufacturerNameLike: "+manufacturerNameLike);
        }

        return ResponseEntity.ok(manufacturers);
    }
	
    @GetMapping("/name/{manufacturerName}")
	public ResponseEntity<Manufacturer> getByName(@PathVariable String manufacturerName) {
		_LOGGER.info(">>> Inside getByName. <<<");	
		
		if (manufacturerName == null || manufacturerName.isBlank()) {
            throw new InvalidManufacturerException("Manufacturer name must not be empty");
        }
		
		Manufacturer manufacturer = manufacturerService.findByMfgName(manufacturerName)
				 .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found with manufacturerName: " + manufacturerName));
		return ResponseEntity.ok(manufacturer);
	}
}
