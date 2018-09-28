package com.bmuschko.todo.webapp.service;

import com.bmuschko.todo.webapp.model.ToDoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ToDoServiceImplIntegrationTest {

    @Autowired
    private ToDoService toDoService;

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
