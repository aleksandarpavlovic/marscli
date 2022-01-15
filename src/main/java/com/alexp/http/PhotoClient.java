package com.alexp.http;

import com.alexp.exception.FailedHttpRequestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoClient {
  private static final String BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers";

  private final HttpClient httpClient;

  public PhotoClient() {
    httpClient = HttpClient.newHttpClient();
  }

  public PhotoClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<String> fetchPhotos(
      String rover, String camera, LocalDate photoDate, String apiKey, int page)
      throws FailedHttpRequestException {
    var request = buildRequest(rover, camera, photoDate, apiKey, page);
    HttpResponse<String> response;
    try {
      response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      throw new FailedHttpRequestException("Failed to fetch photos from Nasa API", e);
    }
    if (isSuccess(response)) return extractPhotos(response);
    else
      throw new FailedHttpRequestException(
          "Nasa API call was unsuccessful with status code: " + response.statusCode());
  }

  private HttpRequest buildRequest(
      String rover, String camera, LocalDate photoDate, String apiKey, int page) {
    var sb = new StringBuilder(BASE_URL);
    sb.append("/")
        .append(rover)
        .append("/")
        .append("photos")
        .append("?camera=")
        .append(camera)
        .append("&earth_date=")
        .append(photoDate.toString())
        .append("&api_key=")
        .append(apiKey)
        .append("&page=")
        .append(page);
    return HttpRequest.newBuilder().uri(URI.create(sb.toString())).GET().build();
  }

  private List<String> extractPhotos(HttpResponse<String> response) {
    if (response.body() == null || response.body().isEmpty()) return Collections.emptyList();
    JsonElement json = JsonParser.parseString(response.body());
    if (json == null || !json.isJsonObject()) return Collections.emptyList();
    JsonArray arr = ((JsonObject) json).getAsJsonArray("photos");
    if (arr == null || arr.isEmpty()) return Collections.emptyList();
    List<String> result = new ArrayList<>(arr.size());
    arr.forEach(
        e -> {
          String photo = ((JsonObject) e).getAsJsonPrimitive("img_src").getAsString();
          result.add(photo);
        });
    return result;
  }

  private boolean isSuccess(HttpResponse response) {
    return response.statusCode() >= 200 && response.statusCode() < 300;
  }
}
