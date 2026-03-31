package com.restful.manufacturer.service;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restful.manufacturer.repository.PaymentRepository;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

@Service
public class PaymentService {

	private static final Logger _LOGGER = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private PaymentRepository repository;
	
	 // Resilience4j registries
    @Autowired
    private RetryRegistry retryRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;


	/*@RateLimiter(name = "rateLimiterService")
	@CircuitBreaker(name = "myServiceCircuit", fallbackMethod = "fallbackMethod")
	@Retry(name = "retryService", fallbackMethod = "fallbackMethod")
	public String circuitBreakDemo() {

		_LOGGER.info(">>> Inside circuitBreakDemo. <<<");

		return repository.processPayment();
	}

	// Fallback method
	@Retry(name="retryService", fallbackMethod="fallbackMethod")
	public String fallbackMethod(Throwable ex) {
		_LOGGER.info(">>> Inside fallbackMethod. <<<");
		return "Fallback response: Service temporarily unavailable";
	} */
	
	// Programmatic method
    public String circuitBreakerDemoProgrammatic() {
        Retry retry = retryRegistry.retry("retryService");
        io.github.resilience4j.circuitbreaker.CircuitBreaker cb =
                circuitBreakerRegistry.circuitBreaker("circuitBreakerService");
        io.github.resilience4j.ratelimiter.RateLimiter rl =
                rateLimiterRegistry.rateLimiter("rateLimiterService");

        // Compose: RateLimiter -> Retry -> CircuitBreaker -> repository
        Callable<String> decorated = io.github.resilience4j.ratelimiter.RateLimiter.decorateCallable(rl,
                io.github.resilience4j.retry.Retry.decorateCallable(retry,
                        io.github.resilience4j.circuitbreaker.CircuitBreaker.decorateCallable(cb, repository::processPayment)
                )
        );

        try {
            return decorated.call();
        } catch (Exception ex) {
            _LOGGER.info(">>> Inside fallbackMethod <<<");
            return "Fallback response: Service temporarily unavailable";
        }
    }
}
