package com.chrzanowski.telegrambot.data.customersettings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSettingsRepository extends JpaRepository<CustomerSettings, Long> {

}
