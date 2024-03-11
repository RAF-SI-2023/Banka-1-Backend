# Backend Banka 1

## Running the application

### Locally

1. Clone the repository
2. Create application.properties file in the src/main/resources directory(see example/application.properties.example)
3. Setup your database locally
4. Run the application

### Docker

1. Clone the repository
2. Create application.properties file in the src/main/resources directory(see example/application.properties.example)
3. Use the commented datasource url in the application.properties file
4. Create init.sql file in root directory of the project(see init.sql.example)
5. Create docker-compose.yaml file in the root directory of the project(see docker-compose.yaml.example)
4. Run the following command to run the application
```bash
docker-compose up
```
5. Stop the application using the following command
```bash
docker-compose down
```

Note: If you are in development stages, you can use the following command to rebuild the image
```bash
docker-compose up --build
```

Troubleshooting: If you encounter any issues with creating the tables in the database, you can switch from create-drop to create strategy