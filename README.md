# Task Manager

Многопользовательский планировщик задач с микросервисной архитектурой.

Проект позволяет пользователям регистрироваться, авторизовываться с использованием JWT и управлять личными задачами: создавать, редактировать, отмечать выполненными и удалять. В качестве источника вдохновения используется Trello, но приложение сознательно остаётся компактным учебным проектом.

Проект создан для практики разработки Java Backend приложений с использованием Spring Boot, Kafka, Docker Compose, PostgreSQL, JWT, Flyway и интеграции с LLM.

## Возможности

- Регистрация пользователей
- JWT-аутентификация и авторизация
- Создание задач
- Редактирование заголовка и описания задачи
- Пометка задачи как выполненной
- Удаление задач
- Email-уведомление после регистрации
- Периодический отчёт по задачам
- Асинхронное взаимодействие сервисов через Kafka
- Локальный запуск полного окружения через Docker Compose
- Healthcheck всех сервисов через Spring Boot Actuator

## Архитектура

Приложение состоит из четырёх микросервисов и общего модуля контрактов сообщений.

| Модуль | Назначение |
|---|---|
| `backend` | Основной REST API: регистрация, JWT-аутентификация и управление задачами |
| `email-sender` | Kafka consumer, который обрабатывает задачи отправки email и отправляет письма через SMTP |
| `scheduler-service` | Периодически инициирует формирование отчётов по задачам |
| `summarization-service` | Обрабатывает запросы суммаризации через LLM-интеграцию |
| `messaging-contracts` | Общие DTO и названия Kafka topics для межсервисного взаимодействия |

### Взаимодействие сервисов

```text
Client
  |
  | HTTP / REST
  v
backend
  |
  | Kafka events
  +-----------------------------+
  |                             |
  v                             v
email-sender            summarization-service
  |
  v
Mailpit

scheduler-service
  |
  | Kafka events / internal integration
  v
backend and email-sender
```

- `backend` — единственный сервис, предоставляющий бизнес REST API.
- `email-sender`, `scheduler-service` и `summarization-service` выполняют фоновые задачи и взаимодействуют с другими сервисами через Kafka.
- PostgreSQL используется для хранения данных.
- Mailpit используется локально как тестовый SMTP-сервер и web-интерфейс для просмотра писем.
- Kafka UI используется локально для просмотра topics, сообщений, consumer groups и offsets.

## Технологии

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Spring Kafka
- Apache Kafka
- Docker
- Docker Compose
- Spring Boot Actuator
- Mailpit
- Kafka UI
- Gradle 
- Springdoc OpenAPI / Swagger UI

## Требования

Для локального запуска необходимы:

- JDK 21
- Docker Desktop
- Docker Compose v2
- Git

Проверить установленные версии:

```powershell
java -version
docker --version
docker compose version
```

## Конфигурация

Проект использует env-файлы, которые не должны содержать реальные секреты в Git.

Структура локальной конфигурации:

```text
compose/
├── .env.docker.compose.local
├── .env.backend.docker.local
├── .env.email-sender.docker.local
├── .env.scheduler.docker.local
└── .env.summarization.docker.local
```

### Общий Compose env-файл

Файл `compose/.env.docker.compose.local` используется Docker Compose для подстановки значений непосредственно в `compose.yaml`.

Пример:

```dotenv
POSTGRES_PASSWORD=local-postgres-password
```



Активный Spring profile для контейнеров задаётся в `compose/compose.yaml`:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: dev
```

> Не добавляйте реальные ключи, пароли и токены в Git-репозиторий.

## Локальный запуск

### 0.  Env файлы

Перед запуском проект должен содержать все вышеуказанные env файлы

### 1. Собрать проект

Из корня репозитория:

```powershell
./gradlew clean build
```

Команда компилирует все Gradle-модули, запускает тесты и собирает JAR-файлы в каталогах `build/libs`.

### 2. Проверить Compose-конфигурацию

```powershell
docker compose --env-file compose/.env.docker.compose.local -f compose/compose.yaml -p task-manager-local config
```

### 3. Собрать образы и поднять полный стек

```powershell
docker compose --env-file compose/.env.docker.compose.local -f compose/compose.yaml -p task-manager-local up -d --build --wait --wait-timeout 240
```

Команда:

1. Собирает Docker images Java-сервисов.
2. Создаёт Docker network и PostgreSQL volume.
3. Запускает PostgreSQL, Kafka, Mailpit и Kafka UI.
4. Ждёт readiness infrastructure-сервисов.
5. Запускает четыре микросервиса.
6. Ожидает успешного завершения healthcheck’ов.

### 4. Проверить состояние сервисов

```powershell
docker compose --env-file compose/.env.docker.compose.local -f compose/compose.yaml -p task-manager-local ps
```

Ожидаемый статус:

```text
postgres                 running (healthy)
kafka                    running (healthy)
mailpit                  running (healthy)
kafka-ui                 running
backend                  running (healthy)
email-sender             running (healthy)
scheduler-service        running (healthy)
summarization-service    running (healthy)
```

## Локальные сервисы

| Сервис | Адрес | Назначение |
|---|---|---|
| Backend API | `http://localhost:8080` | REST API приложения |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` | Документация и интерактивное тестирование Backend REST API |
| Backend Actuator | `http://localhost:8081/actuator/health` | Health endpoint backend |
| Email sender Actuator | `http://localhost:8082/actuator/health` | Health endpoint email-sender |
| Scheduler Actuator | `http://localhost:8083/actuator/health` | Health endpoint scheduler-service |
| Summarization Actuator | `http://localhost:8084/actuator/health` | Health endpoint summarization-service |
| Kafka UI | `http://localhost:8085` | Просмотр Kafka topics и consumer groups |
| Mailpit | `http://localhost:8025` | Просмотр отправленных писем |
| PostgreSQL | `localhost:5432` | Локальное подключение к базе |
| Kafka | `localhost:9092` | Локальное подключение к Kafka |

Проверить readiness backend:

```powershell
curl.exe -i http://localhost:8081/actuator/health/readiness
```

## Swagger UI

Swagger UI доступен после запуска backend:

```text
http://localhost:8080/swagger-ui/index.html
```

Через Swagger UI можно зарегистрировать пользователя, выполнить login, получить JWT access token и протестировать REST API задач.

Для доступа к защищённым endpoint’ам нажмите `Authorize` и вставьте access token. Swagger UI автоматически добавит заголовок `Authorization: Bearer <token>` к запросам.
## Проверка основного сценария

После запуска Compose можно вручную проверить интеграцию сервисов:

1. Зарегистрировать пользователя через backend API.
2. Убедиться, что пользователь сохранён в PostgreSQL.
3. Проверить в Kafka UI событие или задачу на отправку email.
4. Убедиться, что `email-sender` обработал сообщение.
5. Открыть Mailpit и проверить welcome email.
6. Создать несколько задач через backend API.
7. Пометить часть задач выполненными.
8. Дождаться запуска scheduler-service.
9. Проверить Kafka UI и Mailpit для отчёта по задачам.

## Healthcheck

Для локального Compose настроены healthcheck’и:

- PostgreSQL проверяется командой `pg_isready`.
- Kafka проверяется через `kafka-topics.sh`.
- Mailpit проверяется по HTTP API.
- Каждый Spring Boot сервис проверяется через Actuator readiness endpoint:
    - `/actuator/health/readiness`.

Healthcheck выполняется внутри контейнера, поэтому для него используется `localhost` и внутренний management port сервиса.

## Статус проекта

Реализовано:

- Multi-module Gradle проект
- Четыре Spring Boot микросервиса
- PostgreSQL и Flyway migrations
- JWT-аутентификация
- REST API для работы с пользователями и задачами
- Kafka-based коммуникация сервисов
- Email-уведомления через Mailpit
- Spring Boot Actuator и Docker healthcheck’и
- Локальный запуск полного микросервисного окружения через Docker Compose

В разработке:

- CI/CD pipeline
- Автоматический compose smoke test
- Публикация Docker images в Container Registry