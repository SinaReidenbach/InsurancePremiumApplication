# Projektname: InsurancePremiumApplication
<br>

## Projektbeschreibung:
Das Ziel dieses Projekts ist die Entwicklung einer Softwarelösung zur Berechnung von Versicherungsprämien anhand bestimmter Eingabewerte und Berechnungslogiken. Die Anwendung wurde mit Java Spring Boot entwickelt und stellt eine API zur Verfügung, die es ermöglicht, Versicherungsprämien für unterschiedliche Versicherungstypen basierend auf verschiedenen Faktoren zu berechnen.
<br><br><br>

## Anforderungen an das Projekt:
Die Anwendung soll eine Versicherungsprämie basierend auf drei Faktoren berechnen:
- Der Kilometerleistungs-Faktor ist vorgegeben:
<div>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0km - &nbsp;&nbsp;5000km -> Faktor: 0.5<br>
&nbsp;&nbsp;5001km - 10000km -> Faktor: 1.0<br>
10001km - 20000km -> Faktor: 1.5<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; > 20001km -> Faktor: 2.0<br>
</div>

- Der Region-Faktor kann über die einzelnen Regionen festgelegt werden
- Der Fahrzeugtyp-Faktor kann frei gewählt werden

- Die Einträge sollen in einer Datenbank gespeichert werden
- Die Anwendung soll mindestens 2 Services enthalten
- Es soll ein Test-Framework verwendet werden zur Wahrung der Softwarequalität
- Es soll eine Dokumentation erstellt werden
- Optional kann eine Web-basierte Oberfläche erstellt werden
- eine Drittanbieter API soll bereitgestellt werden
- besonderer Fokus liegt auf Einfachheit, Testbarkeit und Wartbarkeit der Anwendung

[Link zur original Aufgabenstellung](README_Aufgabenstellung.md)
<br><br>

## Umsetzung:
Da in diesem Szenario keine permanente Produktionsdatenbank zur Verfügung stand und die Anwendung beim Aufgabensteller ohne zusätzliche Infrastruktur lauffähig sein sollte, wurde in meinem Projekt eine H2-Datenbank eingesetzt. Normalerweise hätte ich mich für eine MySQL-Datenbank entschieden, da diese weit verbreitet, stabil und gut skalierbar ist. Zur einfacheren Präsentation wird die H2-Datenbank bei jedem Programmstart neu aufgebaut, was über die DatabaseService-Klasse gesteuert wird. Der Grund für das Wiederaufbauen liegt darin, dass ich keinen Server bereitstellen kann, auf den der Auftraggeber zugreifen und auf dem die Datenbank gespeichert wird. Diese Lösung ermöglicht es, die Anwendung lokal auszuführen. Nach einer ersten Erstellung der Datenbank kann das Spring-Profil jedoch auf eine andere Konfiguration umgestellt werden, wenn eine dauerhafte Datenbank für den Produktionsbetrieb bereitgestellt wird.
<br><br>
[Link zur JavaDoc-Dokumentation](http://localhost:63342/InsurancePremiumApplication/src/main/resources/javadoc/index.html)
<br><br><br>

### Controller:
Meine Application besitzt 2 Controller. Einen um ein Frontend anzusteuern und die Usereingaben entgegen zu nehmen und einen für den Drittanbieterzugriff über eine REST-API.

Der FrontendController erwartet Usereingaben zu jährlicher Kilometerleistung, Postleitzahl der Zulassungsstelle und dem Fahrzeugtyp. Anhand dieser Eingaben wird dann im weiteren Code die Versicherungsprämie berechnet

Der ThirdPartyController stellt die Schnittstelle für Drittanbieter mit verschiedenen Möglichkeiten:
- Optionsaufrufe um zb die ID vom Fahrzeugtyp zu ermitteln, oder die Kilometerbereiche auszugeben (Infos zu allen drei 
  nötigen Parametern können hier per GET abgerufen werden)
- Ausgabe der Versicherungsprämie über einen POST Request 
- Eine SwaggerDokumentation wurde ebenfalls für diese Schnittstelle erstellt und ist unter folgendem Link zu erreichen:<br>
  <br>
  [Link zur Swagger-Dokumentation](http://localhost:8080/swagger-ui/index.html)
  <br><br><br>

### Models:
Die Anwendung besitzt verschiedene Entyties, hier im Code unter dem Package model gespeichert sind. Diese dienen zur Erstellung der Datenbanktabellen. Tabellen für die zuordnung der nötigen Parameter sind:

- Anno_Kilometers
  <div>(enthält die Kilometerbereiche mit den entsprechenden dazugehörigen Faktoren)</div>
- Postcode
  <div>(enthält alle Postleitzahlen)</div>
- Region
  <div>(enthält alle Regionen und die jeweiligen Faktoren)</div>
- Vehoicle 
  <div>(enthält alle Fahrzeugtypen und die jeweiligen Faktoren)</div>

Zusätzlich zu den nötigen Eingabeparameter gibt es noch die City Entity und die Statistics Entity. Während die City nur als Ergänzung der Informationen dient, wurde die Statistics erstellt, um die User Abfragen dort zu speichern. Nach jeder Abfrage werden die Usereingaben mit TimeStempel und IP-Adresse dort gespeichert. Geplant, aber noch nicht umgesetzt: jährlich soll die IP-Adresse wegen dem Datenschutz aus der Statistics Tabelle gelöscht und die Tabelle in eine Archivtabelle für das jeweilige Jahr übertragen werden, wobei die IP-Adressen eine Platzhalterzahl erhalten, um Wiederholungen zu erkennen.

[Link zur Datenbank-Struktur](Datenbankstruktur.md)
<br><br><br>

### Repositorys:
zu jedem Model existiert auch ein Repository, welches mit der Datenbank kommuniziert und über den restlichen Code angesprochen werden kann.
<br><br><br>

### Services:
Ich habe 5 Services in meinem Code, die alle separierte Aufgabenbereiche abdecken:
- DatabaseService
  <div style="margin-left: 30px;">
  Diese Klasse ist verantwortlich für das Einlesen und Speichern von Daten aus der Postcodes.csv in die Datenbank. Sie handhabt die Erstellung und Speicherung von Entitäten wie Anno_Kilometers, Vehicle, Region, City und Postcode.
  </div>
  <br>
- CalculateService
  <div style="margin-left: 30px;">
  Diese Klasse ist zuständig für die Berechnung der Versicherungsprämie. Sie berechnet Prämien basierend auf Fahrzeugtyp, Kilometerstand und Region. Sie speichert Statistikdaten und liefert die berechneten Prämien als Antwort zurück.</div>
  <br>
- EntityService
  <div style="margin-left: 30px;">
  Diese Klasse ist verantwortlich für die Steuerung von Entitäten und deren Interaktionen im Backend. Sie handhabt die Bereitstellung von Fahrzeugen, Regionen und Postleitzahlen sowie das Filtern und Sortieren.
  </div>
  <br>
- ErrorHandlingService
  <div style="margin-left: 30px;">
  Diese Klasse verantwortet das Fehlerhandling und das Logging. Sie handhabt Fehler durch Loggen von Nachrichten und Werfen von Exceptions.
  </div>
  <br>
- StatisticsService 
  <div style="margin-left: 30px;">
  Diese Klasse ist verantwortlich für die Speicherung von User Anfragen in den Statistiken. Sie handhabt die Speicherung von Statistiken in der Datenbank und prüft auf Duplikate und gültige IP-Adressen
  </div>
  <br><br>

### Sonstiges:
- Es existiert eine weitere Klasse für die Extraktion der IP-Adresse im Package Utils
- über die application-properties wurde der Basiswert für die Berechnungen definiert, damit er zentral Änderbar ist.
- Es existieren eine application-test.properties und eine application-prod.properties, um zwischen Test-Profil und Produktions-Profil wechseln zu können.
- Das Frontend wurde aus Datenschutzgründen mit einer Zustimmung zur Speicherung versehen, ohne die eine Berechnung nicht möglich ist.
- Ebenfalls werden im Frontend schon bestimmte Falscheingaben, wie zb zu niedrige, oder zu hohe km-Zahlen oder nicht existierende PLZ, sowie fehlende Eingaben abgefangen
<br><br><br>

### Teststrategie:
In meinem Projekt habe ich mit JUnit 5 gearbeitet und eine möglichst hohe Testabdeckung angestrebt. Ziel war es, Fehler, die durch Codeänderungen entstehen, frühzeitig zu erkennen und so die Stabilität des Systems sicherzustellen. Eine umfassende Testabdeckung erleichtert nicht nur die Weiterentwicklung des Codes, sondern verhindert auch unerwartete Nebeneffekte bei späteren Anpassungen.

Meine Teststrategie bestand vor allem darin, die Kernlogik durch Unit-Tests abzudecken, um einzelne Komponenten isoliert zu prüfen. Dadurch konnte ich sicherstellen, dass jede Methode unter verschiedenen Bedingungen korrekt funktioniert.

Auch wenn der Einsatz von Test-Driven Development (TDD) sinnvoll gewesen wäre, habe ich mich dagegen entschieden, da ich in diesem Bereich noch nicht so erfahren bin. Stattdessen lag mein Fokus auf einer hohen Testabdeckung, um eine vergleichbare Sicherheit in der Codequalität zu erreichen. Für zukünftige Projekte plane ich, mich intensiver mit TDD auseinanderzusetzen, um von den Vorteilen einer testgetriebenen Entwicklung stärker zu profitieren.
<br><br><br>

### Technologien und Abhängigkeiten:
- Java 21 
- Spring Boot 3.4.2
- H2 Database
- MySQL Connector 8.0.33
- OpenCSV Version 5.9
- springdoc-openapi 2.5.0
- Thymeleaf 
- JPA/Hibernate
- Maven
- Unit Test-Framework (JUnit 5)
- Mockito 5.14.2
  <br><br><br>

### Erweiterungsmöglichkeiten und zukünftige Verbesserungen
Obwohl das Projekt derzeit funktionsfähig und lokal lauffähig ist, gibt es einige Erweiterungen, die in zukünftigen Versionen sinnvoll wären:

- Anbindung einer persistenten Datenbank:<br>
  <div style="margin-left: 30px;">
  Für eine stabilere und skalierbarere Lösung wäre die Integration einer dauerhaften, externen Datenbank von Vorteil. In der aktuellen Version wird die H2-Datenbank verwendet, die bei jedem Start des Projekts zurückgesetzt wird. Eine Verbindung zu einer permanenten MySQL- oder PostgreSQL-Datenbank könnte eine dauerhafte Speicherung der Benutzerdaten und Berechnungen ermöglichen und so das System robuster machen.
  <br><br></div>
- Integration von Authentifizierung und Autorisierung:<br>
  <div style="margin-left: 30px;">
  Da das Projekt aktuell ohne Benutzerverwaltung auskommt, könnte in einer zukünftigen Version eine Authentifizierung und Autorisierung integriert werden. Dies könnte insbesondere dann notwendig werden, wenn das Projekt für eine breitere Zielgruppe oder in einem kommerziellen Kontext verwendet wird, um sicherzustellen, dass nur autorisierte Benutzer auf bestimmte Daten zugreifen oder Berechnungen durchführen können.
  <br><br></div>
- Anonymisierung und Archivierung der Statistiken:<br>
  <div style="margin-left: 30px;">
  Für eine datenschutzgerechte Speicherung der Nutzerstatistiken sollte eine Anonymisierung der gespeicherten IP-Adressen und anderen sensiblen Daten in Betracht gezogen werden. Ebenso könnte die Möglichkeit zur Archivierung von älteren Statistiken sinnvoll sein, um die Datenbankperformance zu verbessern und gesetzlichen Anforderungen gerecht zu werden.
  <br><br></div>
- Berechtigungsmanagement:<br>
  <div style="margin-left: 30px;">
  In einem erweiterten Szenario könnte es sinnvoll sein, ein flexibles Berechtigungsmanagement einzuführen. Dies würde es ermöglichen, den Zugriff auf bestimmte Funktionen oder Daten auf verschiedene Benutzergruppen zu beschränken, was insbesondere in großen Anwendungen mit mehreren Benutzern von Bedeutung wäre.
  <br><br></div>
Diese Erweiterungen würden die Anwendung nicht nur funktionaler, sondern auch sicherer und skalierbarer machen, was sie für den produktiven Einsatz noch besser geeignet machen würde.