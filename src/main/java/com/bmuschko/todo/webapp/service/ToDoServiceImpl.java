package com.bmuschko.todo.webapp.service;

import com.bmuschko.todo.webapp.model.ToDoItem;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToDoServiceImpl implements ToDoService {

    public final static String BASE_URL = "http://localhost:8080";
    private final static MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<ToDoItem> findAll() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/todos")
                .build();
        Response response = callEndpoint(request);
        JSONParser parser = new JSONParser();

        try {
            List<ToDoItem> toDoItems = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) parser.parse(response.body().string());
            jsonArray.forEach(itemJson -> {
                ToDoItem toDoItem = parseToDoItem((JSONObject) itemJson);
                toDoItems.add(toDoItem);
            });
            return toDoItems;
        } catch(IOException | ParseException e) {
            throw new ToDoServiceException(e);
        }
    }

    @Override
    public ToDoItem findOne(Long id) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/todos/" + id)
                .build();

        Response response = callEndpoint(request);
        JSONParser parser = new JSONParser();

        try {
            JSONObject itemJson = (JSONObject) parser.parse(response.body().string());
            return parseToDoItem(itemJson);
        } catch(IOException | ParseException e) {
            throw new ToDoServiceException(e);
        }
    }

    @Override
    public void save(ToDoItem toDoItem) {
        if (toDoItem.getId() == null) {
            RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, buildToDoItemJson(toDoItem));
            Request request = new Request.Builder()
                    .url(BASE_URL + "/todos")
                    .post(requestBody)
                    .build();
            callEndpoint(request);
        } else {
            RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, buildToDoItemJson(toDoItem));
            Request request = new Request.Builder()
                    .url(BASE_URL + "/todos/" + toDoItem.getId())
                    .put(requestBody)
                    .build();
            callEndpoint(request);
        }
    }

    @Override
    public void delete(ToDoItem toDoItem) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/todos/" + toDoItem.getId())
                .delete()
                .build();
        callEndpoint(request);
    }

    private Response callEndpoint(Request request) {
        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new ToDoServiceException("Failed to call endpoint '" + request.url().url().toString() + "' [HTTP " + response.code() + "]");
            }

            return response;
        } catch (IOException e) {
            throw new ToDoServiceException(e);
        }
    }

    private ToDoItem parseToDoItem(JSONObject itemJson) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setId((Long) itemJson.get("id"));
        toDoItem.setName((String) itemJson.get("name"));
        toDoItem.setCompleted((Boolean) itemJson.get("completed"));
        return toDoItem;
    }

    private String buildToDoItemJson(ToDoItem toDoItem) {
        return "{ \"name\": \"" + toDoItem.getName() + "\", \"completed\": " + toDoItem.isCompleted() + " }";
    }
}
