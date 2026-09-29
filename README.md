# gestionale-tennistavolo
Gestionale web per società di tennistavolo: anagrafica di atleti e allenatori, sessioni di allenamento (di gruppo e individuali), prenotazioni, registro presenze e report mensili.

## Perché l'ho fatto
Ho fatto l'allenatore per più di un anno. Gestire gli allenamenti con fogli Excel o addirittura su carta richiedeva molto tempo ed era facile sbagliare. Per questo ho deciso di creare questo gestionale: per semplificare la vita agli allenatori, come lo sono stato io.

## Funzionalità
- **Atleti e allenatori**: inserimento ed elenco. Ogni atleta appartiene a una categoria (AGONISTI, GIOVANILE, AMATORI).
- **Sessioni**: di gruppo (riservate a una categoria, con capienza) o individuali (capienza sempre 1), con allenatore facoltativo.
- **Prenotazioni**: un atleta si prenota a una sessione e può annullare fino all'inizio.
- **Presenze**: l'allenatore segna presente o assente.
- **Report mensili**: presenze per atleta, totale allenamenti, presenze medie al giorno e per turno, percentuale di presenza del singolo atleta.

### Regole di business
Le regole sono applicate nel service layer e, se violate, restituiscono `409 Conflict` con un messaggio:
- non ci si può prenotare a una sessione piena, già iniziata o di un'altra categoria;
- niente doppie prenotazioni: chi annulla e poi riprenota riattiva la stessa prenotazione (vincolo UNIQUE atleta-sessione);
- una prenotazione individuale nasce già PRESENTE (l'allenatore segna solo l'eventuale assenza); una di gruppo nasce PRENOTATA e le presenze si registrano solo dopo l'inizio della sessione;
- si può annullare solo prima dell'inizio della sessione.

<!-- TODO: aggiungere 2-3 screenshot (es. pagina prenotazioni con un messaggio di errore, pagina report) -->

## Stack
Java 25 · Spring Boot 4 (Web MVC, Data JPA, Validation) · Hibernate · MySQL 8.4 · JUnit 6 e Mockito · HTML, CSS e JavaScript (fetch), senza framework frontend

## Architettura
L'applicazione è organizzata a strati: ogni strato conosce solo quello sotto.

```
Browser (HTML + JS fetch)
   │  JSON
   ▼
controller   → riceve la richiesta HTTP, valida il DTO, restituisce il DTO di risposta
   ▼
service      → regole di business e transazioni
   ▼
repository   → accesso ai dati con Spring Data JPA
   ▼
MySQL
```

| Package | Contenuto |
|---|---|
| `entity` | Entità JPA: `Atleta`, `Allenatore`, `Sessione`, `Prenotazione` ed enum |
| `repository` | Interfacce `JpaRepository`, query derivate dal nome del metodo e query `@Query` (JPQL) per i report |
| `service` | Regole di business (`PrenotazioneService`, `SessioneService`, …) e `ReportService` |
| `controller` | Endpoint REST |
| `dto` | Record Java per richieste e risposte: le entità non vengono mai esposte direttamente |
| `exception` | Eccezioni di dominio e `GestoreErrori` (`@RestControllerAdvice`) che le traduce in 400/404/409 |

Alcune scelte:
- **DTO come `record`**: il client può inviare solo i campi previsti (ad esempio non può impostare lo stato di una prenotazione) e il JSON resta indipendente dallo schema del database.
- **Gestione errori centralizzata**: ogni errore arriva al client con lo stesso formato `{ "status": 409, "messaggio": "La sessione è piena" }`.
- **`Clock` iniettato**: le regole che dipendono dall'ora ("sessione già iniziata") sono testabili fissando l'orologio.

## API REST
| Metodo | URL | Descrizione |
|---|---|---|
| GET / POST | `/api/atleti` | Elenco / nuovo atleta |
| GET | `/api/atleti/{id}` | Dettaglio atleta |
| GET / POST | `/api/allenatori` | Elenco / nuovo allenatore |
| GET | `/api/allenatori/{id}` | Dettaglio allenatore |
| GET / POST | `/api/sessioni` | Elenco / nuova sessione |
| GET | `/api/sessioni/{id}` | Dettaglio sessione |
| GET | `/api/sessioni/{id}/prenotazioni` | Prenotazioni di una sessione |
| POST | `/api/prenotazioni` | Nuova prenotazione `{ "atletaId": 1, "sessioneId": 3 }` |
| POST | `/api/prenotazioni/{id}/annullamento` | Annulla la prenotazione |
| POST | `/api/prenotazioni/{id}/presenza` | Segna presente |
| POST | `/api/prenotazioni/{id}/assenza` | Segna assente |
| GET | `/api/report/presenze-atleti?mese=2026-10` | Presenze per atleta nel mese |
| GET | `/api/report/riepilogo?mese=2026-10` | Totale allenamenti, presenze medie al giorno e per turno |
| GET | `/api/report/percentuale-atleta?atletaId=1&mese=2026-10` | Percentuale di presenza dell'atleta sulle sessioni della sua categoria |

Codici di risposta: `201` creazione, `400` dati non validi, `404` risorsa inesistente, `409` regola di business violata.

## Test
- **Unit test** dei service con JUnit e Mockito: repository simulati e orologio fisso, per verificare ogni regola di prenotazione, annullamento, presenza e creazione sessione.
- **Test di integrazione** del controller delle prenotazioni con `@WebMvcTest` e `MockMvc`: codici di stato, validazione e formato degli errori.

```
.\mvnw test
```

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

Le tabelle vengono create automaticamente da Hibernate al primo avvio. L'interfaccia web è su `http://localhost:8080`, le API sotto `http://localhost:8080/api`.

## Limiti noti e possibili sviluppi
- **Nessuna autenticazione**: chiunque acceda può segnare le presenze. Il passo successivo sarebbe Spring Security con i ruoli allenatore e atleta.
- **Concorrenza**: due prenotazioni simultanee sull'ultimo posto potrebbero superare la capienza (il controllo "conta e poi inserisci" non è atomico). Per il volume di una società il rischio è accettabile; la soluzione sarebbe un lock sulla sessione.
- **Solo inserimento**: non sono ancora previste modifica ed eliminazione di atleti e sessioni.
- **Report**: la percentuale di presenza usa come denominatore tutte le sessioni della categoria nel mese (non tiene conto delle assenze giustificate).
