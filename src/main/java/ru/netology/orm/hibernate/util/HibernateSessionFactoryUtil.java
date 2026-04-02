package ru.netology.orm.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.netology.orm.hibernate.model.Person;

/**
 * Утилита для создания и предоставления единственного {@link SessionFactory} на приложение.
 * <p>
 * Используется классический bootstrap: {@code hibernate.cfg.xml} из classpath плюс
 * регистрация аннотированных сущностей через {@link MetadataSources}.
 */
public final class HibernateSessionFactoryUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateSessionFactoryUtil.class);

    private static volatile SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {
    }

    /**
     * Ленивая инициализация {@link SessionFactory}; потокобезопасна для простого учебного сценария.
     *
     * @return фабрика сессий Hibernate
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateSessionFactoryUtil.class) {
                if (sessionFactory == null) {
                    sessionFactory = buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    /**
     * Собирает реестр сервисов, подключает сущность {@link Person} и строит {@link SessionFactory}.
     */
    private static SessionFactory buildSessionFactory() {
        LOG.debug("Инициализация SessionFactory");
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources(registry)
                    .addAnnotatedClass(Person.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (final Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            LOG.error("Ошибка при создании SessionFactory: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Корректно закрывает фабрику (вызывать при завершении приложения).
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            LOG.info("Закрытие SessionFactory");
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
