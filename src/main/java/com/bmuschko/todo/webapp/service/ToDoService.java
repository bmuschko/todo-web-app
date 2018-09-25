package com.bmuschko.todo.webapp.service;

import com.bmuschko.todo.webapp.model.ToDoItem;

import java.util.List;

public interface ToDoService {

    List<ToDoItem> findAll();
    ToDoItem findOne(Long id);
    void save(ToDoItem toDoItem);
    void delete(ToDoItem toDoItem);
}
