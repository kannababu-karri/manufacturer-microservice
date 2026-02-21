package com.restful.manufacturer.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.entity.PageResponseDto;
import com.restful.manufacturer.exception.InvalidManufacturerException;
import com.restful.manufacturer.exception.ManufacturerNotFoundException;
import com.restful.manufacturer.service.ManufacturerService;
import com.restful.manufacturer.utils.ILConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/manufacturer")
@CrossOrigin(origins = {ILConstants.ANGULAR_URL_DEV, ILConstants.ANGULAR_URL_PROD})
public class ManufacturerController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(ManufacturerController.class);
	
    @Autowired
    private ManufacturerService manufacturerService;
    
    public ManufacturerController() {
    	_LOGGER.info(">>> ManufacturerController LOADED. <<<");
    }
    
    @PostMapping({ "", "/" })
    public ResponseEntity<Manufacturer> create(@Valid @RequestBody Manufacturer manufacturer) {

        _LOGGER.info(">>> Inside createManufacturer. <<<");

        Manufacturer saved = manufacturerService.saveOrUpdate(manufacturer);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Manufacturer> update(@PathVariable Long id,
                                               @RequestBody Manufacturer manufacturer) {
        manufacturer.setManufacturerId(id);
        Manufacturer updated = manufacturerService.update(manufacturer);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		_LOGGER.info(">>> Inside deleteManufacturer. <<<");		
		
		if (id == null || id <= 0) {
			throw new InvalidManufacturerException("Manufacturer id must not be empty");
		}
		
		Map<String, String> response = new HashMap<>();
		
		try {
			manufacturerService.deleteByManufacturerId(id);
			
			
		    response.put("message", "Deleted successfully");
			
		    return ResponseEntity.ok(response);
		    
	    } catch (InvalidManufacturerException ex) {
	    	_LOGGER.error("Error deleting manufacturer InvalidManufacturerException with id {}: {}", id, ex.getMessage());
	        response.put("status", HttpStatus.NOT_FOUND.toString());
	        response.put("message", "Manufacturer not found with id: " + id);
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(response);
	    } catch (Exception ex) {
	        _LOGGER.error("Error deleting manufacturer Exception with id {}: {}", id, ex.getMessage());
	        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.put("message", "Unexpected error occurred: " + ex.getMessage());
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(response);
	    }
		
	}

    @GetMapping({"", "/"})
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
    
    @GetMapping({"/search", "/search/{mfgName}"})
    public ResponseEntity<PageResponseDto<Manufacturer>> searchByName(
    		@PathVariable String mfgName,
    		@PageableDefault(size = 5, sort = "mfgName")
    	    Pageable pageable) {
    	_LOGGER.info(">>> Inside searchByName. mfgName:<<<"+mfgName);
    	
    	if (mfgName == null || mfgName.isBlank()) {
            //throw new InvalidManufacturerException("Manufacturer name like must not be empty");
        }
    	
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
