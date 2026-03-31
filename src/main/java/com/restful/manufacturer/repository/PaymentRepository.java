package com.restful.manufacturer.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

	private static final Logger _LOGGER = LoggerFactory.getLogger(PaymentRepository.class);

	private int counter = 0;

	public String processPayment() {

		_LOGGER.info(">>> Inside processPayment. <<<");
		
		//_LOGGER.info(">>> Inside counter. <<<"+counter);

		counter++;

		// Simulate failure every alternate call
		if (counter % 2 == 0) {
			throw new RuntimeException("Database failure!");
		}

		return "Payment processed successfully";
	}
}