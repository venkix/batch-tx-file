package com.venki.batch;

import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author carlosandrefernandes
 */
public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {
    
    @Override
    public Transaction process(Transaction transaction) throws Exception {
        return transaction;
    }

}
