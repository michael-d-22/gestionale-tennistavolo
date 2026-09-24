# gestionale-tennistavolo
Il progetto è rivolto alle società di tennistavolo per la gestione dei propri atleti, delle sessioni di allenamento e turni.

## Stato del progetto
In sviluppo. Al momento l'app si avvia e si collega al DB.

## Stack (previsto)
Java 25, Spring Boot, Spring Data JPA, MySQL 8.4, API REST, frontend HTML/CSS/JS

## Come avviarlo in locale
1. Clonare il repository ed entrare nella cartella
2. Installare JDK 25 e MySQL 8.4 (Maven non serve: il progetto include il Maven Wrapper `mvnw`)
3. Creare il database `gestionale_tennistavolo` e un utente dedicato con permessi solo su quel database
4. Creare `src/main/resources/application-local.properties` con:
   `spring.datasource.username=` e `spring.datasource.password=` (valori del proprio utente MySQL).
   Il file è escluso da Git e non va mai committato.
5. Avviare l'applicazione:
    - Windows: `.\mvnw spring-boot:run`
    - macOS/Linux: `./mvnw spring-boot:run`

Le tabelle vengono create automaticamente da Hibernate al primo avvio. L'app risponde su `http://localhost:8080`.

## Perché l'ho fatto
Ho fatto l'allenatore per più di un anno. Gestire gli allenamenti con fogli Excel o addirittura su carta richiedeva molto tempo ed era facile sbagliare. Per questo ho deciso di creare questo gestionale: per semplificare la vita agli allenatori, come lo sono stato io.