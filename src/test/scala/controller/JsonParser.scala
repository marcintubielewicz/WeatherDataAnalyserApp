package controller

import io.circe._, io.circe.parser._

class JsonParser {
  val jsonString =
    """
      |{
      | "textField": "textContent",
      | "numericField": 123,
      | "booleanField": true,
      | "nestedObject": {
      | "arrayField": [1, 2, 3]
      | }
      |}
      |""".stripMargin

  val parseResult: Either[ParsingFailure, Json] = parse(jsonString)

  parseResult match {
    case Left(parsingError) =>
      throw new IllegalArgumentException(s"Invalid JSON object: ${parsingError.message}")
    case Right(json) => // here we use the JSON object
  }
}
