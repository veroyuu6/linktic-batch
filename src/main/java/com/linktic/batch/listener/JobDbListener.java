package com.linktic.batch.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Listener para el job que procesa datos desde una base de datos.
 */
@Slf4j
@Component
public class JobDbListener extends JobExecutionListenerSupport {

   /**
    * Método que se ejecuta después de que el job ha finalizado.
    * Imprime un mensaje indicando que el job ha finalizado.
    *
    * @param jobExecution objeto que representa la ejecución del job
    */
   @Override
   public void afterJob(JobExecution jobExecution) {
      if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
         log.info("FINALIZÓ EL JOB!! Verifica los resultados:");
      }
   }

}
