package ru.netology.orm.hibernate.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.netology.orm.hibernate.model.Person;

/**
 * Объект доступа к данным для сущности {@link Person}.
 * <p>
 * Каждая публичная операция открывает сессию, выполняет работу в транзакции и закрывает сессию.
 */
public class PersonDao {

    private static final Logger LOG = LoggerFactory.getLogger(PersonDao.class);

    private final SessionFactory sessionFactory;

    /**
     * @param sessionFactory фабрика сессий Hibernate (обычно синглтон на приложение)
     */
    public PersonDao(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Сохраняет новую сущность в БД (операция INSERT).
     *
     * @param person transient-объект без id или с null id
     * @return сущность с заполненным id
     */
    public Person save(final Person person) {
        LOG.debug("Сохранение Person: {}", person);
        try (Session session = sessionFactory.openSession()) {
            final Transaction tx = session.beginTransaction();
            try {
                session.persist(person);
                tx.commit();
                LOG.info("Сохранена запись Person id={}", person.getId());
                return person;
            } catch (final Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Поиск по идентификатору.
     *
     * @param id первичный ключ
     * @return {@link Optional} с detached-сущностью или пустой
     */
    public Optional<Person> findById(final Long id) {
        LOG.debug("Поиск Person по id={}", id);
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Person.class, id));
        }
    }

    /**
     * Все записи таблицы persons (осторожно на больших объёмах данных).
     *
     * @return список сущностей
     */
    public List<Person> findAll() {
        LOG.debug("Выборка всех Person");
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Person order by id", Person.class).list();
        }
    }

    /**
     * Обновляет поля существующей сущности (сущность должна иметь id).
     *
     * @param person managed-подобный объект с заполненным id
     */
    public void update(final Person person) {
        LOG.debug("Обновление Person id={}", person.getId());
        try (Session session = sessionFactory.openSession()) {
            final Transaction tx = session.beginTransaction();
            try {
                session.merge(person);
                tx.commit();
                LOG.info("Обновлена запись Person id={}", person.getId());
            } catch (final Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Удаление по идентификатору.
     *
     * @param id первичный ключ
     * @return true, если запись была найдена и удалена
     */
    public boolean deleteById(final Long id) {
        LOG.debug("Удаление Person id={}", id);
        try (Session session = sessionFactory.openSession()) {
            final Transaction tx = session.beginTransaction();
            try {
                final Person found = session.get(Person.class, id);
                if (found == null) {
                    tx.rollback();
                    return false;
                }
                session.remove(found);
                tx.commit();
                LOG.info("Удалена запись Person id={}", id);
                return true;
            } catch (final Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
