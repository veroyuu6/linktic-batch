package com.linktic.batch.config;

import javax.sql.DataSource;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.linktic.batch.listener.JobCsvListener;
import com.linktic.batch.listener.JobDbListener;
import com.linktic.batch.model.Person;
import com.linktic.batch.processor.PersonCsvProcessor;
import com.linktic.batch.processor.PersonDbProcessor;

import lombok.RequiredArgsConstructor;

/**
 * Configuración de Spring Batch.
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

   @Value("${path.file}")
   private String pathFile;

   public final JobBuilderFactory jobBuilderFactory;

   public final StepBuilderFactory stepBuilderFactory;

   private final DataSource dataSource;

   /**
    * Crea un lector de archivos plano para datos de personas (CSV).
    *
    * @return FlatFileItemReader<Person> Lector de archivos plano configurado para leer datos de personas desde un archivo CSV.
    */
   @Bean
   @StepScope
   public FlatFileItemReader<Person> readerCsv() {
      return new FlatFileItemReaderBuilder<Person>()
            .name("personaItemReader")
            .resource(new ClassPathResource(pathFile))
            .linesToSkip(NumberUtils.INTEGER_ONE)
            .delimited()
            .delimiter(",")
            .names(new String[] { "id", "nombre", "email" })
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
               setTargetType(Person.class);
            }})
            .build();
   }

   /**
    * Crea un lector de base de datos para datos de personas.
    *
    * @return JdbcCursorItemReader<Person> Lector de base de datos configurado para leer datos de personas desde la base de datos.
    */
   @Bean
   @StepScope
   public JdbcCursorItemReader<Person> readerDb() {
      JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<>();
      reader.setSql("SELECT id, nombre, email FROM Person");
      reader.setDataSource(dataSource);
      reader.setFetchSize(100);
      reader.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
      return reader;
   }

   /**
    * Procesador de datos de personas para archivos CSV.
    *
    * @return PersonCsvProcessor Procesador de datos de personas para archivos CSV.
    */
   @Bean
   public PersonCsvProcessor processorCsv() {
      return new PersonCsvProcessor();
   }

   /**
    * Procesador de datos de personas para base de datos.
    *
    * @return PersonDbProcessor Procesador de datos de personas para base de datos.
    */
   @Bean
   public PersonDbProcessor processorDb() {
      return new PersonDbProcessor();
   }

   /**
    * Crea un escritor de lote JDBC para datos de personas desde archivos CSV.
    *
    * @param dataSource DataSource para la conexión a la base de datos.
    * @return JdbcBatchItemWriter<Person> Escritor de lote JDBC configurado para escribir datos de personas desde archivos CSV a la base de datos.
    */
   @Bean
   public JdbcBatchItemWriter<Person> writerCsv(DataSource dataSource) {
      return new JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("MERGE INTO Person (id, nombre, email) KEY(id) VALUES (:id, :nombre, :email)")
            .dataSource(dataSource)
            .build();
   }

   /**
    * Crea un escritor de lote JDBC para datos de personas desde la base de datos.
    *
    * @param dataSource DataSource para la conexión a la base de datos.
    * @return JdbcBatchItemWriter<Person> Escritor de lote JDBC configurado para escribir datos de personas desde la base de datos a otra tabla en
    * la misma base de datos.
    */
   @Bean
   public JdbcBatchItemWriter<Person> writerDb(DataSource dataSource) {
      return new JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("MERGE INTO Person_Migrate (id, nombre, email) KEY(id) VALUES (:id, :nombre, :email)")
            .dataSource(dataSource)
            .build();
   }

   /**
    * Crea un trabajo de Spring Batch para leer datos de personas desde archivos CSV.
    *
    * @param jobCsvListener Listener para el trabajo de lectura de CSV.
    * @param step1          Paso del trabajo para leer datos de CSV.
    * @return Job Trabajo de Spring Batch configurado para leer datos de personas desde archivos CSV.
    */
   @Bean
   public Job personFromCvJob(JobCsvListener jobCsvListener, Step step1) {
      return jobBuilderFactory.get("personFromCvJob").incrementer(new RunIdIncrementer()).listener(jobCsvListener).flow(step1).end().build();
   }

   /**
    * Crea un trabajo de Spring Batch para leer datos de personas desde la base de datos.
    *
    * @param jobDbListener Listener para el trabajo de lectura de la base de datos.
    * @param step2         Paso del trabajo para leer datos de la base de datos.
    * @return Job Trabajo de Spring Batch configurado para leer datos de personas desde la base de datos.
    */
   @Bean
   public Job personFromDbJob(JobDbListener jobDbListener, Step step2) {
      return jobBuilderFactory.get("personFromDbJob").incrementer(new RunIdIncrementer()).listener(jobDbListener).flow(step2).end().build();
   }

   /**
    * Crea un paso de Spring Batch para leer datos de personas desde archivos CSV.
    *
    * @param writerCsv Escritor de lote JDBC para escribir datos de personas desde archivos CSV.
    * @return Step Paso de Spring Batch configurado para leer datos de personas desde archivos CSV.
    */
   @Bean
   public Step step1(JdbcBatchItemWriter<Person> writerCsv) {
      return stepBuilderFactory.get("step1").<Person, Person>chunk(10).reader(readerCsv()).processor(processorCsv()).writer(writerCsv).build();
   }

   /**
    * Crea un paso de Spring Batch para leer datos de personas desde la base de datos.
    *
    * @param writerDb Escritor de lote JDBC para escribir datos de personas desde la base de datos.
    * @return Step Paso de Spring Batch configurado para leer datos de personas desde la base de datos.
    */
   @Bean
   public Step step2(JdbcBatchItemWriter<Person> writerDb) {
      return stepBuilderFactory.get("step2").<Person, Person>chunk(10).reader(readerDb()).processor(processorDb()).writer(writerDb).build();
   }

}
