package com.enterprise.products.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restful.manufacturer.entity.Manufacturer;
import com.restful.manufacturer.repository.ManufacturerRepository;
import com.restful.manufacturer.service.ManufacturerService;

@ExtendWith(MockitoExtension.class)
public class ManufacturerServiceMockitoTest {
	@InjectMocks
	private ManufacturerService manufacturerService;

	@Mock
	private ManufacturerRepository manufacturerRepository;

	@Test
	void testFindByMfgName() {
		//AWS Developer – Associate (DVA-C02)
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setManufacturerId(new Long(6));
		manufacturer.setMfgName("Manufacturer1");

		when(manufacturerRepository.findByMfgName("Manufacturer1")).thenReturn(Optional.of(manufacturer));

		Optional<Manufacturer> manufacturerResult = manufacturerService.findByMfgName("Manufacturer1");

		assertEquals("Manufacturer1", manufacturerResult.get().getMfgName());
	}
}
