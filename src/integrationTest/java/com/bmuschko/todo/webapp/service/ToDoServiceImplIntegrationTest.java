package com.bmuschko.todo.webapp.service;

import com.bmuschko.todo.webapp.model.ToDoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = { ToDoServiceImplIntegrationTest.Initializer.class })
public class ToDoServiceImplIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ToDoServiceImplIntegrationTest.class);
    private final static String WEB_SERVICE_NAME = "webservice_1";
    private final static int WEB_SERVICE_PORT = 8080;
    private final static String DATABASE_NAME = "database_1";
    private final static int DATABASE_PORT = 5432;

    @Autowired
    private ToDoService toDoService;

    @Container
    public static DockerComposeContainer environment = createComposeContainer();

    private static DockerComposeContainer createComposeContainer() {
        return new DockerComposeContainer(new File("src/integrationTest/resources/compose-test.yml"))
                .withLogConsumer(WEB_SERVICE_NAME, new Slf4jLogConsumer(logger))
                .withExposedService(WEB_SERVICE_NAME, WEB_SERVICE_PORT,
                    Wait.forHttp("/actuator/health")
                        .forStatusCode(200))
                .withExposedService(DATABASE_NAME, DATABASE_PORT,
                        Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2));
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "todo.web.service.url=" + getWebServiceUrl()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    private static String getWebServiceUrl() {
        String host = environment.getServiceHost(WEB_SERVICE_NAME, WEB_SERVICE_PORT);
        Integer port = environment.getServicePort(WEB_SERVICE_NAME, WEB_SERVICE_PORT);
        StringBuilder webServiceUrl = new StringBuilder();
        webServiceUrl.append("http://");
        webServiceUrl.append(host);
        webServiceUrl.append(":");
        webServiceUrl.append(port);
        return webServiceUrl.toString();
    }

    @Test
    public void canCreateNewItemAndRetrieveIt() {
        ToDoItem newItem = newItem("Buy milk");
        assertNull(newItem.getId());
        toDoService.save(newItem);
        assertNotNull(newItem.getId());
        ToDoItem retrievedItem = toDoService.findOne(newItem.getId());
        assertEquals(newItem, retrievedItem);
    }

    @Test
    public void canCreateNewItemAndDeleteIt() {
        ToDoItem newItem = newItem("Pay bills");
        assertNull(newItem.getId());
        toDoService.save(newItem);
        assertNotNull(newItem.getId());
        toDoService.delete(newItem);
    }

    @Test
    public void canCreateNewItemAndUpdateIt() {
        ToDoItem newItem = newItem("Run errands");
        assertNull(newItem.getId());
        toDoService.save(newItem);
        assertNotNull(newItem.getId());
        newItem.setName("Work out");
        toDoService.save(newItem);
        ToDoItem retrievedItem = toDoService.findOne(newItem.getId());
        assertEquals("Work out", retrievedItem.getName());
    }

    @Test
    public void canRetrieveAllItems() {
        ToDoItem newItem1 = newItem("Run errands");
        toDoService.save(newItem1);
        ToDoItem newItem2 = newItem("Make appointment");
        toDoService.save(newItem2);
        List<ToDoItem> allItems = toDoService.findAll();
        assertFalse(allItems.isEmpty());

        allItems.forEach(item -> {
            assertNotNull(item.getId());
            assertNotNull(item.getName());
        });
    }

    private static ToDoItem newItem(String name) {
        return newItem(name, false);
    }

    private static ToDoItem newItem(String name, boolean completed) {
        ToDoItem newItem = new ToDoItem();
        newItem.setName(name);
        newItem.setCompleted(completed);
        return newItem;
    }
}
