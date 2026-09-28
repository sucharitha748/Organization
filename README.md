# Organization Service API

## Run
```
mvn spring-boot:run
```
App starts on `http://localhost:8080`

## Endpoints
- `POST /api/organizations` - create organization
- `GET /api/organizations/{organizationCode}` - get by code
- `GET /api/organizations` - list all
- `GET /api/organizations/code/generate?organizationName=` - preview auto-generated code
- `GET /api/organizations/code/check?organizationCode=` - validate + check availability of a manual code

## Organization Code modes (`codeMode`)
| Mode | Behaviour |
|------|-----------|
| `AUTO` | Code generated from name (`Acme Corp` -> `ACME-CORP`). If taken, suffix added (`ACME-CORP-2`, `-3`...). Never returns 409. |
| `MANUAL` | `organizationCode` required. Trimmed + uppercased, 3-30 chars, letters/digits/single hyphens. 400 if invalid, 409 if taken. |

If `codeMode` is omitted: `MANUAL` when `organizationCode` is sent, else `AUTO` (backward compatible).
Response includes `codeMode`.

## UI flow
1. Default mode `AUTO`; call `/code/generate` as the name is typed and show it in the disabled field.
2. On "edit manually": switch to `MANUAL`, enable field, call `/code/check` as the user types.
3. Submit with `codeMode` + `organizationCode` (MANUAL) or just `codeMode: AUTO`.

## Testing
Import `Organization-API.postman_collection.json` into Postman. Run requests in order.
