-- Create the weatherappdata database if it does not exist
CREATE DATABASE IF NOT EXISTS weatherappdata;

-- Connect to the weatherappdata database
\c weatherappdata;

-- Create the weather_data table if it does not exist
CREATE TABLE IF NOT EXISTS weather_data (
    id SERIAL PRIMARY KEY,
    coord_lon FLOAT,
    coord_lat FLOAT,
    weather_id INT,
    weather_main VARCHAR(50),
    weather_description VARCHAR(255),
    weather_icon VARCHAR(10),
    base VARCHAR(50),
    main_temp FLOAT,
    main_feels_like FLOAT,
    main_temp_min FLOAT,
    main_temp_max FLOAT,
    main_pressure INT,
    main_humidity INT,
    visibility INT,
    wind_speed FLOAT,
    wind_deg INT,
    clouds_all INT,
    dt BIGINT,
    sys_type INT,
    sys_id INT,
    sys_country VARCHAR(3),
    sys_sunrise BIGINT,
    sys_sunset BIGINT,
    timezone INT,
    city_id INT,
    city_name VARCHAR(100),
    cod INT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- create procedure to calculate the average temperature for a given city
CREATE OR REPLACE FUNCTION get_average_temp(city_name VARCHAR(100))
RETURNS FLOAT AS $$
DECLARE
    avg_temp FLOAT;
BEGIN
    SELECT AVG(main_temp) INTO avg_temp FROM weather_data WHERE city_name = city_name;
    RETURN avg_temp;
END;
$$ LANGUAGE plpgsql;


