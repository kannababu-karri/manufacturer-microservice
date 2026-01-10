package com.restful.manufacturer.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.exception.ServiceException;
import com.restful.manufacturer.repository.ManufacturerRepository;

@Service
public class ManufacturerService {
	private static final Logger _LOGGER = LoggerFactory.getLogger(ManufacturerService.class);
	@Autowired
    private ManufacturerRepository manufacturerRepository;

	@Transactional
    public Manufacturer saveOrUpdate(Manufacturer manufacturer) throws ServiceException {
    	try {
    		return manufacturerRepository.save(manufacturer);
    	} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in saveOrUpdate."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in saveOrUpdate."+exp.toString());
		}
    }

    public List<Manufacturer> findAllManufacturers() throws ServiceException {
    	try {
    		return manufacturerRepository.findAll();
    	} catch (Exception exp) {
			_LOGGER.error("ERROR: Service Exception occured in findAllManufacturers."+exp.toString());	
			throw new ServiceException("ERROR: Service Exception occured in findAllManufacturers."+exp.toString());
		}
    }
    
    public List<Manufacturer> findByManufacturerNameLike(String mfgName) throws ServiceException {
    	try {
    		return manufacturerRepository.findByManufacturerNameLike(mfgName);
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
