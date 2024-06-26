package com.chrzanowski.telegrambot.data.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findCustomerByTelegramId(Long id);

    Boolean existsCustomerByTelegramId(Long id);
}
