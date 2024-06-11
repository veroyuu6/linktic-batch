package com.linktic.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 */
@SpringBootApplication
public class LinkticBatchApplication {

   /**
    * Método principal que inicia la aplicación Spring Boot.
    *
    * @param args Argumentos de línea de comandos (no se utilizan en esta aplicación).
    */
   public static void main(String[] args) {
      SpringApplication.run(LinkticBatchApplication.class, args);
   }

}
