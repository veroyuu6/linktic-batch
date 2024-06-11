package com.linktic.batch.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Clase que representa a una persona migrada en el sistema.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class PersonMigrate {

   /**
    * Identificador único de la persona migrada.
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Nombre de la persona migrada.
    */
   private String nombre;

   /**
    * Correo electrónico de la persona migrada.
    */
   private String email;

}
