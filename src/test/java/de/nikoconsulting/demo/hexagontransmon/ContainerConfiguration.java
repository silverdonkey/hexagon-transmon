package de.nikoconsulting.demo.hexagontransmon;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfiguration {

    /**
     *
     * Utilize the @ServiceConnection annotation to eliminate
     * the boilerplate code of defining the dynamic properties.
     *
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        int containerPort = 5432 ;
        int localPort = 5432 ;
        PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:15.2-alpine")
                .withDatabaseName("testcontainer_db")
                .withUsername("postgres")
                .withPassword("postgres")
                // by enabling reuse, we can reload or even completely restart the application,
                // while ensuring the containers remain actively preserved.
                //
                // To enable reuse of containers, you must set 'testcontainers.reuse.enable=true'
                // in a file located at {USER_HOME}/.testcontainers.properties
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))))
                .withCopyFileToContainer(
                     MountableFile.forClasspathResource("init-db.sql"),
                        "/docker-entrypoint-initdb.d/init-db.sql");

                return postgres;
    }

}