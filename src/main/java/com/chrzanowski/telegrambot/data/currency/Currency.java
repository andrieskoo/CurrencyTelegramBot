package com.chrzanowski.telegrambot.data.currency;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Objects;

@Data
@Entity
public class Currency {
    @Id
    private Integer iso4217Code;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(iso4217Code, currency.iso4217Code) && Objects.equals(name, currency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iso4217Code, name);
    }
}
