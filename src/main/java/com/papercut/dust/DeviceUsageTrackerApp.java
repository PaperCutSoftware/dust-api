/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.papercut.dust.claim.DeviceClaimResource;
import com.papercut.dust.slack.DeviceSlackResource;
import com.papercut.dust.user.ProfileResource;
import com.papercut.dust.auth.AuthenticatorFeature;
import com.papercut.dust.claim.ClaimResource;
import com.papercut.dust.device.DeviceResource;
import com.papercut.dust.user.UserResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

@ApplicationScoped
public class DeviceUsageTrackerApp extends Application<DeviceUsageTrackerConfig> {

    public static void main(final String[] args) throws Exception {
        new DeviceUsageTrackerApp().run(args);
    }

    @Override
    public String getName() {
        return "device-usage-tracker";
    }

    @Override
    public void initialize(final Bootstrap<DeviceUsageTrackerConfig> bootstrap) {
        // kick off CDI
        bootstrap.addBundle(new WeldBundle());
    }

    @Override
    public void run(final DeviceUsageTrackerConfig configuration, final Environment environment) {
        ConfigurationHolder.set(configuration);

        environment.jersey().register(DeviceResource.class);
        environment.jersey().register(UserResource.class);
        environment.jersey().register(ProfileResource.class);
        environment.jersey().register(ClaimResource.class);
        environment.jersey().register(DeviceSlackResource.class);
        environment.jersey().register(DeviceClaimResource.class);

        // JSON property naming
        environment.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        environment.jersey().register(new CustomExceptionMapper());

        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Auth
        environment.jersey().register(
                new AuthenticatorFeature(environment.metrics(), configuration.getAuthConfig()));
    }

}
