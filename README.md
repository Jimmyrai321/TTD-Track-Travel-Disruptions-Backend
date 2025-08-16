<img width="820" height="312" alt="5" src="https://github.com/user-attachments/assets/d76c9e44-8c1b-4d9b-908d-07c1ce307e33" />

## Project Brief
The TTD app requires a robust RESTful API to store train journeys and track disruptions. This API allows for connection to a PostgreSQL database to store, update, delete train journey details and accessing the [Rail Data Marketplace's Live Fastest Departure Board](https://raildata.org.uk/dataProduct/P-d25efdeb-5389-41c5-83ac-2c0a7f41b6f2/overview) for live national rail information on specific trains depending on the day of travel and departure time. Utilising Springboot to create RESTful endpoints following a MVC architecture.

## Technology
- Spring Boot
- PostgreSQL
- Docker
- TDD/Mockito
- JPA & Hibernate
- MVC architecture

## API

### Accessing the API

The API can be accessed at base URL: `http://localhost:8080/api/v1/journey`

### Endpoints

### <img width="34" height="15" alt="GET" src="https://github.com/user-attachments/assets/348be0ea-8445-49bd-9fd2-b8ee7e81d9ee" /> `/journey`

Returns a list of all journeys in database.

Response:
```json
[
    {
        "id": 1602,
        "notificationsEnabled": true,
        "originCRS": "BHM",
        "destinationCRS": "LEI",
        "userId": 1,
        "days": [
            "SUNDAY",
            "SATURDAY"
        ],
        "departureTime": "15:45:00",
        "journeyLegs": [
            {
                "id": 1552,
                "origin": "Birmingham New Street",
                "originCRS": "BHM",
                "destination": "Leicester",
                "destinationCRS": "LEI",
                "legOrder": 0,
                "transportProvider": null
            }
        ]
    },
    {
        "id": 1752,
        "notificationsEnabled": false,
        "originCRS": "BHM",
        "destinationCRS": "EUS",
        "userId": 1,
        "days": [
            "FRIDAY"
        ],
        "departureTime": "21:45:00",
        "journeyLegs": [
            {
                "id": 1702,
                "origin": "Birmingham New Street",
                "originCRS": "BHM",
                "destination": "London Euston",
                "destinationCRS": "EUS",
                "legOrder": 0,
                "transportProvider": null
            }
        ]
    }
...etc
```

### <img width="34" height="15" alt="GET" src="https://github.com/user-attachments/assets/348be0ea-8445-49bd-9fd2-b8ee7e81d9ee" /> `/journey/:userid`

Returns journeys with given userID as path variable e.g `http://localhost:8080/api/v1/journey/1`. If the `days` does not include the current day or the `departureTime` is not within 2hrs of current time then `railDataDTO` will return null. Invalid userID will throw 404 and error message.

Response:
```json
[
    {
        "journeyDTO": {
            "id": 1602,
            "userId": 1,
            "originCRS": "BHM",
            "destinationCRS": "LEI",
            "departureTime": "15:45:00",
            "days": [
                "SUNDAY",
                "SATURDAY"
            ],
            "notificationsEnabled": true
        },
        "railDataDTO": null
    },
    {
        "journeyDTO": {
            "id": 1752,
            "userId": 1,
            "originCRS": "BHM",
            "destinationCRS": "EUS",
            "departureTime": "21:45:00",
            "days": [
                "FRIDAY"
            ],
            "notificationsEnabled": false
        },
        "railDataDTO": {
            "generatedAt": "2025-08-15T21:33:58.6704733+01:00",
            "departureStationCrs": "BHM",
            "departureStationName": "Birmingham New Street",
            "destinationStationCrs": "EUS",
            "destinationStationName": "London Euston",
            "etd": "On time",
            "std": "23:11",
            "platform": "2",
            "eta": "On time",
            "sta": "01:17",
            "cancelReason": null,
            "delayReason": null,
            "serviceID": "4776621BHAMNWS_",
            "affectedBy": null,
            "operator": "Avanti West Coast",
            "filterLocationCancelled": false,
            "cancelled": false
        }
    }
]
```
Invalid ID:
```json
{
    "httpStatus": "NOT_FOUND",
    "timestamp": "2025-08-15T20:35:02.435393607",
    "message": "No journeys found for user with ID: 2"
}
```

### <img width="34" height="15" alt="GET" src="https://github.com/user-attachments/assets/348be0ea-8445-49bd-9fd2-b8ee7e81d9ee" /> `/journey/:userid/journey/:journeyid`

Return a single journey with given journeyID and userID as path variables e.g `http://localhost:8080/api/v1/journey/1/journey/1602`. The `railDataDTO` will return the next train departure details from the current time. Invalid userID or journeyID will throw 404 and error message.

Response:
```json
{
    "journeyDTO": {
        "id": 1602,
        "userId": 1,
        "originCRS": "BHM",
        "destinationCRS": "LEI",
        "departureTime": "15:45:00",
        "days": [
            "SUNDAY",
            "SATURDAY"
        ],
        "notificationsEnabled": true
    },
    "railDataDTO": {
        "generatedAt": "2025-08-15T21:37:34.1974398+01:00",
        "departureStationCrs": "BHM",
        "departureStationName": "Birmingham New Street",
        "destinationStationCrs": "LEI",
        "destinationStationName": "Leicester",
        "etd": "On time",
        "std": "22:22",
        "platform": "12A",
        "eta": "On time",
        "sta": "23:18",
        "cancelReason": null,
        "delayReason": null,
        "serviceID": "4789396BHAMNWS_",
        "affectedBy": null,
        "operator": "CrossCountry",
        "filterLocationCancelled": false,
        "cancelled": false
    }
}
```
Invalid ID:
```json
{
    "httpStatus": "NOT_FOUND",
    "timestamp": "2025-08-15T20:42:31.938801568",
    "message": "Journey not found with ID: 3 for user with ID: 1"
}
```

### <img width="46" height="15" alt="POST" src="https://github.com/user-attachments/assets/aedb9d01-ed2f-4fe6-93ab-20e90e2fc9ff" /> `/journey`

Adds new journey to the database. E.g of request body is shown below.

Request body:
```json
{
  "notificationsEnabled": false,
  "originCRS": "BHM",
  "destinationCRS": "EUS",
  "userId": 1,
  "days": [
    "FRIDAY",
    "SATURDAY"
  ],
  "departureTime": "21:45",
  "journeyLegs": [
    {
      "origin": "Birmingham New Street",
      "originCRS": "BHM",
      "destination": "London Euston",
      "destinationCRS": "EUS"
    }
  ]
}
```

>[!WARNING]
> All fields must be present in request body as shown in example. Valid origin and destination CRS need to present. Days in the `days` field must be captialised.

Response:
```json
{
    "id": 1758,
    "notificationsEnabled": false,
    "originCRS": "BHM",
    "destinationCRS": "EUS",
    "userId": 1,
    "days": [
        "FRIDAY",
        "SATURDAY"
    ],
    "departureTime": "21:45:00",
    "journeyLegs": [
        {
            "id": 1708,
            "origin": "Birmingham New Street",
            "originCRS": "BHM",
            "destination": "London Euston",
            "destinationCRS": "EUS",
            "legOrder": 0,
            "transportProvider": null
        }
    ]
}
```

### <img width="34" height="15" alt="PUT" src="https://github.com/user-attachments/assets/9639ac4a-a370-44eb-bc2a-10daacdb4449" /> `/journey/:journeyid`

Updates journey of selected journeyID e.g `http://localhost:8080/api/v1/journey/1602`. Any field requiring update needs to be present in request body. Invalid journeyID will throw 404 and error message.

Request Body:
```json
{
    "notificationsEnabled":false,
    "days":["SATURDAY"],
    "departureTime":"15:30",
    "originCRS":"ELY",
    "destinationCRS":"CBG"
}
```

Invalid ID:
```json
{
    "httpStatus": "NOT_FOUND",
    "timestamp": "2025-08-16T14:30:39.793678135",
    "message": "Journey not found with ID: 10"
}
```

>[!WARNING]
> Only **notifcationsEnabled, days, departureTime, originCRS, destinationCRS** fields can be updated.

### <img width="70" height="15" alt="DELETE" src="https://github.com/user-attachments/assets/c282e969-4537-478e-964a-a3670f307a61" /> `/journey/:journeyid`

Deletes journey at specific journeyID given as path variable and returns a success message. Invalid journeyID will throw 404 and error message.

Response:
```
Journey Deleted
```

Invalid ID:
```json
{
    "httpStatus": "NOT_FOUND",
    "timestamp": "2025-08-16T14:35:57.964766588",
    "message": "Journey not found with ID: 30"
}
```

### ⛑️ Health Status

APIs health status can be checked at `http://localhost:8080/health`

## 🔑 API Key Setup

This project uses the [Fastest Departure Board](https://raildata.org.uk/dataProduct/P-4600b51c-29c6-4cab-b5b0-333c32cf5d3c/overview) API from the UK Rail Data Marketplace.
For security and professionalism, my personal API key is not included in the repository.

To run this project yourself:

1. Sign up to the the Rail Data Marketplace.
2. Subscribe to the [Live Fastest Departure Board](https://raildata.org.uk/dataProduct/P-4600b51c-29c6-4cab-b5b0-333c32cf5d3c/overview)
3. Create a file named apikey.txt in the project-root/src/main/resources.
4. Paste your API key into apikey.txt (no quotes, no extra spaces).
5. Run the application — it will read the key automatically.

>[!TIP]
> The API credentials will be in the specification tab under consumer key, copy this key. However you MUST use API key for **Version 1.0** of the Live Fastest Departure Board.

## Database Connections

### Dev Mode

This API can be configured to run using a H2 in-memory database connection. To utilise this, clone this repository and set `spring.profiles.active=dev` in the application.properties file.

### Connection to PostgreSQL database

To persist data, a PostgreSQL database connection is recommended. Set `spring.profiles.active=prod` in the `application.properties` file. Connection information will need to be configured in a new `application-prod.properties` file as follows:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/<YOUR_DB_NAME>
spring.datasource.username=<YOUR_USERNAME>
spring.datasource.password=<YOUR_PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🐳 Running with Docker

This project includes a `Dockerfile` and `docker-compose.yml` so you can run the app inside a container.
Using Docker ensures the project runs consistently on any machine.

1. Requirements

    - [Docker Desktop](https://www.docker.com/products/docker-desktop/) (for Windows/macOS) or [Docker Engine](https://docs.docker.com/engine/install/) (for Linux).
    - An API key from the UK Rail Data Marketplace (look at [API Key Setup](#api-key-setup)).

2. Using Docker Compose

    A `docker-compose.yml` file is included just run the command `docker compose up --build` in terminal to build     the image and the container.

## Future Considerations
- [x] Integration with mobile front-end.
- [ ] Further error handling to handle edge cases.
- [ ] Supporting journeys made up of multiple legs.
- [ ] Error handling when third party api is down.
