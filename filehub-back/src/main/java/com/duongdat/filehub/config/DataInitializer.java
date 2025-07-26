package com.duongdat.filehub.config;

import com.duongdat.filehub.entity.*;
import com.duongdat.filehub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private FileTypeRepository fileTypeRepository;
    
    @Autowired
    private DepartmentCategoryRepository departmentCategoryRepository;
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer starting...");
        
        // Initialize file types first
        if (fileTypeRepository.count() == 0) {
            System.out.println("Creating default file types...");
            createDefaultFileTypes();
        }
        
        // Initialize system configurations
        if (systemConfigRepository.count() == 0) {
            System.out.println("Creating default system configurations...");
            createDefaultSystemConfigs();
        }
        
        // Create demo users if none exist or ensure admin exists
        if (userRepository.count() == 0) {
            System.out.println("Creating demo users...");
            createDemoUsers();
        } else {
            // Ensure admin user exists
            if (!userRepository.existsByUsername("admin")) {
                System.out.println("Creating admin user...");
                createAdminUser();
            }
        }
        
        // Create demo departments if none exist
        if (departmentRepository.count() == 0) {
            System.out.println("Creating demo departments...");
            createDemoDepartments();
        }
        
        // Create department categories
        if (departmentCategoryRepository.count() == 0) {
            System.out.println("Creating department categories...");
            createDepartmentCategories();
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
        
        // Log existing users
        System.out.println("Users:");
        userRepository.findAll().forEach(user -> 
            System.out.println("  - " + user.getUsername() + " (ID: " + user.getId() + 
                ", Role: " + user.getRole() + ", Email: " + user.getEmail() + ")"));
        
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
        engineering.setManagerId(1L);  // Admin user
        engineering = departmentRepository.save(engineering);
        
        Department frontend = new Department();
        frontend.setName("Frontend Team");
        frontend.setDescription("User interface and user experience development");
        frontend.setIsActive(true);
        frontend.setManagerId(2L);  // johndoe
        frontend.setParentId(engineering.getId());
        departmentRepository.save(frontend);
        
        Department backend = new Department();
        backend.setName("Backend Team");
        backend.setDescription("Server-side development and APIs");
        backend.setIsActive(true);
        backend.setManagerId(3L);  // janesmith
        backend.setParentId(engineering.getId());
        departmentRepository.save(backend);
        
        Department marketing = new Department();
        marketing.setName("Marketing");
        marketing.setDescription("Brand promotion and customer acquisition");
        marketing.setIsActive(true);
        marketing.setManagerId(4L);  // bobwilson
        departmentRepository.save(marketing);
        
        Department hr = new Department();
        hr.setName("Human Resources");
        hr.setDescription("Employee management and recruitment");
        hr.setIsActive(true);
        hr.setManagerId(5L);  // alicechen
        departmentRepository.save(hr);
        
        Department finance = new Department();
        finance.setName("Finance");
        finance.setDescription("Financial planning and accounting");
        finance.setIsActive(true);
        finance.setManagerId(6L);  // mikebrown
        departmentRepository.save(finance);
    }
    
    private void createDemoProjects() {
        Project fileManagement = new Project();
        fileManagement.setName("File Management System");
        fileManagement.setDescription("Development of the company file management platform");
        fileManagement.setDepartmentId(1L);  // Engineering
        fileManagement.setStatus("ACTIVE");
        Project savedFileManagement = projectRepository.save(fileManagement);
        System.out.println("Saved project: " + savedFileManagement.getName());
        
        Project uiRedesign = new Project();
        uiRedesign.setName("UI/UX Redesign");
        uiRedesign.setDescription("Complete redesign of the user interface");
        uiRedesign.setDepartmentId(2L);  // Frontend Team
        uiRedesign.setStatus("ACTIVE");
        projectRepository.save(uiRedesign);
        
        Project apiOptimization = new Project();
        apiOptimization.setName("API Optimization");
        apiOptimization.setDescription("Performance improvements for backend APIs");
        apiOptimization.setDepartmentId(3L);  // Backend Team
        apiOptimization.setStatus("ACTIVE");
        projectRepository.save(apiOptimization);
        
        Project marketingCampaign = new Project();
        marketingCampaign.setName("Marketing Campaign");
        marketingCampaign.setDescription("Q2 product launch marketing campaign");
        marketingCampaign.setDepartmentId(4L);  // Marketing
        marketingCampaign.setStatus("ACTIVE");
        projectRepository.save(marketingCampaign);
        
        Project employeeTraining = new Project();
        employeeTraining.setName("Employee Training");
        employeeTraining.setDescription("Technical skills development program");
        employeeTraining.setDepartmentId(5L);  // HR
        employeeTraining.setStatus("ACTIVE");
        projectRepository.save(employeeTraining);
        
        Project budgetPlanning = new Project();
        budgetPlanning.setName("Budget Planning");
        budgetPlanning.setDescription("Annual budget planning and forecasting");
        budgetPlanning.setDepartmentId(6L);  // Finance
        budgetPlanning.setStatus("COMPLETED");
        projectRepository.save(budgetPlanning);
    }
    
    private void createDefaultFileTypes() {
        // Create file types based on schema
        FileType document = new FileType();
        document.setName("DOCUMENT");
        document.setDescription("Documents (PDF, Word, Text)");
        document.setAllowedExtensions("[\"pdf\", \"doc\", \"docx\", \"txt\", \"rtf\"]");
        document.setColor("#2196F3");
        document.setIcon("document");
        document.setMaxSize(104857600L); // 100MB
        fileTypeRepository.save(document);
        
        FileType image = new FileType();
        image.setName("IMAGE");
        image.setDescription("Images and Graphics");
        image.setAllowedExtensions("[\"jpg\", \"jpeg\", \"png\", \"gif\", \"bmp\", \"svg\"]");
        image.setColor("#4CAF50");
        image.setIcon("image");
        image.setMaxSize(52428800L); // 50MB
        fileTypeRepository.save(image);
        
        FileType video = new FileType();
        video.setName("VIDEO");
        video.setDescription("Video and Multimedia");
        video.setAllowedExtensions("[\"mp4\", \"avi\", \"mov\", \"wmv\", \"flv\"]");
        video.setColor("#FF9800");
        video.setIcon("video");
        video.setMaxSize(524288000L); // 500MB
        fileTypeRepository.save(video);
        
        FileType slide = new FileType();
        slide.setName("SLIDE");
        slide.setDescription("Presentations");
        slide.setAllowedExtensions("[\"ppt\", \"pptx\", \"odp\"]");
        slide.setColor("#9C27B0");
        slide.setIcon("presentation");
        slide.setMaxSize(104857600L); // 100MB
        fileTypeRepository.save(slide);
        
        FileType sourceCode = new FileType();
        sourceCode.setName("SOURCE_CODE");
        sourceCode.setDescription("Source Code and Archives");
        sourceCode.setAllowedExtensions("[\"zip\", \"rar\", \"7z\", \"tar\", \"gz\", \"java\", \"js\", \"css\", \"html\", \"py\", \"cpp\"]");
        sourceCode.setColor("#FF5722");
        sourceCode.setIcon("code");
        sourceCode.setMaxSize(209715200L); // 200MB
        fileTypeRepository.save(sourceCode);
        
        FileType other = new FileType();
        other.setName("OTHER");
        other.setDescription("Other File Types");
        other.setAllowedExtensions("[\"csv\", \"xml\", \"json\", \"log\"]");
        other.setColor("#9E9E9E");
        other.setIcon("file");
        other.setMaxSize(52428800L); // 50MB
        fileTypeRepository.save(other);
    }
    
    private void createDefaultSystemConfigs() {
        SystemConfig maxFileSize = new SystemConfig();
        maxFileSize.setConfigKey("file.max-size");
        maxFileSize.setConfigValue("104857600");
        maxFileSize.setDataType("NUMBER");
        maxFileSize.setDescription("Maximum file size (100MB)");
        maxFileSize.setIsPublic(true);
        systemConfigRepository.save(maxFileSize);
        
        SystemConfig allowedTypes = new SystemConfig();
        allowedTypes.setConfigKey("file.allowed-types");
        allowedTypes.setConfigValue("[\"pdf\",\"doc\",\"docx\",\"txt\",\"jpg\",\"jpeg\",\"png\",\"gif\",\"mp4\",\"avi\",\"mp3\",\"wav\",\"zip\",\"rar\",\"json\",\"xml\",\"csv\",\"ppt\",\"pptx\"]");
        allowedTypes.setDataType("JSON");
        allowedTypes.setDescription("Allowed file types for upload");
        allowedTypes.setIsPublic(true);
        systemConfigRepository.save(allowedTypes);
        
        SystemConfig storagePath = new SystemConfig();
        storagePath.setConfigKey("storage.path");
        storagePath.setConfigValue("/app/uploads");
        storagePath.setDataType("STRING");
        storagePath.setDescription("File storage path");
        storagePath.setIsPublic(false);
        systemConfigRepository.save(storagePath);
        
        SystemConfig appName = new SystemConfig();
        appName.setConfigKey("app.name");
        appName.setConfigValue("File Management System");
        appName.setDataType("STRING");
        appName.setDescription("Application name");
        appName.setIsPublic(true);
        systemConfigRepository.save(appName);
        
        SystemConfig appVersion = new SystemConfig();
        appVersion.setConfigKey("app.version");
        appVersion.setConfigValue("1.0.0");
        appVersion.setDataType("STRING");
        appVersion.setDescription("Application version");
        appVersion.setIsPublic(true);
        systemConfigRepository.save(appVersion);
    }
    
    private void createDepartmentCategories() {
        // Get all departments
        var departments = departmentRepository.findAll();
        
        for (Department dept : departments) {
            if ("IT Department".equals(dept.getName()) || "Engineering".equals(dept.getName())) {
                createITCategories(dept.getId());
            } else if ("Marketing Department".equals(dept.getName()) || "Marketing".equals(dept.getName())) {
                createMarketingCategories(dept.getId());
            } else if ("HR Department".equals(dept.getName()) || "Human Resources".equals(dept.getName())) {
                createHRCategories(dept.getId());
            } else if ("Finance Department".equals(dept.getName()) || "Finance".equals(dept.getName())) {
                createFinanceCategories(dept.getId());
            } else if ("Operations Department".equals(dept.getName()) || "Operations".equals(dept.getName())) {
                createOperationsCategories(dept.getId());
            }
        }
    }
    
    private void createITCategories(Long departmentId) {
        DepartmentCategory techDocs = new DepartmentCategory();
        techDocs.setName("Technical Documents");
        techDocs.setDescription("Technical specifications and documentation");
        techDocs.setDepartmentId(departmentId);
        techDocs.setColor("#5D4037");
        techDocs.setIcon("tech-doc");
        techDocs.setDisplayOrder(1);
        departmentCategoryRepository.save(techDocs);
        
        DepartmentCategory meetings = new DepartmentCategory();
        meetings.setName("Meeting Minutes");
        meetings.setDescription("Meeting notes and minutes");
        meetings.setDepartmentId(departmentId);
        meetings.setColor("#7B1FA2");
        meetings.setIcon("meeting");
        meetings.setDisplayOrder(2);
        departmentCategoryRepository.save(meetings);
        
        DepartmentCategory maintenance = new DepartmentCategory();
        maintenance.setName("Maintenance Requests");
        maintenance.setDescription("System maintenance and support requests");
        maintenance.setDepartmentId(departmentId);
        maintenance.setColor("#D32F2F");
        maintenance.setIcon("maintenance");
        maintenance.setDisplayOrder(3);
        departmentCategoryRepository.save(maintenance);
    }
    
    private void createMarketingCategories(Long departmentId) {
        DepartmentCategory campaigns = new DepartmentCategory();
        campaigns.setName("Campaigns");
        campaigns.setDescription("Marketing campaigns and materials");
        campaigns.setDepartmentId(departmentId);
        campaigns.setColor("#E91E63");
        campaigns.setIcon("campaign");
        campaigns.setDisplayOrder(1);
        departmentCategoryRepository.save(campaigns);
        
        DepartmentCategory research = new DepartmentCategory();
        research.setName("Research");
        research.setDescription("Market research and analysis");
        research.setDepartmentId(departmentId);
        research.setColor("#00796B");
        research.setIcon("research");
        research.setDisplayOrder(2);
        departmentCategoryRepository.save(research);
        
        DepartmentCategory brandAssets = new DepartmentCategory();
        brandAssets.setName("Brand Assets");
        brandAssets.setDescription("Brand guidelines and assets");
        brandAssets.setDepartmentId(departmentId);
        brandAssets.setColor("#FF5722");
        brandAssets.setIcon("brand");
        brandAssets.setDisplayOrder(3);
        departmentCategoryRepository.save(brandAssets);
    }
    
    private void createHRCategories(Long departmentId) {
        DepartmentCategory contracts = new DepartmentCategory();
        contracts.setName("Contracts");
        contracts.setDescription("Employment contracts and agreements");
        contracts.setDepartmentId(departmentId);
        contracts.setColor("#1976D2");
        contracts.setIcon("contract");
        contracts.setDisplayOrder(1);
        departmentCategoryRepository.save(contracts);
        
        DepartmentCategory policies = new DepartmentCategory();
        policies.setName("Policies");
        policies.setDescription("Company policies and procedures");
        policies.setDepartmentId(departmentId);
        policies.setColor("#388E3C");
        policies.setIcon("policy");
        policies.setDisplayOrder(2);
        departmentCategoryRepository.save(policies);
        
        DepartmentCategory recruitment = new DepartmentCategory();
        recruitment.setName("Recruitment");
        recruitment.setDescription("Recruitment documents and resumes");
        recruitment.setDepartmentId(departmentId);
        recruitment.setColor("#F57C00");
        recruitment.setIcon("recruitment");
        recruitment.setDisplayOrder(3);
        departmentCategoryRepository.save(recruitment);
    }
    
    private void createFinanceCategories(Long departmentId) {
        DepartmentCategory budgets = new DepartmentCategory();
        budgets.setName("Budgets");
        budgets.setDescription("Budget documents and financial plans");
        budgets.setDepartmentId(departmentId);
        budgets.setColor("#2E7D32");
        budgets.setIcon("budget");
        budgets.setDisplayOrder(1);
        departmentCategoryRepository.save(budgets);
        
        DepartmentCategory reports = new DepartmentCategory();
        reports.setName("Reports");
        reports.setDescription("Financial reports and statements");
        reports.setDepartmentId(departmentId);
        reports.setColor("#1565C0");
        reports.setIcon("report");
        reports.setDisplayOrder(2);
        departmentCategoryRepository.save(reports);
        
        DepartmentCategory invoices = new DepartmentCategory();
        invoices.setName("Invoices");
        invoices.setDescription("Invoices and billing documents");
        invoices.setDepartmentId(departmentId);
        invoices.setColor("#E65100");
        invoices.setIcon("invoice");
        invoices.setDisplayOrder(3);
        departmentCategoryRepository.save(invoices);
    }
    
    private void createOperationsCategories(Long departmentId) {
        DepartmentCategory procedures = new DepartmentCategory();
        procedures.setName("Procedures");
        procedures.setDescription("Operational procedures and workflows");
        procedures.setDepartmentId(departmentId);
        procedures.setColor("#6A1B9A");
        procedures.setIcon("procedure");
        procedures.setDisplayOrder(1);
        departmentCategoryRepository.save(procedures);
        
        DepartmentCategory qualityControl = new DepartmentCategory();
        qualityControl.setName("Quality Control");
        qualityControl.setDescription("Quality assurance documents");
        qualityControl.setDepartmentId(departmentId);
        qualityControl.setColor("#00838F");
        qualityControl.setIcon("quality");
        qualityControl.setDisplayOrder(2);
        departmentCategoryRepository.save(qualityControl);
        
        DepartmentCategory training = new DepartmentCategory();
        training.setName("Training");
        training.setDescription("Training materials and guides");
        training.setDepartmentId(departmentId);
        training.setColor("#558B2F");
        training.setIcon("training");
        training.setDisplayOrder(3);
        departmentCategoryRepository.save(training);
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
    
    private void createDemoUsers() {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@filehub.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("System Administrator");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);
        
        // Create test users
        User user1 = new User();
        user1.setUsername("johndoe");
        user1.setEmail("john.doe@filehub.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setFullName("John Doe");
        user1.setRole(Role.USER);
        user1.setActive(true);
        userRepository.save(user1);
        
        User user2 = new User();
        user2.setUsername("janesmith");
        user2.setEmail("jane.smith@filehub.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setFullName("Jane Smith");
        user2.setRole(Role.USER);
        user2.setActive(true);
        userRepository.save(user2);
        
        User user3 = new User();
        user3.setUsername("bobwilson");
        user3.setEmail("bob.wilson@filehub.com");
        user3.setPassword(passwordEncoder.encode("password123"));
        user3.setFullName("Bob Wilson");
        user3.setRole(Role.USER);
        user3.setActive(true);
        userRepository.save(user3);
        
        User user4 = new User();
        user4.setUsername("alicechen");
        user4.setEmail("alice.chen@filehub.com");
        user4.setPassword(passwordEncoder.encode("password123"));
        user4.setFullName("Alice Chen");
        user4.setRole(Role.USER);
        user4.setActive(true);
        userRepository.save(user4);
        
        User user5 = new User();
        user5.setUsername("mikebrown");
        user5.setEmail("mike.brown@filehub.com");
        user5.setPassword(passwordEncoder.encode("password123"));
        user5.setFullName("Mike Brown");
        user5.setRole(Role.USER);
        user5.setActive(true);
        userRepository.save(user5);
        
        System.out.println("Created demo users: admin, johndoe, janesmith, bobwilson, alicechen, mikebrown");
    }
    
    private void createAdminUser() {
        // Create admin user only
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@filehub.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("System Administrator");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);
        
        System.out.println("Created admin user with username: admin, password: admin123");
    }
}
