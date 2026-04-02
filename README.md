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
| `src/test/java/.../PersonDaoIntegrationTest.java` | Интеграционный тест CRUD (JUnit 5) |

## Соответствие заданию (`Docs/ASSIGNMENT.md`)

| Требование | Реализация |
|------------|------------|
| Maven, Hibernate и драйвер СУБД | `pom.xml`: `hibernate-core`, H2 |
| Сущность с JPA-аннотациями | `Person`: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column` |
| DAO с `Session` и транзакциями | `PersonDao`: `openSession`, `beginTransaction`, commit/rollback |
| Сценарий: сохранение, список, обновление, удаление | `Main.runCrudDemo`, дублируется проверками в `PersonDaoIntegrationTest` |
| Логирование этапов | SLF4J + Logback: консоль и файлы в `log/` |

**Критерии приёмки из задания:** сборка `mvn -q -DskipTests package` (или полный цикл с тестами `mvn -q test`); запуск `mvn -q exec:java` без необработанных исключений; в консоли и логах видны операции с БД. На дату обновления документации все перечисленные команды выполнялись успешно (код выхода 0).

## Установка и запуск

### Требования

- JDK **17** или новее  
- [Apache Maven](https://maven.apache.org/) 3.9+

### Установка зависимостей и сборка

```bash
mvn -q package
```

### Автоматические тесты

Проверка сценария CRUD на той же in-memory H2, что и основное приложение:

```bash
mvn -q test
```

### Запуск демонстрационного приложения

Из корня репозитория:

```bash
mvn -q exec:java
```

Ожидаемое поведение: в консоль выводятся этапы CRUD (создание двух записей `Person`, список всех, обновление одной, удаление второй); в каталоге `log/` появляются файлы с префиксами `INFO_hibernate_app` и `DEBUG_hibernate_app` согласно `logback.xml`.

### Проверка вручную

1. Выполнить `mvn -q test` — все тесты зелёные.
2. Выполнить `mvn -q exec:java` — код выхода 0, без необработанных исключений в выводе.
3. Просмотреть `log/INFO_hibernate_app*.log` и при необходимости `DEBUG_*` — должны быть сообщения о сохранении, обновлении и удалении.
4. Сверить требования с таблицей в разделе «Соответствие заданию» и с `Docs/ASSIGNMENT.md`.

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

### `ru.netology.orm.hibernate.PersonDaoIntegrationTest` (тесты)

- **`crudScenarioPersistsUpdatesAndDeletes()`** — один интеграционный сценарий: два `save`, `findAll` (размер 2), `update`, `deleteById`, проверка отсутствия удалённой записи.

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
| 1.0.0-SNAPSHOT | Maven, Hibernate 6, сущность `Person`, DAO, демо в `Main`, Logback, `Docs/ASSIGNMENT.md`. Дополнено: интеграционный тест `PersonDaoIntegrationTest`, Surefire, матрица соответствия заданию в README и в `Docs/ASSIGNMENT.md`, обновлены инструкции проверки. |
