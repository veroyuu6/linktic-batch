package com.linktic.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.linktic.batch.model.Person;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la lógica de procesamiento de personas provenientes de la base de datos.
 */
@Slf4j
public class PersonDbProcessor implements ItemProcessor<Person, Person> {

   /**
    * Método que procesa una persona proveniente de la base de datos.
    * @param item Persona a procesar.
    * @return La persona procesada con el nombre convertido a minúsculas.
    */
   @Override
   public Person process(Person item) {
      // Convertir el nombre a minúsculas
      item.setNombre(item.getNombre().toLowerCase());
      return item; // Retornar la persona procesada
   }

}
