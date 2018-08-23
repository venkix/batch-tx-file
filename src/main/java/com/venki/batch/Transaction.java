package com.venki.batch;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author carlosandrefernandes
 */
public class Transaction {

    private Integer id;
    private Integer card;
    private Double value;
    private Date transactionDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCard() {
        return card;
    }

    public void setCard(Integer card) {
        this.card = card;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionDateFRT() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        return dateFormat.format(getTransactionDate());
    }

    public String getValueFRT() {
        String pattern = "000000000.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String format = decimalFormat.format(getValue());
        return format.replace(".", "");
    }
    
    public String getCardFRT() {
        String pattern = "0000000000000000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String format = decimalFormat.format(getCard());
        return format;
    }    
}
