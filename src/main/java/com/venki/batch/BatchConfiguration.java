package com.venki.batch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author carlosandrefernandes
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("${transaction.chunk.size}")
    private int transaction_chunk_size;
    
    @Value("${spring.datasource.driver}")
    private String dbdriver;
    
    @Value("${spring.datasource.url}")
    private String dburl;
        
    @Value("${spring.datasource.username}")
    private String dbuser;

    @Value("${spring.datasource.password}")
    private String dbpw;
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbdriver);
        dataSource.setUrl(dburl);
        dataSource.setUsername(dbuser);
        dataSource.setPassword(dbpw);

        return dataSource;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Transaction> pagingReader(@Value("#{jobParameters[date]}") String date){
        if(date==null){
            date=getToday();
        }
        
        JdbcPagingItemReader<Transaction> reader
                = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setPageSize(transaction_chunk_size);
        reader.setRowMapper(new TransactionRowMapper());        
        reader.setQueryProvider(createQueryProvider());
        
        Map<String, Object> map = new HashMap<>();
        map.put("transactionDate",date);

        reader.setParameterValues(map);
        
        return reader;
    }

    private PagingQueryProvider createQueryProvider() {
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

        queryProvider.setSelectClause("SELECT id, card, value, transactionDate ");
        queryProvider.setFromClause("FROM transaction");
        queryProvider.setWhereClause("where transactionDate = :transactionDate");
        
        Map<String, Order> sortKeys = new HashMap<>(1);
	sortKeys.put("transactionDate", Order.ASCENDING);
        
	queryProvider.setSortKeys(sortKeys);

        return queryProvider;
    }

    public class TransactionRowMapper implements RowMapper<Transaction> {

        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getInt("id"));
            transaction.setCard(rs.getInt("card"));
            transaction.setValue(rs.getDouble("value"));
            transaction.setTransactionDate(rs.getDate("transactionDate"));
            return transaction;
        }

    }

    @Bean
    public TransactionItemProcessor processor() {
        return new TransactionItemProcessor();
    }

    private BeanWrapperFieldExtractor<Transaction> fieldExtractor() {
        BeanWrapperFieldExtractor<Transaction> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"cardFRT", "valueFRT", "transactionDateFRT"});
        return fieldExtractor;
    }

    @Bean
    public FlatFileItemWriter<Transaction> fixedWriter() {
        FlatFileItemWriter<Transaction> fixedWriter = new FlatFileItemWriter<>();
        fixedWriter.setResource(new FileSystemResource("./transactions.txt"));
        fixedWriter.setAppendAllowed(false);
        fixedWriter.setLineAggregator(formatterLineAggregator());
        return fixedWriter;
    }

    private LineAggregator<Transaction> formatterLineAggregator() {
        FormatterLineAggregator<Transaction> lineAggregator = new FormatterLineAggregator<>();
        lineAggregator.setFieldExtractor(fieldExtractor());
        lineAggregator.setFormat("%-16s%-11s%-8s");
        return lineAggregator;
    }    

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }
    
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Transaction, Transaction>chunk(transaction_chunk_size)
                .reader(pagingReader(null))
                .processor(processor())
                .writer(fixedWriter())
                .build();
    }

    @Bean
    public Job exportTransactionJob() {
        return jobBuilderFactory.get("exportTransactionJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

}
