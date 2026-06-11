# TheKnife - Laboratorio Interdisciplinare B

## Autori

- Hamdar Youssef – Matricola 753832 – Sede Como
- Dellatorre Federico – Matricola 755856 – Sede Como

## Descrizione

TheKnife è una piattaforma che consente di ricercare ristoranti e gestire recensioni, simulando alcune funzionalità della piattaforma TheFork.

L'applicazione è stata sviluppata nell'ambito del corso Laboratorio Interdisciplinare B dell'Università degli Studi dell'Insubria.

## Tecnologie Utilizzate

- Java 21
- PostgreSQL 18
- JDBC
- Maven
- Swing
- Socket TCP/IP
- GitHub

## Architettura

Il progetto è composto da:

### ServerTK

Gestisce:

- Connessioni dei client
- Accesso al database PostgreSQL
- Registrazione utenti
- Login utenti
- Gestione ristoranti
- Gestione recensioni
- Gestione preferiti

### ClientTK

Permette agli utenti di:

- Registrarsi
- Effettuare il login
- Cercare ristoranti
- Gestire preferiti
- Inserire recensioni
- Modificare recensioni
- Eliminare recensioni

### GUI

Interfaccia grafica realizzata tramite Java Swing.

## Database

Database PostgreSQL:

dbTK

Tabelle principali:

- utenti
- ristoranti
- recensioni
- preferiti

## Funzionalità Implementate

### Utenti

- Registrazione
- Login
- Ricerca ristoranti
- Visualizzazione ristoranti
- Gestione preferiti
- Inserimento recensioni
- Modifica recensioni
- Eliminazione recensioni

### Ristoratori

- Inserimento ristoranti
- Visualizzazione recensioni
- Risposta alle recensioni
- Visualizzazione riepilogo recensioni

## Avvio del Server

Eseguire:

```bash
java -jar serverTK.jar
```

## Avvio del Client

Eseguire:

```bash
java -jar clientTK.jar
```

## Repository GitHub

https://github.com/YoussefHamdar/TheKnife-LabB

## Note

Le password vengono memorizzate in forma cifrata.

Il progetto utilizza PostgreSQL come DBMS e JDBC per l'accesso ai dati.
