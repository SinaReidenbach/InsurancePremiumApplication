### Tabelle: ANNO_KILOMETERS
- **FACTOR** (DOUBLE PRECISION) –  Der Faktor, der von der Jahreskilometerleistung beeinflusst wird
- **MAX** (INTEGER) – maximaler KM-Wert für den entsprechenden Faktor
- **MIN** (INTEGER) – minimaler KM-Wert für den entsprechenden Faktor
- **ID** (BIGINT) –  (Primärschlüssel)

### Tabelle: CITY
- **ID** (BIGINT) –  (Primärschlüssel) 
- **REGION_ID** (BIGINT) –  (Fremdschlüssel aus Region Tabelle)
- **NAME** (CHARACTER VARYING) – Bezeichnung der Stadt

### Tabelle: POSTCODE
- **CITY_ID** (BIGINT) –  (Fremdschlüssel aus City Tabelle)
- **ID** (BIGINT) –  (Primärschlüssel)
- **REGION_ID** (BIGINT) –  (Fremdschlüssel aus Region Tabelle)
- **POSTCODE_VALUE** (CHARACTER VARYING) – Postleitzahl

### Tabelle: REGION
- **FACTOR** (DOUBLE PRECISION) –  Der Faktor, der von der Region beeinflusst wird
- **ID** (BIGINT) –  (Primärschlüssel)
- **NAME** (CHARACTER VARYING) – Bezeichnung der Region

### Tabelle: STATISTICS
- **ANNOKILOMETERS** (INTEGER) – Jahreskilometerleistung
- **PREMIUM** (DOUBLE PRECISION) – Versicherungsprämienbetrag
- **DATE_TIME** (TIMESTAMP) – aktueller Datumsstempel
- **ID** (BIGINT) –  (Primärschlüssel)
- **IP_ADDRESS** (CHARACTER VARYING) – IP-Adresse des Users
- **POSTCODE** (CHARACTER VARYING) – Postleitzahl
- **VEHICLE** (CHARACTER VARYING) – Bezeichnung des Fahrzeugtyps

### Tabelle: VEHICLE
- **FACTOR** (DOUBLE PRECISION) –  Der Faktor, der vom Fahrzeugtyp beeinflusst wird
- **ID** (BIGINT) –  (Primärschlüssel)
- **NAME** (CHARACTER VARYING) – Bezeichnung des Fahrzeugtyps

