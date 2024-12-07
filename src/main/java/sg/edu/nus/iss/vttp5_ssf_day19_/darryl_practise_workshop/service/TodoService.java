package sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.service;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.*;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.model.Todo;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.repo.HashRepo;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.util.Constant;

@Service
public class TodoService {
    
    @Autowired
    HashRepo todoRepo;

    public Map<String,Todo> getTodoMap() throws ParseException{
        Map<String,String> todoStringMap = todoRepo.entries(Constant.redisHashName);
        Map<String,Todo> todoMap = new HashMap<>();
        for (Entry<String, String> todoStringEntry : todoStringMap.entrySet()){
            String jsonString = todoStringEntry.getValue();
            JsonReader reader = Json.createReader(new StringReader(jsonString));
            JsonObject jsonObject = reader.readObject();

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy");

            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");            
            Date dueDate = sdf.parse(jsonObject.getString("due_date"));
            String priority = jsonObject.getString("priority_level");
            String status = jsonObject.getString("status");
            Date createdDate = sdf.parse(jsonObject.getString("created_at"));
            Date updatedDate = sdf.parse(jsonObject.getString("updated_at"));

            Todo todo = new Todo(id,name,description,dueDate,priority,status,createdDate,updatedDate);
            todoMap.put(id, todo);
        }
        return todoMap;
    }

    public String addTodo(Todo todo){

        //convert Todo to Json String
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy");

        JsonObjectBuilder job = Json.createObjectBuilder();
        JsonObject jsonObject = job.add("id", todo.getId())
                                .add("name", todo.getName())
                                .add("description", todo.getDescription())
                                .add("due_date", sdf.format(todo.getDueDate()))
                                .add("priority_level", todo.getPriority())
                                .add("status", todo.getStatus())
                                .add("created_at", sdf.format(todo.getCreatedAt()))
                                .add("updated_at", sdf.format(todo.getUpdatedAt()))
                                .build();
        
        todoRepo.put(Constant.redisHashName, todo.getId(), jsonObject.toString());

        return todo.getId();
    }

    public void deleteTodo(String id){
        todoRepo.delete(Constant.redisHashName, id);
    }

    public Todo getTodo(String idRequested) throws ParseException{

        String jsonString = todoRepo.get(Constant.redisHashName, idRequested).toString();
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = reader.readObject();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy");

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String description = jsonObject.getString("description");            
        Date dueDate = sdf.parse(jsonObject.getString("due_date"));
        String priority = jsonObject.getString("priority_level");
        String status = jsonObject.getString("status");
        Date createdDate = sdf.parse(jsonObject.getString("created_at"));
        Date updatedDate = sdf.parse(jsonObject.getString("updated_at"));

        Todo todo = new Todo(id,name,description,dueDate,priority,status,createdDate,updatedDate);
        return todo;
    }
    
}
