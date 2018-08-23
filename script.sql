CREATE DATABASE `challenge-file` /*!40100 DEFAULT CHARACTER SET utf8 */;

create table `challenge-file`.`transaction` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `card` INT NOT NULL,
   `value` DOUBLE NOT NULL,
   `transactionDate` DATE,
   PRIMARY KEY ( `id` ),
   KEY `idx_transaction_date` ( `transactionDate` )
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO transaction(id,card,value,transactionDate) VALUES(1, 473732632, 352636.23, '2018-08-15');
INSERT INTO transaction(id,card,value,transactionDate) VALUES(2, 473732632, 352636.23, '2018-08-15');
INSERT INTO transaction(id,card,value,transactionDate) VALUES(3, 473732632, 352636.23, '2018-08-15');
INSERT INTO transaction(id,card,value,transactionDate) VALUES(4, 473732632, 352636.23, '2018-08-16');
INSERT INTO transaction(id,card,value,transactionDate) VALUES(5, 473732632, 352636.23, '2018-08-16');
INSERT INTO transaction(id,card,value,transactionDate) VALUES(6, 473732632, 352636.23, '2018-08-16');
INSERT INTO transaction(id,card,value,transactionDate) VALUES(7, 473732632, 352636.23, '2018-08-16');
