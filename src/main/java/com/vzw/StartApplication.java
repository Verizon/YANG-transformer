/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartApplication implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(StartApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Override
    public void run(String... args) {

        logger.info("StartApplication...");

        //repository.save(new Book("Java"));
        //repository.save(new Book("Node"));
        //repository.save(new Book("Python"));

        //System.out.println("\nfindAll()");
        //repository.findAll().forEach(x -> System.out.println(x));

        //System.out.println("\nfindById(300419481230L)");
        //repository.findById(300419481230L).ifPresent(x -> logger.info(x));

        //System.out.println("\nfindByName('Node')");
        //repository.findByName("Node").forEach(x -> System.out.println(x));

    }

}
