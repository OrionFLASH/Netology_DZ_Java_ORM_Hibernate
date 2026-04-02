package ru.netology.orm.hibernate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Сущность «Человек» — пример доменной модели для демонстрации маппинга Hibernate.
 * <p>
 * Таблица в БД создаётся автоматически при {@code hibernate.hbm2ddl.auto=create-drop}.
 */
@Entity
@Table(name = "persons")
public class Person {

    /**
     * Суррогатный первичный ключ; генерируется СУБД (IDENTITY для H2).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ФИО; обязательное поле, длина ограничена для согласованности со схемой БД.
     */
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    /**
     * Электронная почта; уникальность обеспечивается на уровне приложения в демо-коде.
     */
    @Column(name = "email", nullable = false, length = 120)
    private String email;

    /** Конструктор по умолчанию — требование JPA для сущностей. */
    public Person() {
    }

    /**
     * Удобный конструктор для создания новых записей без id (id выставит Hibernate после persist).
     *
     * @param fullName полное имя
     * @param email    адрес электронной почты
     */
    public Person(final String fullName, final String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{id=" + id + ", fullName='" + fullName + "', email='" + email + "'}";
    }
}
