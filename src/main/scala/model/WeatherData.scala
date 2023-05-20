package model

/**
 * The `WeatherData` case class represents weather data for a specific location.
 * @param coord       The coordinates of the location.
 * @param weather     The weather conditions.
 * @param base        The source of the weather data.
 * @param main        The main weather parameters.
 * @param visibility  The visibility in meters.
 * @param wind        The wind conditions.
 * @param clouds      The cloudiness.
 * @param dt          The time of data calculation in Unix timestamp format.
 * @param sys         Additional system-related data.
 * @param timezone    The time zone offset from UTC in seconds.
 * @param id          The ID of the location.
 * @param name        The name of the location.
 * @param cod         The HTTP response code.
 */
case class WeatherData(
                        coord: Coord,
                        weather: Seq[Weather],
                        base: String,
                        main: Main,
                        visibility: Int,
                        wind: Wind,
                        clouds: Clouds,
                        dt: Int,
                        sys: Sys,
                        timezone: Int,
                        id: Int,
                        name: String,
                        cod: Int
                      )

/**
 * The `Coord` case class represents the coordinates of a location.
 * @param lon  The longitude of the location.
 * @param lat  The latitude of the location.
 */
case class Coord(lon: Double, lat: Double)

/**
 * The `Weather` case class represents weather conditions.
 * @param id          The weather condition ID.
 * @param main        The main weather category.
 * @param description The detailed weather description.
 * @param icon        The weather icon code.
 */
case class Weather(id: Int, main: String, description: String, icon: String)

/**
 * The `Main` case class represents main weather parameters.
 * @param temp       The temperature in Kelvin.
 * @param feels_like The "feels like" temperature in Kelvin.
 * @param temp_min   The minimum temperature in Kelvin.
 * @param temp_max   The maximum temperature in Kelvin.
 * @param pressure   The atmospheric pressure in hPa.
 * @param humidity   The relative humidity in percentage.
 */
case class Main(
                 temp: Double,
                 feels_like: Double,
                 temp_min: Double,
                 temp_max: Double,
                 pressure: Int,
                 humidity: Int
               )

/**
 * The `Wind` case class represents wind conditions.
 * @param speed The wind speed in meters per second.
 * @param deg   The wind direction in degrees.
 */
case class Wind(speed: Double, deg: Int)

/**
 * The `Clouds` case class represents cloudiness.
 * @param all The cloudiness in percentage.
 */
case class Clouds(all: Int)

/**
 * The `Sys` case class represents additional system-related data.
 * @param type     The system parameter type.
 * @param id       The system parameter ID.
 * @param country  The country code.
 * @param sunrise  The sunrise time in Unix timestamp format.
 * @param sunset   The sunset time in Unix timestamp format.
 */
case class Sys(`type`: Int, id: Int, country: String, sunrise: Long, sunset: Long)

