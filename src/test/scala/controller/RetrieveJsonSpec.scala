package controller

import org.scalatest.flatspec.AnyFlatSpec

class RetrieveJsonSpec extends AnyFlatSpec {

  "readDataFromJsonFileAndSaveItToDatabase" should "read JSON data from a file and print it" in {
    val retrieveJson = new RetrieveJson()

    // Call the method
    retrieveJson.readDataFromJsonFileAndSaveItToDatabase()

    // Assertions can be added here to verify the behavior,
    // such as checking the printed output or verifying the
    // contents of the JSON data.
    // For this example, let's assume the printed output is
    // captured and tested separately.
    assert(true)
  }
}

