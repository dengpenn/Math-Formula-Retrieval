package com.search.engine.mathsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MathSearchApplication {

	public static void main(String[] args) {
		if(args.length>0) {
			if (args[0].contains("init")){
				try {
					dataInitializer.dataInitializer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		SpringApplication.run(MathSearchApplication.class,"");
	}



}
