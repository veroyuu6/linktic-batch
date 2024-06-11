package com.linktic.batch.controller;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linktic.batch.util.ConstantsUtil;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para ejecutar trabajos de Spring Batch relacionados con la entidad Person.
 */
@RestController
@RequestMapping(value = "${context.application}")
@RequiredArgsConstructor
public class PersonBatchController {

   private final JobLauncher jobLauncher;

   private final Job personFromCvJob;

   private final Job personFromDbJob;

   /**
    * Endpoint para procesar un archivo CSV y ejecutar el trabajo correspondiente.
    *
    * @return ResponseEntity con el estado del lote
    * @throws JobInstanceAlreadyCompleteException si la instancia del trabajo ya está completa
    * @throws JobExecutionAlreadyRunningException si la ejecución del trabajo ya está en curso
    * @throws JobParametersInvalidException si los parámetros del trabajo son inválidos
    * @throws JobRestartException si el trabajo no se puede reiniciar
    */
   @GetMapping(value = "${endpoint.processor.csv.get}")
   public ResponseEntity<BatchStatus> processCsv()
         throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
      jobLauncher.run(personFromCvJob, getJobParameters(personFromCvJob));
      return ResponseEntity.ok().body(jobLauncher.run(personFromDbJob, getJobParameters(personFromDbJob)).getStatus());
   }

   /**
    * Endpoint para procesar datos desde la base de datos y ejecutar el trabajo correspondiente.
    *
    * @return ResponseEntity con el estado del lote
    * @throws JobInstanceAlreadyCompleteException si la instancia del trabajo ya está completa
    * @throws JobExecutionAlreadyRunningException si la ejecución del trabajo ya está en curso
    * @throws JobParametersInvalidException si los parámetros del trabajo son inválidos
    * @throws JobRestartException si el trabajo no se puede reiniciar
    */
   @GetMapping(value = "${endpoint.processor.db.get}")
   public ResponseEntity<BatchStatus> processDb()
         throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
      return ResponseEntity.ok().body(jobLauncher.run(personFromDbJob, getJobParameters(personFromDbJob)).getStatus());
   }

   /**
    * Crea los parámetros necesarios para ejecutar un trabajo de Spring Batch.
    *
    * @param job el trabajo para el cual se crearán los parámetros
    * @return JobParameters los parámetros del trabajo
    */
   private JobParameters getJobParameters(final Job job) {
      return new JobParametersBuilder()
            .addString(ConstantsUtil.JOB_ID, String.valueOf(System.currentTimeMillis()))
            .addString(ConstantsUtil.JOB_NAME, job.getName())
            .toJobParameters();
   }
}
