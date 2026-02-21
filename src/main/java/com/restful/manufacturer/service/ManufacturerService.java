package com.restful.manufacturer.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.exception.InvalidManufacturerException;
import com.restful.manufacturer.exception.ServiceException;
import com.restful.manufacturer.repository.ManufacturerRepository;

@Service
public class ManufacturerService {
	private static final Logger _LOGGER = LoggerFactory.getLogger(ManufacturerService.class);
	@Autowired
    private ManufacturerRepository manufacturerRepository;

	@Transactional
    public Manufacturer saveOrUpdate(Manufacturer manufacturer) throws ServiceException {
		if (manufacturerRepository.findByMfgName(manufacturer.getMfgName()).isPresent()) {
	        throw new InvalidManufacturerException("Manufacturer name already exists!!!");
	    }
    	try {
    		return manufacturerRepository.save(manufacturer);
	    } catch (DataIntegrityViolationException e) {
	        throw new InvalidManufacturerException("Manufacturer name already exists!!!");
	    } catch (Exception e) {
	        _LOGGER.error("saveOrUpdate failed", e);
	        throw new ServiceException("Server error while saving Manufacturer");
	    }
    }
	
	@Transactional
    public Manufacturer update(Manufacturer manufacturer) throws ServiceException {
    	try {
    		Manufacturer existing =
    		        manufacturerRepository.findById(manufacturer.getManufacturerId())
    		        .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
    		
    		//Check existing and db product name.
    		if(!existing.getMfgName().equalsIgnoreCase(manufacturer.getMfgName())) {
    			//Check product name already exists.
    			if (manufacturerRepository.findByMfgName(manufacturer.getMfgName()).isPresent()) {
    		        throw new InvalidManufacturerException("Updated manufacturer name already exists!!!");
    		    }
    		}

		    existing.setMfgName(manufacturer.getMfgName());
		    existing.setAddress1(manufacturer.getAddress1());
		    existing.setAddress2(manufacturer.getAddress2());
		    existing.setCity(manufacturer.getCity());
		    existing.setState(manufacturer.getState());
		    existing.setZip(manufacturer.getZip());
		    existing.setZipExt(manufacturer.getZipExt());

		    return manufacturerRepository.save(existing);

	    } catch (DataIntegrityViolationException e) {
	        throw new InvalidManufacturerException("Updated manufacturer name already exists!!!");
	    } catch (Exception e) {
	        _LOGGER.error("saveOrUpdate failed", e);
	        throw new ServiceException("Server error while updating Manufacturer");
	    }
    }

    public Page<Manufacturer> findAllManufacturers(Pageable pageable) throws ServiceException {
    	try {
    		return manufacturerRepository.findAll(pageable);
    	} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findAllManufacturers."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findAllManufacturers."+exp.toString());
		}
    }
    
    public Page<Manufacturer> findByManufacturerNameLike(String mfgName, Pageable pageable) throws ServiceException {
    	try {
    		return manufacturerRepository.findByManufacturerNameLike(mfgName, pageable);
    	} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByManufacturerNameLike."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByManufacturerNameLike."+exp.toString());
		}
    }
    
	/**
	 * Retrieve by manufacturer id
	 * @param manufacturerId
	 * @return
	 */
	public Optional<Manufacturer> findByManufacturerId(Long manufacturerId) throws ServiceException {
		try {
	        return manufacturerRepository.findByManufacturerId(manufacturerId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByManufacturerId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByManufacturerId."+exp.toString());
		}
    }
	
	@Transactional
	public void deleteByManufacturerId(Long manufacturerId) throws ServiceException {
		try {
			if (!manufacturerRepository.existsById(manufacturerId)) {
		        throw new ServiceException("Manufacturer not found with id: " + manufacturerId);
		    }
			manufacturerRepository.deleteByManufacturerId(manufacturerId);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in deleteByManufacturerId."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in deleteByManufacturerId."+exp.toString());
		}
	}
	
	public Optional<Manufacturer> findByMfgName(String mfgName) throws ServiceException {
		try {
			return manufacturerRepository
					.findByMfgName(mfgName);
		} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findByMfgName."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findByMfgName."+exp.toString());
		}
	}
}
