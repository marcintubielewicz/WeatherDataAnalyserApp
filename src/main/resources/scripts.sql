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

-- create query to get data from weather_data table and calculate the average temperature for each country grouped by month extracted from created_at column
SELECT
    sys_country as country,
    ROUND(AVG(main_temp)::numeric, 2) AS average_temperature,
    EXTRACT(MONTH FROM created_at) AS month
FROM
    weather_data
GROUP BY
    country,
    month
ORDER BY
    month ASC;

-- create SQL function to get data from weather_data table and calculate the average temperature for each country grouped by month extracted from created_at column
CREATE OR REPLACE FUNCTION averageMonthlyTempByCountry() RETURNS TABLE (
       country TEXT,
       avg_temp NUMERIC,
       month INTEGER
    ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            sys_country::TEXT as country,
            ROUND(AVG(main_temp)::numeric, 2)::NUMERIC as avg_temp,
            EXTRACT(MONTH FROM created_at)::INTEGER as month
        FROM
            weather_data
        GROUP BY
            country,
            month
        ORDER BY
            month ASC;
END;
$$ LANGUAGE plpgsql;


-- create SQL function to get data from weather_data table and calculate the average, min and max temperature for each country grouped by month extracted from created_at column

CREATE OR REPLACE FUNCTION averageMinMaxMonthlyTempByCountry()
    RETURNS TABLE (
                      country TEXT,
                      city TEXT,
                      average_temperature NUMERIC,
                      min_temp NUMERIC,
                      max_temp NUMERIC,
                      month INTEGER
                  )
AS $$
BEGIN
    RETURN QUERY
        SELECT
            sys_country::text AS country,
            city_name::text AS city,
            ROUND(AVG(main_temp)::numeric, 2) AS average_temperature,
            ROUND(MIN(main_temp)::numeric, 2) AS min_temp,
            ROUND(MAX(main_temp)::numeric, 2) AS max_temp,
            EXTRACT(MONTH FROM created_at)::int AS month
        FROM
            weather_data
        GROUP BY
            country,
            city,
            month
        ORDER BY
            month ASC;
END;
$$ LANGUAGE plpgsql;
