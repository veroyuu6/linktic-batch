package com.linktic.batch.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.linktic.batch.model.Person;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener para el job que procesa datos desde un archivo CSV.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobCsvListener extends JobExecutionListenerSupport {

   private final JdbcTemplate jdbcTemplate;

   /**
    * Método que se ejecuta después de que el job ha finalizado.
    * Imprime los resultados de la operación si el job se ha completado exitosamente.
    *
    * @param jobExecution objeto que representa la ejecución del job
    */
   @Override
   public void afterJob(JobExecution jobExecution) {
      if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
         log.info("FINALIZÓ EL JOB!! Verifica los resultados:");
         jdbcTemplate
               .query("SELECT id, nombre, email FROM Person",
                     (rs, row) -> new Person(rs.getLong(1), rs.getString(2), rs.getString(3)))
               .forEach(persona -> log.info("Registro < " + persona + " >"));
      }
   }

}
