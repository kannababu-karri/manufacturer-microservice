package com.restful.manufacturer.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;

@Configuration
public class Resilience4jMonitoringConfig {

	private static final Logger _LOGGER = LoggerFactory.getLogger(Resilience4jMonitoringConfig.class);
	
	// Thread-safe list to store logs
    private final List<String> logEvents = new CopyOnWriteArrayList<>();

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @PostConstruct
    public void registerEventListeners() {

        // --------------------------
        // CircuitBreaker Events
        // --------------------------
        circuitBreakerRegistry.getAllCircuitBreakers()
            .forEach(cb -> cb.getEventPublisher()
                .onStateTransition(event -> addLog("CIRCUIT BREAKER '" + event.getCircuitBreakerName() + "' STATE CHANGE: " + event.getStateTransition()))
                .onCallNotPermitted(event -> addLog("CIRCUIT BREAKER '" + event.getCircuitBreakerName() + "' CALL BLOCKED (OPEN)"))
                .onError(event -> addLog("CIRCUIT BREAKER '" + event.getCircuitBreakerName() + "' ERROR: " + event.getThrowable()))
                .onSuccess(event -> addLog("CIRCUIT BREAKER '" + event.getCircuitBreakerName() + "' SUCCESS"))
            );

        // --------------------------
        // Retry Events
        // --------------------------
        retryRegistry.getAllRetries().forEach(retry -> {
            retry.getEventPublisher()
                .onRetry(event -> addLog("RETRY '" + event.getName() + "' ATTEMPT #" + event.getNumberOfRetryAttempts()));
        });

        // --------------------------
        // RateLimiter Events
        // --------------------------
        rateLimiterRegistry.getAllRateLimiters().forEach(rl -> rl.getEventPublisher()
            .onSuccess(event -> addLog("RATE LIMITER '" + event.getRateLimiterName() + "' SUCCESSFUL ACQUIRE"))
            .onFailure(event -> addLog("RATE LIMITER '" + event.getRateLimiterName() + "' EXCEEDED LIMIT"))
        );

        addLog("✅ Resilience4j Monitoring Config Initialized");
    }
    
    public List<String> getLogEvents() {
        return List.copyOf(logEvents);
    }
    
    public void setLogEvents() {
    	logEvents.clear();
    }
    
    private void addLog(String msg) {
        _LOGGER.info(msg);
        logEvents.add(msg);
        if (logEvents.size() > 25) logEvents.remove(0); // keep last 100 logs
    }
}