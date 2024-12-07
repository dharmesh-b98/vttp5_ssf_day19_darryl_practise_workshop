package sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.controller;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.model.Todo;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.service.TodoService;

@Controller
@RequestMapping("/todo")
public class TodoController {
    
    @Autowired
    TodoService todoService;

    @GetMapping("/todoList")
    public String getTodoList(@RequestParam(name="sorted", defaultValue = "false") Boolean sorted, HttpSession session, Model model) throws ParseException{
        List<String> sessionAttributeList = Collections.list(session.getAttributeNames());
        
        if (!(sessionAttributeList.contains("username") && sessionAttributeList.contains("age"))){
            return "nologin";
        }
        
        
        Map<String,Todo> todoMap = todoService.getTodoMap();
        Set<Entry<String,Todo>> todoEntrySet = todoMap.entrySet();

        List<Entry<String,Todo>> todoEntryList = todoEntrySet.stream().collect(Collectors.toList());
        
        if(sorted){
            List<Entry<String,Todo>> todoEntryListSorted = todoEntrySet.stream().sorted(Comparator.comparing(a->a.getValue().getStatus())).collect(Collectors.toList());
            model.addAttribute("todoEntryList", todoEntryListSorted);
        }
        else{
            model.addAttribute("todoEntryList", todoEntryList);
        }

        return "todolist";
    }

    
    @GetMapping("/addTodo")
    public String addTodo(Model model){
        Todo todo = new Todo();
        todo.setId(UUID.randomUUID().toString());

        Date currentDate = Calendar.getInstance().getTime();
        todo.setCreatedAt(currentDate);
        todo.setUpdatedAt(currentDate);

        model.addAttribute("todo", todo);
        
        return "addtodo";
    }

    @PostMapping("/addTodo")
    public String addTodoPost(@Valid @ModelAttribute("todo") Todo todo, BindingResult binding){
        if (binding.hasErrors())
            return "addtodo";

        String Id = todoService.addTodo(todo);
        System.out.println(Id);

        return "redirect:/todo/todoList";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestBody MultiValueMap<String,String> loginMap, HttpSession session, Model model){
        String username = loginMap.getFirst("username");
        Integer age = Integer.parseInt(loginMap.getFirst("age"));

        if (age < 10){
            return "underage";
        }

        session.setAttribute("username", username);
        session.setAttribute("age", age);

        return "redirect:/todo/todoList";
    }

    @GetMapping("/todoList/delete/{id}")
    public String deleteTodo(@PathVariable("id") String id){
        todoService.deleteTodo(id);
        return "redirect:/todo/todoList";
    }

    @GetMapping("/todoList/update/{id}")
    public String updateTodo(@PathVariable("id") String id, Model model) throws ParseException{
        Todo todo = todoService.getTodo(id);

        Date currentDate = Calendar.getInstance().getTime();
        todo.setUpdatedAt(currentDate);
        model.addAttribute("todo", todo);

        return "addtodo";
    }

    @PostMapping("/todoList/update/{id}")
    public String updateTodoPost(@PathVariable("id") String id, @Valid @ModelAttribute("todo") Todo todo, BindingResult binding ){
        if (binding.hasErrors())
        return "addtodo";

        String Id = todoService.addTodo(todo);
        System.out.println(Id); 
        
        todoService.addTodo(todo);
        return "redirect:/todo/todoList";
    }

    

}
