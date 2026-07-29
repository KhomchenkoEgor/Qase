# Qase.io UI & API Test Automation Framework

Проект представляет собой модульный фреймворк для автоматизации тестирования UI и API функционала системы управления тест-кейсами **Qase.io**. 

Фреймворк построен с использованием гибридного подхода (UI + API), изоляции тестовых данных и динамической генерации контекста через облачную LLM (Mistral API) с надёжным fallback-механизмом на случай недоступности ключа/сервиса.
## 🛠 Технологический стек

* **Язык программирования:** Java 17
* **Сборщик проектов:** Maven 
* **Тестовый движок:** TestNG 
* **Автоматизация UI:** Selenide 
* **Тестирование API:** Rest Assured + JSON Schema Validator
* **Генерация данных:** Mistral API (`mistral-medium-latest`) через REST-клиент, с локальным Java-фолбэком при недоступности ключа/сервиса
* **Утилиты:** Lombok, Log4j2
* **Отчетность:** Allure Report
* **CI/CD:** GitHub Actions

---

## 🏗 Архитектура и ключевые решения

### 1. Паттерны проектирования UI
* **Page Object Model (POM):** Разделение логики страниц и непосредственно логики тестов.
* **Fluent/Chain of States API:** Реализация цепочки вызовов методов на страницах для улучшения читаемости и поддержки кода тестов.
* **Scoping / Ограничение области поиска:** Локаторы элементов страниц инкапсулированы внутри специфичных UI-компонентов, что минимизирует дублирование кода и повышает стабильность тестов при изменении верстки.

### 2. API Слой и Управление Данными (Data Management)
* **Паттерн Адаптер (Adapter):** API-запросы к сущностям Qase (Projects, Suites, Cases) обернуты в специализированные классы-адаптеры.
* **API Seeding (Гибридный подход):** Для UI-тестов создание предусловий (pre-conditions) и необходимых сущностей выполняется через API-запросы. Это существенно сокращает время выполнения тест-дизайна.
* **Атомарность и Очистка данных:** Каждому тесту генерируются уникальные сущности. После завершения тестов в блоке `@AfterMethod` вызываются API-методы для полной очистки созданных данных (Teardown), гарантируя независимость запусков.
* **DTO (Data Transfer Object):** Моделирование тела запросов и ответов реализовано через Lombok-модели (`@Data`, `@Builder`).

### 3. Интеграция с ИИ (AI-Driven Testing)
* Вместо стандартных библиотек генерации фейковых данных (Faker), в проекте реализована интеграция с облачной моделью **Mistral (`mistral-medium-latest`)** через REST-клиент.
* Через REST-клиент модель динамически генерирует реалистичные контекстные данные (названия проектов, описания тест-кейсов, предусловия) по заданному системному промту, имитируя действия реального QA-инженера.

---

## 📝 Тестовое покрытие (Checklist)

В рамках фреймворка автоматизированы следующие критические сценарии платформы Qase.io:

### 1. API layer (Pre-conditions & Core)
* **Projects API:** Создание нового проекта, валидация JSON-схемы ответа, удаление проекта.
* **Suites API:** Создание тест-сьютов внутри проекта с динамическими данными от LLM.
* **Cases API:** Создание, чтение и удаление тест-кейсов через адаптеры.

### 2. UI layer (End-to-End & Validation)
* **Auth Flow:** Авторизация пользователя (Positive/Negative сценарии, проверка нотификаций об ошибках).
* **Project Dashboard:** Проверка создания проекта через UI (валидация полей ввода, маски кодов проекта).
* **Repository Management:** Создание тест-кейса через UI-форму с использованием сгенерированных ИИ данных, проверка сохранения и отображения в дереве репозитория.
* **Hybrid E2E:** Создание проекта через API -> проверка его физического отображения в UI-интерфейсе -> удаление через API (Очистка).

---

## 📊 Пайплайн и Отчетность

* **CI/CD пайплайн** настроен в GitHub Actions. Тесты запускаются автоматически при пуше или создании Pull Request.
* Тестирование UI в CI выполняется в **Headless-режиме**.
* После завершения пайплайна генерируется детализированный отчет **Allure Report**, включающий в себя:
  * Шаги выполнения (`@Step`) для API и UI действий.
  * Логирование запросов и ответов Rest Assured.
  * Скриншоты и исходный код страниц при падении UI-тестов.

---

### 📂 Структура проекта

```text
.
├── .github/workflows/gitHubActions.yml
├── pom.xml
└── src
    ├── main/java
    │   ├── api
    │   │   ├── adapters      # RestAssured-обёртки над сущностями Qase
    │   │   └── models        # DTO (Rq/Rs) для cases, plan, project, suite
    │   ├── ui
    │   │   ├── dict          # Константы текстов интерфейса
    │   │   ├── locators      # Локаторы, сгруппированные по страницам
    │   │   └── pages         # Page Object Model на Selenide
    │   └── utils
    │       ├── PropertyReader.java
    │       └── QwenDataGenerator.java
    └── test
        ├── java
        │   ├── listeners     # TestListener, RetryListener
        │   ├── tests/api     # BaseApiTest + 5 API-теста
        │   ├── tests/ui      # BaseTest + 4 UI-теста
        │   └── utils         # AllureUtils, Retry
        └── resources
            ├── schemas/create_project_schema.json
            ├── allure.properties
            ├── config.properties.example
            ├── log4j2-test.xml
            ├── tests.xml            # полный прогон (дефолт surefire)
            ├── api-smoke.xml
            ├── api-regression.xml
            ├── ui-smoke.xml
            ├── ui-regression.xml
            └── crossbrowser.xml     # последовательный прогон в 3 браузерах
```
---

## 🚀 Локальный запуск проекта

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com
   ```
2. Для работы AI-генерации данных получите API-ключ Mistral (https://console.mistral.ai) — без него сработает локальный фолбэк с заранее заданными данными.
3. Скопируйте `src/test/resources/config.properties.example` в `src/test/resources/config.properties` и укажите свои данные: `user`, `password`, `token`, `mistral_api_key`.
4. Запустите тесты через Maven:
   ```bash
   mvn clean test
   ```
5. Для генерации и просмотра Allure-отчета выполните:
   ```bash
   mvn allure:serve
   ```
## 🔄 Настройка Непрерывной Интеграции (CI/CD)

Так как генерация данных завязана на внешний Mistral API, агенту сборщика нужен доступ к интернету и валидный `MISTRAL_API_KEY` (см. секреты ниже). Если ключ недоступен, тесты не упадут — сработает локальный фолбэк-генератор, но данные будут менее разнообразными.

### 🔹 GitHub Actions (`.github/workflows/gitHubActions.yml`)
Создайте файл по указанному пути в корне проекта. Сценарий запустит тесты в режиме Chrome Headless и опубликует Allure-отчет на GitHub Pages.

```yaml
name: Qase Framework CI & Allure Report

on:
  push:
    branches: [ "master", "main" ]
  pull_request:
    branches: [ "master", "main" ]
  workflow_dispatch:
    inputs:
      test_suite:
        description: 'Выберите тест-сьют для запуска:'
        required: true
        default: 'tests'
        type: choice
        options:
          - 'api-smoke'
          - 'ui-smoke'
          - 'api-regression'
          - 'ui-regression'
          - 'tests'
          

jobs:
  run-tests:
    name: Execute Tests & Publish Report
    runs-on: ubuntu-latest

    env:
      QASE_TOKEN: ${{ secrets.QASE_API_TOKEN }}
      USER_EMAIL: ${{ secrets.QASE_USER_EMAIL }}
      USER_PASSWORD: ${{ secrets.QASE_USER_PASSWORD }}
      MISTRAL_API_KEY: ${{ secrets.MISTRAL_API_KEY }}

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Run tests
        run: |
          mvn clean test \
          -Dtoken=$QASE_TOKEN \
          -Duser=$USER_EMAIL \
          -Dpassword=$USER_PASSWORD \
          -Dmistral_api_key=$MISTRAL_API_KEY

      - name: Get Allure history
        uses: actions/checkout@v4
        if: always()
        continue-on-error: true
        with:
          ref: gh-pages
          path: gh-pages

      - name: Generate Allure Report
        uses: simple-elf/allure-report-action@master
        if: always()
        with:
          allure_results: target/allure-results
          allure_history: allure-history
          keep_reports: 20

      - name: Deploy Allure Report to GitHub Pages
        if: always()
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_branch: gh-pages
          publish_dir: allure-history
```

---