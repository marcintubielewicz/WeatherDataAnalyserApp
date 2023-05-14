# weatherDataAnalyser (Weather Analyzer Data Application)

The Weather Analyzer Data Application is a Scala project that retrieves weather data from the OpenWeatherMap API, parses the JSON response, and saves it to a PostgreSQL database. This application allows you to analyze weather data for different locations.

## Prerequisites

Before running the Weather Analyzer Data Application, ensure that you have the following prerequisites installed:

- Scala: [Download and install Scala](https://www.scala-lang.org/download/)
- PostgreSQL: [Download and install PostgreSQL](https://www.postgresql.org/download/)

## Setup

1. Clone the repository:

2. Configure the application:

- Open the `application.conf` file in the `src/main/resources` directory.
- Update the `openweathermap.apiKey` property with your OpenWeatherMap API key.
- Update the `postgres.properties` properties with your PostgreSQL database connection details (url, database name, username, password).

3. Create the PostgreSQL database:

- Connect to your PostgreSQL database using a tool like `psql` or a graphical client.
- Create a new database with the desired name (e.g., `weatherappdata`).

4. Build and run the application:

- Open a terminal and navigate to the project root directory.
- Build the project using the following command:
  ```
  sbt compile
  ```
- Run the application using the following command:
  ```
  sbt run
  ```

## Usage

The Weather Analyzer Data Application automatically retrieves weather data from the OpenWeatherMap API, parses the JSON response, and saves it to the configured PostgreSQL database. The data is stored in the `weather_data` table with different columns representing various weather attributes.

You can modify the application to fetch weather data for specific locations or customize the database table schema as per your requirements.

## Contributing

Contributions to the Weather Analyzer Data Application are welcome. If you want to contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them with descriptive commit messages.
4. Push your changes to your forked repository.
5. Submit a pull request to the main repository.

## License

The Weather Analyzer Data Application is licensed under the [MIT License](LICENSE). Feel free to use, modify, and distribute the application according to the terms of the license.

## Contact

If you have any questions or suggestions regarding the Weather Analyzer Data Application, please contact me at marcin.tubielewicz@gmail.com

Happy weather analyzing!

