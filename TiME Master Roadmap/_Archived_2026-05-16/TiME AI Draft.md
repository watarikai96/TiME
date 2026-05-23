# TiME AI Draft

> **Fully functional full-stack Java + Spring Boot + Angular + MySQL app**

> ## Concept: What Are We Building?

You’re building **TiME AI**, a powerful time-management system.
Let’s say you're starting with a basic feature: **Tasks**.

A Task might have:

- a name ("Study Java")
- a deadline ("2025-05-01")
- a status (e.g., completed or not)

You want users to:

- Create tasks
- View tasks
- Update tasks
- Delete tasks

To do this, we need both a **backend** and a **frontend**.

> ## Your Full-Stack Architecture (One Entity Example: `Task`)

### 1. **Entity (JPA)**

This is the class that directly maps to a **MySQL table**.

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String deadline;

    private boolean completed;
    
    // Getters and setters
}
```

### 2. **DTO (Data Transfer Object)**

This is a "safe version" of your data you send to/from the frontend.

```java
public class TaskDto {
    private String title;
    private String deadline;
    private boolean completed;

    // Getters and setters
}
```

- You might **hide sensitive fields**, or **combine multiple objects** later.

### 3. **Repository**

This talks directly to MySQL.

```java
public interface TaskRepository extends JpaRepository<Task, Long> {}
```

- You **don’t write SQL** — Spring JPA handles it.

### 4. **Service Layer**

#### Interface

Declares what services are available.

```java
public interface TaskService {
    TaskDto createTask(TaskDto dto);
    List<TaskDto> getAllTasks();
    TaskDto updateTask(Long id, TaskDto dto);
    void deleteTask(Long id);
}
```

#### Implementation

Contains the actual logic.

```java
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskDto createTask(TaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDeadline(dto.getDeadline());
        task.setCompleted(dto.isCompleted());
        task = taskRepository.save(task);

        TaskDto response = new TaskDto();
        response.setTitle(task.getTitle());
        response.setDeadline(task.getDeadline());
        response.setCompleted(task.isCompleted());
        return response;
    }

    // Implement other methods similarly
}
```

### 5. **Controller (REST API)**

This listens to requests from the frontend and connects them to the service layer.

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto dto) {
        return ResponseEntity.ok(taskService.createTask(dto));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskDto dto) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Frontend: Angular

Use Angular to:

- Show tasks in a list
- Send HTTP requests to the Spring API
- Handle user input (like creating/editing tasks)

You’ll need:

1. **Angular Service** to call Spring Boot API
2. **Component(s)** to display and interact with data

## CHECKLIST (For Every Entity You Create in TiME AI)

**Database**

- [ ] Create MySQL table or let Spring JPA handle it via `@Entity`.

**Backend (Spring Boot)**

- [ ] Create `Entity` class (maps to DB)
- [ ] Create `Dto` class (for data transfer)
- [ ] Create `Repository` interface
- [ ] Create `Service` interface + implementation
- [ ] Create `Controller` with REST endpoints

**Frontend (Angular)**

- [ ] Create Angular `Service` (use `HttpClient`)
- [ ] Create Angular `Component` (form for input, table/list for output)
- [ ] Create models (TypeScript interfaces)
- [ ] Setup routing and views

**Connection**

- [ ] Backend `application.properties` with MySQL DB config
- [ ] Enable CORS so Angular can talk to Spring
- [ ] Start both servers (Angular `ng serve`, Spring Boot `run`)

**Testing**

- [ ] Test each REST API endpoint with Postman or curl
- [ ] Test Angular forms and API integration

**Repeat for each feature/entity** like:

- [ ] Projects
- [ ] Timers
- [ ] Goals
- [ ] Tags
- [ ] Users
- [ ] Notes
- [ ] etc.

> Would you like me to:

- > Set up a starter project template you can copy for all entities?
- > Show you how to wire up Angular part (task list + create form)?
- > Give you a full working CRUD project GitHub template to begin with?