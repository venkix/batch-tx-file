package com.venki.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author carlosandrefernandes
 */
@SpringBootApplication
@EnableAutoConfiguration
public class SpringBatchApplication {

    public static void main(String... args) throws Exception {
        if (args.length != 0 && args.length % 2 == 1) {
            // First parameter is jobname
            String jobName = args[0];
            Map<String, String> params = new HashMap<String, String>();
            for (int i = 1; i < args.length; i += 2) {
                String key = args[i];
                String value = args[i + 1];
                params.put(key.replace("-", ""), value);
            }

            params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

            // Start
            ConfigurableApplicationContext context = SpringApplication.run(SpringBatchApplication.class, args);
            JobLauncher jobLauncher = context.getBean("jobLauncher", JobLauncher.class);
            Properties property = new Properties();
            for (Map.Entry<String, String> set : params.entrySet()) {
                property.put(set.getKey(), set.getValue());
            }
            JobParameters jobParameters = new DefaultJobParametersConverter().getJobParameters(property);
            Job job = (Job) context.getBean(jobName, Job.class);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        } else {
            // Error
            throw new Exception("Need at least one argument jobName and parameter should be --key value");
        }
    }
}
