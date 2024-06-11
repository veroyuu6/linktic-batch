package com.linktic.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.linktic.batch.model.Person;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la lógica de procesamiento de personas provenientes de un archivo CSV.
 */
@Slf4j
public class PersonCsvProcessor implements ItemProcessor<Person, Person> {

   // Patrón para validar el formato del correo electrónico
   private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

   /**
    * Método que procesa una persona proveniente de un archivo CSV.
    * @param item Persona a procesar.
    * @return La persona procesada si el correo electrónico tiene un formato válido y el nombre se convierte a mayúsculas, o null si el correo electrónico no es válido.
    */
   @Override
   public Person process(Person item) {
      // Verificar si el correo electrónico cumple con el patrón
      if (item.getEmail().matches(EMAIL_PATTERN)) {
         // Convertir el nombre a mayúsculas
         item.setNombre(item.getNombre().toUpperCase());
         return item; // Retornar la persona procesada
      }
      // Si el correo electrónico no es válido, retornar null
      return null;
   }

}
