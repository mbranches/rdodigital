package com.branches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MeudiariodeobrasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeudiariodeobrasApplication.class, args);
	}

}
