package com.restful.manufacturer.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restful.manufacturer.entity.Manufacturer;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
	// Find by manufacturer name
	Optional<Manufacturer> findByMfgName(String mfgName);
    
	Optional<Manufacturer> findByManufacturerId(Long id);
	
	void deleteByManufacturerId(Long id);
    
    @Query("""
    	    SELECT m FROM Manufacturer m
    	    WHERE LOWER(m.mfgName) LIKE LOWER(CONCAT('%', :mfgName, '%'))
    	""")
    	Page<Manufacturer> findByManufacturerNameLike(@Param("mfgName") String mfgName, Pageable pageable);
}
