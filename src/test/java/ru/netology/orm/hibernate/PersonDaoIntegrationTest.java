package ru.netology.orm.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.orm.hibernate.dao.PersonDao;
import ru.netology.orm.hibernate.model.Person;
import ru.netology.orm.hibernate.util.HibernateSessionFactoryUtil;

/**
 * Интеграционная проверка слоя DAO и Hibernate на in-memory H2.
 * <p>
 * Дублирует по смыслу сценарий из {@link Main}, но в виде утверждений JUnit —
 * удобно для регрессии при изменениях кода.
 */
class PersonDaoIntegrationTest {

    @BeforeAll
    static void initSessionFactory() {
        HibernateSessionFactoryUtil.getSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        HibernateSessionFactoryUtil.shutdown();
    }

    @Test
    void crudScenarioPersistsUpdatesAndDeletes() {
        final PersonDao dao = new PersonDao(HibernateSessionFactoryUtil.getSessionFactory());

        final Person first = dao.save(new Person("Тест А", "test-a@example.com"));
        final Person second = dao.save(new Person("Тест Б", "test-b@example.com"));
        assertNotNull(first.getId());
        assertNotNull(second.getId());
        assertEquals(2, dao.findAll().size());

        first.setFullName("Тест А (обновлён)");
        dao.update(first);
        assertEquals("Тест А (обновлён)", dao.findById(first.getId()).orElseThrow().getFullName());

        assertTrue(dao.deleteById(second.getId()));
        assertEquals(1, dao.findAll().size());
        assertFalse(dao.findById(second.getId()).isPresent());
    }
}
