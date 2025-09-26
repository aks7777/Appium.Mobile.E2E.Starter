package company.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.Map;

public class APIManager {
    private RequestSpecification request;
    private String tenantID;
    private String platform;
    private String buildVersion;

    public APIManager(String url)
    {
        RestAssured.baseURI = ReadProperties.getValue(url);
        request = RestAssured.given().relaxedHTTPSValidation();
    }

    public APIManager(String url, String endpoint)
    {
        String baseUri = ReadProperties.getValue(url);
        RestAssured.baseURI = baseUri + endpoint;
        request = RestAssured.given().relaxedHTTPSValidation();
    }

    /**
     * Overloaded POST method with a JSON payload file, and no headers, and token.
     *
     * @param endpoint Path for the POST request
     * @param payload  Name of the json file for the POST request
     * @return Response object
     */
    public Response sendPostRequest(String endpoint, String payload) {
        return sendPostRequest(endpoint, payload, null);
    }

    /**
     * Overloaded POST method with a JSON payload file, headers, and no token.
     *
     * @param endpoint endpoint for the POST request
     * @param payload  Name of the json file for the POST request
     * @param headers  Headers for the request
     * @return Response object
     */
    public Response sendPostRequest(String endpoint, String payload, Map<String, String> headers) {
        return sendPostRequest(endpoint, payload, headers, "");
    }

    /**
     * Sends a POST request to the specified path with a JSON payload file, headers, and token.
     *
     * @param endpoint Path for the POST request
     * @param payload  Name of the json file for the POST request
     * @param headers  Optional headers for the request
     * @param token    Optional token for the request
     * @return Response object
     */

    //This method is WIP and can be subject to updates in the future
    public Response sendPostRequest(String endpoint, String payload, Map<String, String> headers, String token) {
        // initializing the request with HTTPS validation
        request = RestAssured.given().relaxedHTTPSValidation();
        Response response = null;

        // Checking if headers are provided -> then checking if custom headers contain any default headers
        // If the default headers are changed, then add those headers. If no default headers are in header map, then set default values
        if (headers != null) {
            if (!headers.containsKey("TenantId")) {
                headers.put("TenantId", tenantID);
            }
            if (!headers.containsKey("Platform")) {
                headers.put("Platform", platform);
            }
            if (!headers.containsKey("BuildVersion")) {
                headers.put("BuildVersion", buildVersion);
            }
            if (!headers.containsKey("Content-Type")) {
                headers.put("Content-Type", "application/json");
            }
            request = request.headers(headers);

            //If no custom headers were provided, set default headers with default values
        } else {
            request = request.header("TenantId", tenantID)
                    .header("Content-Type", "application/json")
                    .header("BuildVersion", buildVersion)
                    .header("platform", platform);
        }

        if (token != null && !token.isEmpty()) {
            request = request.header("Authorization", "Bearer " + token);
        }

        try {
            response = request
                    .urlEncodingEnabled(false)
                    .body(payload)
//                    .log().all()
                    .when().post(endpoint)
                    .then()
//                    .log().status()
//                    .log().body()
                    .extract().response();
        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error occurred while sending post request " + endpoint);
//            Assert.fail("Error occurred while sending post request : " + e);
            throw new RuntimeException("Error occurred while sending post request : " + e);
        }
        LoggerUtilities.infoLoggerInFileAndReport("Post request sent successfully " + endpoint);
        return response;
    }

    /**
     * Overloaded GET method without any additional parameters.
     *
     * @param path The path to send the request to
     * @return Response object
     */
    public Response sendGetRequest(String path) {
        return sendGetRequest(path, "");
    }


    /**
     * Overloaded GET method with an authorization header containing the specified token.
     *
     * @param path  The path to send the request to
     * @param token The authorization token to include in the request header
     * @return The response of the GET request
     */
    public Response sendGetRequest(String path, String token) {
        return sendGetRequest(path, token, (Map<String, String>) null);
    }


    /**
     * Overloaded GET method with the specified query single or multiple parameters.
     *
     * @param path   The path to send the request to
     * @param params An array of key-value pairs to include as query parameters in the request
     * @return The response of the GET request
     */
    public Response sendGetRequest(String path, String... params) {
        return sendGetRequest(path, "", null, params);
    }


    /**
     * Sends a GET request to the specified path with the specified authorization, headers, and query parameters.
     *
     * @param endpoint The path to send the request to
     * @param token    The authorization token to include in the request header
     * @param headers  A map of headers to include in the request
     * @param params   An array of key-value pairs to include as query parameters in the request
     * @return The response of the GET request
     */
    public Response sendGetRequest(String endpoint, String token, Map<String, String> headers, String... params) {
        Response response = null;
        request = RestAssured.given().relaxedHTTPSValidation();
        try {
            if (!token.isEmpty()) {
                request = request.header("Authorization", token);
            }

            if (headers != null) {
                if (!headers.containsKey("TenantId")) {
                    headers.put("TenantId", tenantID);
                }
                request = request.headers(headers);
                if (headers.containsKey("Platform")) {
                    headers.put("Platform", headers.get("Platform"));
                }
                if (headers.containsKey("BuildVersion")) {
                    headers.put("BuildVersion", headers.get("BuildVersion"));
                }
            } else {
                request = request.header("TenantId", tenantID);
            }

            // Optional argument control flow with single or multiple parameters

            if (params.length != 0) {
                params = Arrays.stream(params).filter(s -> !s.isEmpty()).toArray(String[]::new);
                for (String param : params) {
                    String[] parts = param.split("=");
                    if (parts.length == 2) {
                        String key = parts[0];
                        String value = parts[1];
                        request = request.param(key, value);
                    }
                }
            }
            response = request
//                    .log().all()
                    .when()
                    .get(endpoint)
                    .then()
//                    .log().status()
//                    .log().body()
                    .extract()
                    .response();
        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error occurred while sending get request " + endpoint);
//            Assert.fail("Error occurred while sending get request : " + e);
            throw new RuntimeException("Error occurred while sending GET request : " + e);
        }
        LoggerUtilities.infoLoggerInFileAndReport("Get request sent successfully " + endpoint);

        return response;
    }

    public Response sendGetRequest(String endpoint, Map<String, String> headers){
        request = RestAssured.given().relaxedHTTPSValidation();
        Response response = null;
        if (headers != null) {
            if (!headers.containsKey("TenantId")) {
                headers.put("TenantId", tenantID);
            }
            request = request.headers(headers);
            if (headers.containsKey("Platform")) {
                headers.put("Platform", headers.get("Platform"));
            }
            if (headers.containsKey("BuildVersion")) {
                headers.put("BuildVersion", headers.get("BuildVersion"));
            }
        } else {
            request = request.header("TenantId", tenantID);
        }

        // Optional argument control flow with single or multiple parameters

        response = request
//                .log().all()
                .when()
                .get(endpoint)
                .then()
//                .log().status()
//                .log().body()
                .extract()
                .response();
        LoggerUtilities.infoLoggerInFileAndReport("Get request sent successfully " + endpoint);
        return response;
    }

    /**
     * Overloaded DELETE method with a JSON payload file, and no headers, and token.
     *
     * @param endpoint Path for the POST request
     * @param payload  Name of the json file for the POST request
     * @return Response object
     */
    public Response sendDeleteRequest(String endpoint, String payload) {
        return sendDeleteRequest(endpoint, payload, null);
    }


    /**
     * Overloaded DELETE method with a JSON payload file, headers, and no token.
     *
     * @param endpoint endpoint for the POST request
     * @param payload  Name of the json file for the POST request
     * @param headers  Headers for the request
     * @return Response object
     */
    public Response sendDeleteRequest(String endpoint, String payload, Map<String, String> headers) {
        return sendDeleteRequest(endpoint, payload, headers, "");
    }

    /**
     * Sends a DELETE request to the specified path with optional headers and no token.
     *
     * @param endpoint Path for the DELETE request
     * @param headers Optional headers for the request
     * @return Response object
     */
    public Response sendDeleteRequest(String endpoint, Map<String, String> headers) {
        return sendDeleteRequest(endpoint, null, headers, null);
    }


    /**
     * Sends a DELETE request to the specified path with a JSON payload file, headers, and token.
     *
     * @param endpoint Path for the POST request
     * @param payload  Name of the json file for the POST request
     * @param headers  Optional headers for the request
     * @param token    Optional token for the request
     * @return Response object
     */
    public Response sendDeleteRequest(String endpoint, String payload, Map<String, String> headers, String token) {
        request = RestAssured.given().relaxedHTTPSValidation();
        Response response = null;
        try {

            request = request //.header("platform", "android")
                    .header("Content-Type", "application/json");

            if (payload != null) { // Check if payload is not null before setting it as the body
                request = request.body(payload);
            }

            if (token != null && !token.isEmpty()) {
                request = request.header("Authorization", token);
            }

            if (headers != null) {
                if (!headers.containsKey("TenantId")) {
                    headers.put("TenantId", tenantID);
                }
                request = request.headers(headers);
            } else {
                request = request.header("TenantId", tenantID);
            }

            response = request
//                    .log().all()
                    .when().delete(endpoint)
                    .then()
//                    .log().status()
//                    .log().body()
                    .extract().response();

        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error occurred while sending delete request " + endpoint);
//            Assert.fail("Error occurred while sending delete request : " + e);
            throw new RuntimeException("Error occurred while sending delete request : " + e);
        }
        LoggerUtilities.infoLoggerInFileAndReport("Delete request sent successfully " + endpoint);

        return response;
    }

    public Response sendPutRequest(String endpoint, Map<String, String> headers, String payload) {
        request = RestAssured.given().relaxedHTTPSValidation();
        Response response = null;
        try {

            if (headers != null) {
                if (!headers.containsKey("TenantId")) {
                    headers.put("TenantId", tenantID);
                }
                if (!headers.containsKey("Platform")) {
                    headers.put("Platform", platform);
                }
                if (!headers.containsKey("BuildVersion")) {
                    headers.put("BuildVersion", buildVersion);
                }
                if (!headers.containsKey("Content-Type")) {
                    headers.put("Content-Type", "application/json");
                }
                request = request.headers(headers);
            } else {
                request = request.header("TenantId", tenantID);
            }

            response = request
                    .body(payload)
                    .when().put(endpoint)
                    .then()
                    .extract().response();

        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error occurred while sending PUT request " + endpoint);
//            Assert.fail("Error occurred while sending PUT request :" + e);
            throw new RuntimeException("Error occurred while sending PUT request : " + e);
        }
        LoggerUtilities.infoLoggerInFileAndReport("PUT request sent successfully " + endpoint);
        return response;
    }

    public Response sendPutRequest(String endpoint, String payload, Map<String, String> headers, String token) {
        Response response = null;
        try {

            request = request.header("platform", "android")
                    .header("Content-Type", "application/json");

            if (!token.isEmpty()) {
                request = request.header("Authorization", token);
            }

            if (headers != null) {
                if (!headers.containsKey("TenantId")) {
                    headers.put("TenantId", tenantID);
                }
                request = request.headers(headers);
            } else {
                request = request.header("TenantId", tenantID);
            }

            response = request
//                    .log().all()
                    .body(payload)
                    .when().put(endpoint)
                    .then()
//                    .log().status()
//                    .log().body()
                    .extract().response();

        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error occurred while sending PUT request " + endpoint);
//            Assert.fail("Error occurred while sending PUT request :" + e);
            throw new RuntimeException("Error occurred while sending PUT request : " + e);
        }
        LoggerUtilities.infoLoggerInFileAndReport("PUT request sent successfully " + endpoint);
        return response;
    }

    public Response sendPatchRequest(String endpoint, Map<String, String> headers, String payload) {
        request = RestAssured.given().relaxedHTTPSValidation();
        Response response = null;
        try {

            if (headers != null) {

                request = request.headers(headers);
            } else {
                request = request.header("TenantId", tenantID);
            }

            response = request
//                    .log().all()
                    .body(payload)
                    .when().patch(endpoint)
                    .then()
//                    .log().status()
//                    .log().body()
                    .extract().response();
            System.out.println(endpoint);


        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error occurred while sending PATCH request " + endpoint);
//            Assert.fail("Error occurred while sending post request : " + e);
            throw new RuntimeException("Error occurred while sending PUT request : " + e);
        }
        LoggerUtilities.infoLoggerInFileAndReport("PATCH request sent successfully " + endpoint);
        return response;
    }

}
