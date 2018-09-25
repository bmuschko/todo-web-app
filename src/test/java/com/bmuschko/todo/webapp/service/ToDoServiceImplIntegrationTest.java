package com.bmuschko.todo.webapp.service;

import com.bmuschko.todo.webapp.model.ToDoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ToDoServiceImplIntegrationTest {

    @Autowired
    private ToDoService toDoService;

    @Test
    public void canCreateNewItem() {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setName("Buy milk");
        toDoItem.setCompleted(false);
        toDoService.save(toDoItem);
    }
}
