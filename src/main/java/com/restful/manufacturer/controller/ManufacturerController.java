package com.restful.manufacturer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.entity.PageResponseDto;
import com.restful.manufacturer.exception.InvalidManufacturerException;
import com.restful.manufacturer.exception.ManufacturerNotFoundException;
import com.restful.manufacturer.service.ManufacturerService;

//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/manufacturer")
public class ManufacturerController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(ManufacturerController.class);
	
    @Autowired
    private ManufacturerService manufacturerService;
    
    public ManufacturerController() {
    	_LOGGER.info(">>> ManufacturerController LOADED. <<<");
    }
    
    @PostMapping
    	(
    			consumes = MediaType.APPLICATION_JSON_VALUE,
    			produces  = MediaType.APPLICATION_JSON_VALUE
    	)
    public ResponseEntity<Manufacturer> createManufacturer(@RequestBody Manufacturer manufacturer) {
    	_LOGGER.info(">>> Inside createManufacturer. <<<");
        if (manufacturer.getMfgName() == null || manufacturer.getMfgName().isBlank()) {
            throw new InvalidManufacturerException("Manufacturer name must not be empty");
        }

        Manufacturer saved = manufacturerService.saveOrUpdate(manufacturer);
        
        //return new ResponseEntity<>(saved, HttpStatus.CREATED);
        //Using builder for 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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
    public ResponseEntity<PageResponseDto<Manufacturer>> getAll(
    		@PageableDefault(size = 5, sort = "mfgName")
    	    Pageable pageable
    		) {
    	_LOGGER.info(">>> Inside getAll. <<<");
    	
    	Page<Manufacturer> page = manufacturerService.findAllManufacturers(pageable);
    	
    	PageResponseDto<Manufacturer> dto = new PageResponseDto<>();
    	
    	if(page != null) {
	    	dto.setContent(page.getContent());
	        dto.setTotalPages(page.getTotalPages());
	        dto.setTotalElements(page.getTotalElements());
	        dto.setPageNumber(page.getNumber());
	        dto.setPageSize(page.getSize());
    	}
    	
    	if (page != null && page.isEmpty()) {
            //throw new ManufacturerNotFoundException("No manufacturers found");
        }

        return ResponseEntity.ok(dto);
    	
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
    //@GetMapping("/search")
    //@GetMapping("/search/{mfgName}")
    //public ResponseEntity<List<Manufacturer>> searchByName(@RequestParam(name="mfgName", required=false) String manufacturerNameLike) {
    @GetMapping({
    	   "/search/{mfgName}"
    	})
    public ResponseEntity<PageResponseDto<Manufacturer>> searchByName(
    		@PathVariable String mfgName,
    		@PageableDefault(size = 5, sort = "mfgName")
    	    Pageable pageable) {
    	_LOGGER.info(">>> Inside searchByName. mfgName:<<<"+mfgName);
    	
    	if (mfgName == null || mfgName.isBlank()) {
            //throw new InvalidManufacturerException("Manufacturer name like must not be empty");
        }
    	
    	//int p = (page != null) ? page : 0;
    	//int s = (size != null) ? size : 5;
    	
    	//Pageable pageable = PageRequest.of(p, s);
    	
		Page<Manufacturer> page = null;
		if (mfgName == null || mfgName.isBlank()) {
			page = manufacturerService.findAllManufacturers(pageable);
		} else {
			page = manufacturerService.findByManufacturerNameLike(mfgName, pageable);
		}
		
		PageResponseDto<Manufacturer> dto = new PageResponseDto<>();
    	
    	if(page != null) {
	    	dto.setContent(page.getContent());
	        dto.setTotalPages(page.getTotalPages());
	        dto.setTotalElements(page.getTotalElements());
	        dto.setPageNumber(page.getNumber());
	        dto.setPageSize(page.getSize());
    	}
    	
    	if (page != null && page.isEmpty()) {
            //throw new ManufacturerNotFoundException("No manufacturers found for manufacturerNameLike: "+manufacturerNameLike);
        }

        //return ResponseEntity.ok(manufacturers);
        //return new ResponseEntity<>(manufacturers, HttpStatus.OK);
        
    	return new ResponseEntity<>(dto, HttpStatus.OK);
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
