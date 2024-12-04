package com.dubrovsky.task.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TaskRestfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskRestfulApplication.class, args);
	}

}
