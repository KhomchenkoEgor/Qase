# Qase.io UI & API Test Automation Framework

Проект представляет собой модульный фреймворк для автоматизации тестирования UI и API функционала системы управления тест-кейсами **Qase.io**. 

Фреймворк построен с использованием гибридного подхода (UI + API), изоляции тестовых данных и динамической генерации контекста с помощью локальной LLM.

## 🛠 Технологический стек

* **Язык программирования:** Java 17
* **Сборщик проектов:** Maven (pom.xml)
* **Тестовый движок:** TestNG (Параллельный запуск, Группировка, Dataproviders)
* **Автоматизация UI:** Selenide (Обертка над Selenium WebDriver)
* **Тестирование API:** Rest Assured + JSON Schema Validator
* **Генерация данных:** Локальная LLM (Qwen2.5-Coder через Ollama API)
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
* Вместо стандартных библиотек генерации фейковых данных (Faker), в проекте реализована интеграция с локально запущенной большой языковой моделью (**Qwen2.5-Coder через Ollama**).
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
src
└── test
    ├── java
    │   ├── adapters           # Слой API-адаптеров (RestAssured обертки для сущностей Qase)
    │   │   ├── BaseAdapter.java
    │   │   ├── CaseAdapter.java
    │   │   ├── PlanAdapter.java
    │   │   ├── ProjectAdapter.java
    │   │   └── SuiteAdapter.java
    │   ├── dict               # Словари, константы и статические тексты приложения
    │   │   └── Elements.java
    │   ├── listeners          # Слушатели TestNG (логирование, скриншоты Allure на failure)
    │   │   ├── RetryListener.java
    │   │   └── TestListener.java
    │   ├── models             # DTO / POJO модели для сериализации/десериализации Jackson/Gson
    │   │   ├── cases          # Модели запросов и ответов для Тест-кейсов (CaseRq, CaseRs, Step)
    │   │   ├── plan           # Модели для Тест-планов
    │   │   ├── project        # Модели для Проектов
    │   │   └── suite          # Модели для Тест-сьютов
    │   ├── pages              # Слой UI-страниц (Паттерн Page Object Model на Selenide)
    │   │   ├── LoginPage.java
    │   │   ├── ProjectPage.java
    │   │   ├── ProjectsPage.java
    │   │   └── TestPlanPage.java
    │   ├── tests              # Слой тест-сценариев
    │   │   ├── api            # Изолированные API-тесты (компонентные и CRUD)
    │   │   │   ├── BaseApiTest.java
    │   │   │   ├── CaseApiTest.java
    │   │   │   ├── ProjectApiTest.java
    │   │   │   └── SuiteApiTest.java
    │   │   └── ui             # UI-тесты (включая гибридные E2E сценарии)
    │   │       ├── BaseTest.java
    │   │       ├── LoginTest.java
    │   │       ├── ProjectTest.java
    │   │       ├── SuiteUiTest.java
    │   │       └── TestPlanUiTest.java
    │   └── utils              # Утилитарные классы (чтение конфигов, AI-генератор, перезапуски)
    │       ├── AllureUtils.java
    │       ├── PropertyReader.java
    │       ├── QwenDataGenerator.java  # Модуль интеграции с Ollama API (Qwen LLM)
    │       └── Retry.java
    └── resources
        ├── schemas            # JSON-схемы для валидации контрактов API (.json)
        ├── config.properties  # Файл конфигурации (в репозитории хранится шаблон template)
        └── testng.xml         # Файл конфигурации последовательного запуска тестов TestNG
```
---

## 🚀 Локальный запуск проекта

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com
   ```
2. Для работы генерации данных через LLM убедитесь, что у вас запущен Ollama с моделью `qwen2.5-coder`.
3. Создайте файл `src/test/resources/config.properties` и укажите ваши конфигурационные данные (токен API, базовые URL).
4. Запустите тесты через Maven:
   ```bash
   mvn clean test
   ```
5. Для генерации и просмотра Allure-отчета выполните:
   ```bash
   mvn allure:serve
   ```
## 🔄 Настройка Непрерывной Интеграции (CI/CD)

Так как тесты завязаны на LLM-модель, агенты сборщиков должны иметь доступ к Ollama. Ниже представлены готовые конфигурации для автоматического развертывания окружения.

### 🔹 GitHub Actions (`.github/workflows/maven.yml`)
Создайте файл по указанному пути в корне проекта. Сценарий автоматически поднимет Docker-контейнер Ollama на стороне GitHub-раннера, скачает модель Qwen, запустит тесты в режиме Chrome Headless и опубликует Allure-отчет на GitHub Pages.

```yaml
name: Qase.io AI Regression CI

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]

jobs:
  build:
    runs-on: ubuntu-latest

    services:
      # Поднимаем локальную Ollama в Docker-контейнере прямо на раннере GitHub
      ollama:
        image: ollama/ollama:latest
        ports:
          - 11434:11434

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Pull Qwen Model inside Container
      run: |
        curl http://localhost:11434/api/pull -d '{"name": "qwen2.5-coder:7b"}'

    - name: Run Regression Tests (Headless)
      run: mvn clean test -Dselenide.headless=true
      env:
        # Передаем секреты из настроек репозитория GitHub
        QASE_TOKEN: ${{ secrets.QASE_TOKEN }}
        QASE_USER: ${{ secrets.QASE_USER }}
        QASE_PASSWORD: ${{ secrets.QASE_PASSWORD }}

    - name: Get Allure History
      final: always()
      uses: actions/checkout@v4
      if: always()
      with:
        ref: gh-pages
        path: gh-pages

    - name: Generate Allure Report
      uses: simple-elf/allure-report-action@master
      if: always()
      with:
        allure_results: target/allure-results
        allure_history: allure-history

    - name: Deploy Allure to GitHub Pages
      if: always()
      uses: peaceiris/actions-gh-pages@v3
      with:
        github_token: ${{ secrets.GITHUB_TOKEN }}
        publish_dir: allure-history
```

---

 🛡️ Best Practices & Стабилизация паттернов

* **API-Seeding в UI-тестах:** Для тяжелых тестов (например, сборка тест-планов) создание проекта и наполнение его тест-кейсами происходит мгновенно через быстрые API-адаптеры в `@BeforeMethod`, а в браузере проверяются только нативные клики.
* **Изоляция модальных окон (Form Scoping):** Поля ввода и кнопки подтверждения ищутся строго внутри активного CSS-контейнера формы (`form.NWLa0T #title`), что защищает Selenide от взаимодействия со старыми скрытыми React-элементами в DOM.
* **Пуленепробиваемые локаторы:** Работа с выпадающими списками и иерархией папок React Aria Components переведена на оси XPath (`ancestor::button`) и поиск по константным атрибутам доступности (`button[contains(@aria-label, 'suite name actions')]`, `[data-key='create_suite']`), что полностью нивелирует падения из-за динамических ID и ховер-эффектов.
* **Атомарная очистка данных (Single Responsibility Cleanup):** UI-тесты больше не удаляют за собой проекты через клики по интерфейсу. Полное каскадное удаление перенесено на API-адаптер в `@AfterMethod`, который всегда возвращает честный `HTTP 200` и защищает бесплатные лимиты аккаунта от забивания мусором.