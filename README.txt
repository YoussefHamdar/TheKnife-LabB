TheKnife - Laboratorio Interdisciplinare B

Autori:
- Hamdar Youssef - Matricola 753832 - Sede Como
- Dellatorre Federico - Matricola 755856 - Sede Como

Repository: https://github.com/YoussefHamdar/TheKnife-LabB

Requisiti:
- Java 21 o superiore
- PostgreSQL
- Maven

Compilazione:
mvn clean package

Esecuzione server:
java -jar bin/serverTK.jar

Esecuzione client:
java -jar bin/clientTK.jar

Struttura repository richiesta:
- src/: codice sorgente
- bin/: jar client e server
- doc/: manuali, UML, ER e JavaDoc
- lib/: eventuali librerie esterne
- pom.xml: configurazione Maven
- autori.txt: autori e repository

Note:
Il progetto usa PostgreSQL e JDBC. Le password sono salvate in forma cifrata.
