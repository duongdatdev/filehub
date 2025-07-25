package com.duongdat.filehub.config;

import com.duongdat.filehub.entity.Department;
import com.duongdat.filehub.entity.Project;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.repository.FileCategoryRepository;
import com.duongdat.filehub.repository.DepartmentRepository;
import com.duongdat.filehub.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileCategoryRepository fileCategoryRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer starting...");
        
        // Create demo departments if none exist
        if (departmentRepository.count() == 0) {
            System.out.println("Creating demo departments...");
            createDemoDepartments();
        }
        
        // Create demo projects if none exist
        if (projectRepository.count() == 0) {
            System.out.println("Creating demo projects...");
            createDemoProjects();
        }
        
        // Update users with department assignments if needed
        updateUserDepartments();
        
        System.out.println("DataInitializer completed");
        System.out.println("Available departments: " + departmentRepository.count());
        System.out.println("Available projects: " + projectRepository.count());
        System.out.println("Available users: " + userRepository.count());
        System.out.println("Available categories: " + fileCategoryRepository.count());
        
        // Log existing data for verification
        System.out.println("Departments:");
        departmentRepository.findAll().forEach(dept -> 
            System.out.println("  - " + dept.getName() + " (ID: " + dept.getId() + ", Manager: " + 
                (dept.getManagerId() != null ? dept.getManagerId() : "None") + ")"));
            
        System.out.println("Projects:");
        projectRepository.findAll().forEach(proj -> 
            System.out.println("  - " + proj.getName() + " (Department: " + proj.getDepartmentId() + 
                ", Status: " + proj.getStatus() + ")"));
    }
    
    private void createDemoDepartments() {
        Department engineering = new Department();
        engineering.setName("Engineering");
        engineering.setDescription("Software development and technical teams");
        engineering.setIsActive(true);
        engineering.setManagerId(1L);  // Assuming admin user has ID 1
        engineering = departmentRepository.save(engineering);
        
        Department frontend = new Department();
        frontend.setName("Frontend Team");
        frontend.setDescription("User interface and user experience development");
        frontend.setIsActive(true);
        frontend.setManagerId(2L);
        frontend.setParentId(engineering.getId());
        departmentRepository.save(frontend);
        
        Department backend = new Department();
        backend.setName("Backend Team");
        backend.setDescription("Server-side development and APIs");
        backend.setIsActive(true);
        backend.setManagerId(3L);
        backend.setParentId(engineering.getId());
        departmentRepository.save(backend);
        
        Department marketing = new Department();
        marketing.setName("Marketing");
        marketing.setDescription("Brand promotion and customer acquisition");
        marketing.setIsActive(true);
        marketing.setManagerId(4L);
        departmentRepository.save(marketing);
        
        Department hr = new Department();
        hr.setName("Human Resources");
        hr.setDescription("Employee management and recruitment");
        hr.setIsActive(true);
        hr.setManagerId(5L);
        departmentRepository.save(hr);
        
        Department finance = new Department();
        finance.setName("Finance");
        finance.setDescription("Financial planning and accounting");
        finance.setIsActive(true);
        finance.setManagerId(6L);
        departmentRepository.save(finance);
    }
    
    private void createDemoProjects() {
        Project fileManagement = new Project();
        fileManagement.setName("File Management System");
        fileManagement.setDescription("Development of the company file management platform");
        fileManagement.setDepartmentId(1L);  // Engineering
        fileManagement.setManagerId(1L);
        fileManagement.setStatus("IN_PROGRESS");
        fileManagement.setPriority("HIGH");
        fileManagement.setStartDate(LocalDate.of(2024, 1, 1));
        fileManagement.setEndDate(LocalDate.of(2024, 12, 31));
        fileManagement.setBudget(new BigDecimal("50000.00"));
        fileManagement.setIsActive(true);
        projectRepository.save(fileManagement);
        
        Project uiRedesign = new Project();
        uiRedesign.setName("UI/UX Redesign");
        uiRedesign.setDescription("Complete redesign of the user interface");
        uiRedesign.setDepartmentId(2L);  // Frontend Team
        uiRedesign.setManagerId(2L);
        uiRedesign.setStatus("IN_PROGRESS");
        uiRedesign.setPriority("MEDIUM");
        uiRedesign.setStartDate(LocalDate.of(2024, 2, 1));
        uiRedesign.setEndDate(LocalDate.of(2024, 8, 31));
        uiRedesign.setBudget(new BigDecimal("25000.00"));
        uiRedesign.setIsActive(true);
        projectRepository.save(uiRedesign);
        
        Project apiOptimization = new Project();
        apiOptimization.setName("API Optimization");
        apiOptimization.setDescription("Performance improvements for backend APIs");
        apiOptimization.setDepartmentId(3L);  // Backend Team
        apiOptimization.setManagerId(3L);
        apiOptimization.setStatus("PLANNING");
        apiOptimization.setPriority("HIGH");
        apiOptimization.setStartDate(LocalDate.of(2024, 3, 1));
        apiOptimization.setEndDate(LocalDate.of(2024, 9, 30));
        apiOptimization.setBudget(new BigDecimal("30000.00"));
        apiOptimization.setIsActive(true);
        projectRepository.save(apiOptimization);
        
        Project marketingCampaign = new Project();
        marketingCampaign.setName("Marketing Campaign");
        marketingCampaign.setDescription("Q2 product launch marketing campaign");
        marketingCampaign.setDepartmentId(4L);  // Marketing
        marketingCampaign.setManagerId(4L);
        marketingCampaign.setStatus("NOT_STARTED");
        marketingCampaign.setPriority("MEDIUM");
        marketingCampaign.setStartDate(LocalDate.of(2024, 4, 1));
        marketingCampaign.setEndDate(LocalDate.of(2024, 6, 30));
        marketingCampaign.setBudget(new BigDecimal("15000.00"));
        marketingCampaign.setIsActive(true);
        projectRepository.save(marketingCampaign);
        
        Project employeeTraining = new Project();
        employeeTraining.setName("Employee Training");
        employeeTraining.setDescription("Technical skills development program");
        employeeTraining.setDepartmentId(5L);  // HR
        employeeTraining.setManagerId(5L);
        employeeTraining.setStatus("IN_PROGRESS");
        employeeTraining.setPriority("LOW");
        employeeTraining.setStartDate(LocalDate.of(2024, 1, 15));
        employeeTraining.setEndDate(LocalDate.of(2024, 12, 15));
        employeeTraining.setBudget(new BigDecimal("20000.00"));
        employeeTraining.setIsActive(true);
        projectRepository.save(employeeTraining);
        
        Project budgetPlanning = new Project();
        budgetPlanning.setName("Budget Planning");
        budgetPlanning.setDescription("Annual budget planning and forecasting");
        budgetPlanning.setDepartmentId(6L);  // Finance
        budgetPlanning.setManagerId(6L);
        budgetPlanning.setStatus("COMPLETED");
        budgetPlanning.setPriority("HIGH");
        budgetPlanning.setStartDate(LocalDate.of(2023, 11, 1));
        budgetPlanning.setEndDate(LocalDate.of(2024, 1, 31));
        budgetPlanning.setBudget(new BigDecimal("10000.00"));
        budgetPlanning.setIsActive(true);
        projectRepository.save(budgetPlanning);
    }
    
    private void updateUserDepartments() {
        // Assign users to departments if they don't have department assignments
        userRepository.findAll().stream()
            .filter(user -> user.getDepartmentId() == null)
            .limit(12)  // Limit to first 12 users
            .forEach(user -> {
                // Distribute users across departments
                Long departmentId = ((user.getId() - 1) % 6) + 1;  // Distribute among 6 departments
                user.setDepartmentId(departmentId);
                userRepository.save(user);
            });
    }
}
