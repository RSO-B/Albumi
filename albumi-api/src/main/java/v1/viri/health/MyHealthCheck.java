package v1.viri.health;

import configuration.AppProperties;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class MyHealthCheck implements HealthCheck {
    @Inject
    private AppProperties appProperties;

    public HealthCheckResponse call() {

        HealthCheckResponseBuilder healthCheckResponseBuilder =
                HealthCheckResponse.named(MyHealthCheck.class.getSimpleName());

        if (appProperties.isHealthy()) {
            return healthCheckResponseBuilder.up().build();
        } else {
            return healthCheckResponseBuilder.down().build();
        }
    }
}
