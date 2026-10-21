# StockFlow API

API REST para la gestión de inventarios, construida con Java 21, Spring Boot y SQL Server.
Esta fase incluye la gestión de categorías y productos.

## Requisitos

- JDK 21
- Maven 3.6.3 o superior
- Docker Desktop con Docker Compose (para la base de datos local)

## Configuración local

1. Crea el archivo de variables de entorno a partir de la plantilla:

   ```powershell
   Copy-Item .env.example .env
   ```

   Edita `.env` y define `DB_PASSWORD` con una contraseña local de al menos 8 caracteres que combine mayúsculas, minúsculas, números y símbolos. La plantilla deja la contraseña vacía; `.env` no debe subirse a Git.

2. Inicia SQL Server y crea la base de datos `StockFlowDb`:

   ```powershell
   docker compose up -d sqlserver
   docker compose run --rm database-setup
   ```

   El servicio `database-setup` espera a que SQL Server esté disponible y crea la base de datos si aún no existe. Comprueba el estado de SQL Server con `docker compose ps`.

3. Carga las variables del archivo `.env` en la sesión PowerShell y ejecuta la aplicación:

   ```powershell
   Get-Content .env | ForEach-Object {
       if ($_ -match '^([^#][^=]+)=(.*)$') {
           [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
       }
   }
   mvn spring-boot:run
   ```

   `DB_URL` usa por defecto `localhost:1433` y la base `StockFlowDb`. También puedes configurar `DB_URL` y `SERVER_PORT` desde el entorno. No se incluyen secretos reales en el repositorio.

## Base de datos y migraciones

Flyway aplica `src/main/resources/db/migration/V1__create_categories_and_products.sql` automáticamente al iniciar la API. La migración crea las tablas y restricciones de unicidad de categorías y SKU; no elimina tablas ni datos. Hibernate usa `ddl-auto: validate` para comprobar que el esquema coincide con las entidades, sin crear ni modificar tablas automáticamente.

Antes de aplicar la migración a una base existente que ya contenga tablas `categories` o `products`, revisa y respalda el esquema: Flyway no puede adoptar esas tablas automáticamente.

## Endpoints

La documentación interactiva OpenAPI/Swagger UI está disponible en <http://localhost:8080/swagger-ui.html>; el esquema JSON está en <http://localhost:8080/v3/api-docs>.

| Método | Endpoint | Resultado |
|---|---|---|
| `POST` | `/api/categories` | `201 Created` |
| `GET` | `/api/categories` | `200 OK` |
| `GET` | `/api/categories/{id}` | `200 OK` o `404 Not Found` |
| `POST` | `/api/products` | `201 Created` |
| `GET` | `/api/products?page=0&size=20&sort=name,asc` | `200 OK` con paginación |
| `GET` | `/api/products/{id}` | `200 OK` o `404 Not Found` |
| `PUT` | `/api/products/{id}` | `200 OK` |
| `PATCH` | `/api/products/{id}/deactivate` | `204 No Content` |

Los errores de validación devuelven `400`, recursos inexistentes `404` y nombres/SKU duplicados `409`. Las respuestas de productos contienen los valores `stock` y `active`, pero las solicitudes de creación/actualización rechazan campos desconocidos, incluidos `stock` y `active`. Cada producto comienza con stock `0` y activo; este CRUD no modifica stock.

### Crear categoría

Request:

```json
{
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

Response (`201 Created`):

```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

### Crear producto

Request:

```json
{
  "name": "Wireless Mouse",
  "sku": "MOU-1001",
  "description": "Ergonomic wireless mouse",
  "price": 29.99,
  "minimumStock": 5,
  "categoryId": 1
}
```

Response (`201 Created`):

```json
{
  "id": 1,
  "name": "Wireless Mouse",
  "sku": "MOU-1001",
  "description": "Ergonomic wireless mouse",
  "price": 29.99,
  "stock": 0,
  "minimumStock": 5,
  "active": true,
  "categoryId": 1,
  "categoryName": "Electronics"
}
```

### Actualizar y desactivar un producto

`PUT /api/products/1` acepta los mismos campos editables que el request de creación (`name`, `sku`, `description`, `price`, `minimumStock` y `categoryId`); no acepta `stock` ni `active`. Para desactivar:

```powershell
Invoke-RestMethod -Method Patch http://localhost:8080/api/products/1/deactivate
```

Para crear recursos y listar productos desde PowerShell, usa:

```powershell
$category = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/categories `
    -ContentType 'application/json' -Body '{"name":"Electronics","description":"Devices"}'

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/products `
    -ContentType 'application/json' `
    -Body (@{ name = 'Wireless Mouse'; sku = 'MOU-1001'; price = 29.99; minimumStock = 5; categoryId = $category.id } | ConvertTo-Json)

Invoke-RestMethod 'http://localhost:8080/api/products?page=0&size=20'
```

## Comprobar el health check

Con la aplicación en ejecución, abre <http://localhost:8080/api/health> o ejecuta:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

La respuesta JSON esperada es:

```json
{
  "status": "UP",
  "application": "StockFlow API"
}
```

## Compilar y probar

```powershell
mvn test
```

La suite contiene pruebas del health check, servicios de categorías/productos y validación de los datos del producto.
