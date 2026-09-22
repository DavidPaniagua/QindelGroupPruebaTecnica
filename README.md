# 📈 Pricing Service — Qindel / Inditex

API REST para la consulta del precio aplicable a un producto de una cadena en una fecha determinada, construida con **Spring Boot 4** y **Java 25** siguiendo **Arquitectura Hexagonal** (Ports & Adapters), enfoque **API-First** con OpenAPI y principios **SOLID**.

---

## 🧠 Decisiones técnicas

1. **API-First con OpenAPI Generator**: el contrato (`src/main/resources/openapi/`) es la fuente de verdad. La interfaz del controller y los DTOs se generan en build (`interfaceOnly`), garantizando que implementación y documentación no diverjan.
2. **Value Objects auto-validados**: `BrandId` y `ProductId` son inválidos de construir con valores nulos o no positivos. La validación vive en el dominio, no dispersa en la capa web, se podria añadir cualquier tipo de validación que requiera negocio.
3. **Inmutabilidad**: `Price` es un `record` con validación de invariantes en el constructor compacto (precio no negativo, moneda no vacía, `startDate <= endDate`). Uso extensivo de `final`.
4. **Patrón Builder (Lombok)** en la entidad JPA para evitar asignaciones accidentales entre campos del mismo tipo (`brandId` / `productId`).
5. **Referencia por ID en lugar de `@ManyToOne`**: evita acoplamiento físico entre agregados, el problema de *N+1 selects* y `LazyInitializationException`.
6. **Extracción eficiente**: la selección del precio se resuelve **íntegramente en base de datos** con una única query (`ORDER BY priority DESC` + límite de 1 resultado vía `Pageable`, que Hibernate traduce a `LIMIT 1`). Nunca se cargan todas las tarifas para filtrar en memoria. Al devolver `List` (y no `Page`) no se ejecuta la query de `COUNT` adicional.
7. **Criterio de desempate documentado**: el enunciado define la desambiguación por `PRIORITY`, pero no qué ocurre si dos tarifas empatan también en prioridad. Para garantizar un **resultado único y determinista** (requisito explícito), se aplica un segundo criterio: `price DESC`. Es una decisión propia ante la ausencia de especificación, aislada en la query y trivial de cambiar si negocio define otra regla.
8. **Índice compuesto** `(BRAND_ID, PRODUCT_ID, START_DATE, END_DATE, PRIORITY)` alineado con la query principal: las igualdades sobre `BRAND_ID` y `PRODUCT_ID` acotan el escaneo y el rango de fechas se resuelve sobre el propio índice.
9. **Esquema controlado por scripts** (`ddl-auto: none` + `schema.sql`/`data.sql`): el DDL es explícito y reproducible; los datos del enunciado se cargan al arrancar, como exige la prueba.
10. **Caché acotada (Caffeine) con TTL centralizado**: el caso de uso cachea resultados con `maximumSize=10.000` y una expiración configurable vía `pricing.cache.ttl-minutes` (`PricingCacheProperties`, `@ConfigurationProperties`) — nunca crece sin límite ni sirve datos obsoletos indefinidamente. La cabecera `Cache-Control: max-age` del controller lee la **misma propiedad**, garantizando que la expiración de la caché de servidor y el tiempo de cacheo permitido a clientes/intermediarios estén siempre sincronizados por diseño, no por coincidencia de valores hardcodeados en dos sitios.
11. **Manejo de errores centralizado** (`@ControllerAdvice`) con un modelo de error único definido en el contrato OpenAPI. Las violaciones de invariantes del dominio se tratan como error de servidor (500), no como error del cliente.
12. **`open-in-view` desactivado**: al no existir relaciones `@ManyToOne`/`@OneToMany` en el modelo (ver punto 5), no hay riesgo de `LazyInitializationException` fuera de transacción. Desactivarlo libera la conexión JDBC en cuanto termina la transacción del caso de uso, en vez de retenerla hasta el final de la serialización HTTP.

## 🌐 API

### `GET /prices`

| Parámetro | Tipo | Descripción |
|---|---|---|
| `applicationDate` | `date-time` (ISO-8601 **con offset**) | Fecha de aplicación, p. ej. `2020-06-14T16:00:00Z` |
| `productId` | `integer (int64)` | Identificador del producto |
| `brandId` | `integer (int64)` | Identificador de la cadena |

> ⚠️ **Formato de fecha**: la API trabaja con `OffsetDateTime`, por lo que la fecha **debe incluir offset** (`Z` para UTC, o `+02:00`, etc.). Una fecha sin offset (`2020-06-14T16:00:00`) devuelve `400 Bad Request`. Esta decisión hace el servicio explícito respecto a zonas horarias, relevante en un negocio multi-país.

**Respuesta 200:**

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00Z",
  "endDate": "2020-06-14T18:30:00Z",
  "price": 25.45,
  "currency": "EUR"
}
```

**Errores** (todos con el mismo modelo `ErrorResponse`: `timestamp`, `status`, `error`, `message`, `path`):

| Código | Cuándo                                      |
|---|---------------------------------------------|
| `400` | Request parameters are invalid or missing.  |
| `404` | The requested resource was not found.       |
| `500` | Unexpected server error.                    |

El contrato completo está en `src/main/resources/openapi/openapi.yaml` (respuestas 200/400/404/500 documentadas).

## 🚀 Ejecución

### Opción A: Docker 🐳 (recomendada)

No requiere Java ni Maven en la máquina (build multi-stage, ejecución como usuario no-root):

```bash
docker-compose up --build
```

### Opción B: Local

Requisito: **Java 25**. No hace falta Maven instalado — el proyecto incluye Maven Wrapper:

```bash
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

En ambos casos la API queda disponible en `http://localhost:8080/prices`.

**Consola H2** (solo para inspección en desarrollo): `http://localhost:8080/h2-console`
* JDBC URL: `jdbc:h2:mem:pricingdb` · Usuario: `sa` · Sin contraseña

## 🧪 Testing

```bash
./mvnw test
```

Estrategia en tres niveles:

* **Tests unitarios** de caso de uso, adaptador de persistencia y mappers (Mockito): lógica y validaciones aisladas del framework, incluyendo la construcción del `Pageable` y el `Optional` de retorno en `PricePortAdapter`.
* **Test de slice JPA** (`@DataJpaTest`) sobre la query del repositorio, verificando la resolución de prioridad contra la base real.
* **Tests de integración end-to-end** (`@SpringBootTest` + MockMvc) que cubren los **5 casos del enunciado** de forma parametrizada — verificando tarifa, fechas de aplicación, precio y moneda — más los casos de error (404, 400 por id inválido, parámetro ausente y fecha malformada).

### Los 5 casos del enunciado

| Test | Fecha de aplicación | Producto | Cadena | Resultado esperado |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `2020-06-14T10:00:00Z` | 35455 | 1 (ZARA) | Tarifa 1 — 35.50 EUR |
| 2 | `2020-06-14T16:00:00Z` | 35455 | 1 (ZARA) | Tarifa 2 — 25.45 EUR |
| 3 | `2020-06-14T21:00:00Z` | 35455 | 1 (ZARA) | Tarifa 1 — 35.50 EUR |
| 4 | `2020-06-15T10:00:00Z` | 35455 | 1 (ZARA) | Tarifa 3 — 30.50 EUR |
| 5 | `2020-06-16T21:00:00Z` | 35455 | 1 (ZARA) | Tarifa 4 — 38.95 EUR |

**URL de ejemplo (Test 2):**

```
http://localhost:8080/prices?productId=35455&brandId=1&applicationDate=2020-06-14T16:00:00Z
```

### Pruebas funcionales manuales (Bruno)

Se incluye una colección de [Bruno](https://www.usebruno.com/) con las peticiones preconfiguradas:

* Ruta: `src/test/resources/functional/bruno`
* Importa la carpeta en Bruno y lanza las peticiones contra la aplicación en ejecución.

## 📂 Organización del repositorio

* `src/main/resources/openapi/` — contrato OpenAPI (fuente de verdad de la API).
* `src/main/resources/h2/` — scripts `schema.sql` y `data.sql` con los datos del enunciado.
* `src/test/resources/functional/bruno/` — colección de pruebas manuales.
* `docker/` — `Dockerfile` multi-stage optimizado.

---
Desarrollado por **David Paniagua**