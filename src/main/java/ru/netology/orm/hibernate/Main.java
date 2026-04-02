package ru.netology.orm.hibernate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.netology.orm.hibernate.dao.PersonDao;
import ru.netology.orm.hibernate.model.Person;
import ru.netology.orm.hibernate.util.HibernateSessionFactoryUtil;

/**
 * Точка входа: демонстрация жизненного цикла CRUD с Hibernate.
 * <p>
 * Перед запуском убедитесь, что каталог {@code log} существует или создаётся автоматически
 * (Logback создаст файлы при первой записи, если родительская папка есть — при необходимости создайте {@code log} вручную).
 */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        try {
            Files.createDirectories(Path.of("log"));
        } catch (final Exception e) {
            System.err.println("Не удалось создать каталог log: " + e.getMessage());
        }
        LOG.info("Запуск демонстрации Hibernate ORM");
        final PersonDao dao = new PersonDao(HibernateSessionFactoryUtil.getSessionFactory());
        try {
            runCrudDemo(dao);
        } finally {
            HibernateSessionFactoryUtil.shutdown();
            LOG.info("Завершение работы приложения");
        }
    }

    /**
     * Последовательно выполняет создание, чтение, обновление и удаление записей.
     *
     * @param dao DAO для работы с {@link Person}
     */
    private static void runCrudDemo(final PersonDao dao) {
        final Person alice = dao.save(new Person("Алиса Иванова", "alice@example.com"));
        final Person bob = dao.save(new Person("Борис Петров", "bob@example.com"));
        LOG.info("Созданы записи: {} и {}", alice.getId(), bob.getId());

        final List<Person> all = dao.findAll();
        LOG.info("Всего записей в БД: {}", all.size());
        all.forEach(p -> LOG.info("  {}", p));

        alice.setFullName("Алиса Иванова-Петрова");
        dao.update(alice);
        dao.findById(alice.getId()).ifPresent(p -> LOG.info("После обновления: {}", p));

        final boolean removed = dao.deleteById(bob.getId());
        LOG.info("Удаление id={}: {}", bob.getId(), removed);
        dao.findById(bob.getId()).ifPresentOrElse(
                p -> LOG.warn("Неожиданно: запись всё ещё есть {}", p),
                () -> LOG.info("Запись id={} отсутствует в БД (ожидаемо)", bob.getId()));
    }
}
