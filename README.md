# Учебный проект: ORM и Hibernate (Java)

Репозиторий предназначен для домашней работы по курсу Java по теме **объектно-реляционное отображение (ORM)** с использованием **Hibernate**. В проекте **одна** тематическая реализация; исходный код расположен в стандартной структуре Maven (`src/main/java`, `src/main/resources`).

> **Важно:** в некоторых методичках встречается шаблон описания для **Django**; данный репозиторий — **не** Django-проект, а **Java + Maven + Hibernate**.

## Структура репозитория

| Путь | Назначение |
|------|------------|
| `src/main/java/ru/netology/orm/hibernate/` | Пакет приложения: `Main`, `dao`, `model`, `util` |
| `src/main/resources/hibernate.cfg.xml` | Конфигурация подключения к БД и свойства Hibernate |
| `src/main/resources/logback.xml` | Настройка логирования в консоль и в каталог `log/` |
| `Docs/` | Формулировка задания и дополнительные материалы |
| `log/` | Файлы логов (создаётся при запуске; не коммитить содержимое) |
| `pom.xml` | Зависимости и сборка Maven |

## Установка и запуск

### Требования

- JDK **17** или новее  
- [Apache Maven](https://maven.apache.org/) 3.9+

### Установка зависимостей и сборка

```bash
mvn -q package
```

### Запуск демонстрационного приложения

Из корня репозитория:

```bash
mvn -q exec:java
```

Ожидаемое поведение: в консоль выводятся этапы CRUD (создание двух записей `Person`, список всех, обновление одной, удаление второй); в каталоге `log/` появляются файлы с префиксами `INFO_hibernate_app` и `DEBUG_hibernate_app` согласно `logback.xml`.

### Проверка вручную

1. Выполнить `mvn -q exec:java` — завершение с кодом 0, без stack trace.
2. Убедиться, что в логах есть сообщения об сохранении, обновлении и удалении записи.
3. При необходимости сравнить поведение с формулировкой в `Docs/ASSIGNMENT.md`.

## Описание классов и функций

### `ru.netology.orm.hibernate.Main`

- **`main(String[] args)`** — точка входа: создаёт каталог `log`, получает `SessionFactory`, выполняет демонстрацию через `PersonDao`, закрывает фабрику.
- **`runCrudDemo(PersonDao dao)`** — приватный сценарий: save → findAll → update → deleteById → проверка отсутствия удалённой записи.

### `ru.netology.orm.hibernate.model.Person`

Сущность «человек»: поля `id`, `fullName`, `email`; геттеры/сеттеры; конструкторы для JPA и для удобного создания в коде.

**Пример использования:**

```java
Person p = new Person("Иван Сидоров", "ivan@example.com");
```

### `ru.netology.orm.hibernate.util.HibernateSessionFactoryUtil`

- **`getSessionFactory()`** — возвращает единственный `SessionFactory` (ленивая инициализация).
- **`shutdown()`** — закрывает фабрику при завершении JVM-приложения.

### `ru.netology.orm.hibernate.dao.PersonDao`

| Метод | Назначение |
|-------|------------|
| `save(Person)` | `persist`, новый id после commit |
| `findById(Long)` | `Optional` с результатом `get` |
| `findAll()` | HQL-запрос всех `Person` по возрастанию `id` |
| `update(Person)` | `merge` для изменения полей |
| `deleteById(Long)` | `true`, если запись найдена и удалена |

**Пример:**

```java
PersonDao dao = new PersonDao(HibernateSessionFactoryUtil.getSessionFactory());
Person saved = dao.save(new Person("Имя", "email@test.ru"));
```

## Переменные окружения и секреты

Для демонстрации используется in-memory **H2** без пароля; отдельный `.env` не требуется. При переносе на PostgreSQL или MySQL задайте URL, логин и пароль в `hibernate.cfg.xml` или вынесите в внешний конфиг и **не коммитьте** секреты.

## История версий

| Версия | Изменения |
|--------|-----------|
| 1.0.0-SNAPSHOT | Первичная инициализация: Maven, Hibernate 6, сущность `Person`, DAO, демо CRUD, логирование Logback, документация в `README.md` и `Docs/ASSIGNMENT.md`. |
