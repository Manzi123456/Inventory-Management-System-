package com.airtel.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// @ComponentScan explicitly tells Spring exactly where to look
// for Controllers, Services, and Repositories
// This overrides the default scan and makes it explicit
@SpringBootApplication
@ComponentScan(basePackages = "com.airtel.inventory")
public class InventoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
}