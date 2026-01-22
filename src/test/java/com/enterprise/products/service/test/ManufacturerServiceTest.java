package com.enterprise.products.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.repository.ManufacturerRepository;
import com.restful.manufacturer.service.ManufacturerService;

@SpringBootTest
public class ManufacturerServiceTest {
	@Autowired
    private ManufacturerService manufacturerService;

    @MockBean
    private ManufacturerRepository manufacturerRepository;
    
    @Test
    void testFindByMfgName() {
    	Manufacturer manufacturer = new Manufacturer();
    	manufacturer.setManufacturerId(new Long(6));
    	manufacturer.setMfgName("Manufacturer1");

        Mockito.when(manufacturerRepository.findByMfgName("Manufacturer1"))
               .thenReturn(Optional.of(manufacturer));

        Optional<Manufacturer> manufacturerResult = manufacturerService.findByMfgName("Manufacturer1");

        assertEquals("Manufacturer1", manufacturerResult.get().getMfgName());
    }
}
