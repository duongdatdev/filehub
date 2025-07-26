package com.duongdat.filehub.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component  // Temporarily disabled for testing new schema
public class DataInitializerBackup implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer temporarily disabled for testing new authentication schema");
    }
}
