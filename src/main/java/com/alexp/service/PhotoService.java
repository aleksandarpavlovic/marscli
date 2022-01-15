package com.alexp.service;

import com.alexp.cache.Cache;
import com.alexp.cache.FileSystemCache;
import com.alexp.http.PhotoClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhotoService {
  private static final int PHOTOS_PER_DATE = 3;
  private final PhotoClient client;
  private final Cache<LocalDate, List<String>> cache;

  public PhotoService() {
    client = new PhotoClient();
    cache =
        new FileSystemCache<>(
            LocalDate::toString,
            photos -> photos.stream(),
            linestream ->
                linestream == null
                    ? Collections.emptyList()
                    : linestream.collect(Collectors.toList()));
  }

  public PhotoService(PhotoClient client, Cache<LocalDate, List<String>> cache) {
    this.client = client;
    this.cache = cache;
  }

  public Map<LocalDate, List<String>> getPhotos(
      String rover, String camera, LocalDate lastDate, int daysBack, String apiKey) {
    return Stream.generate(new ReverseLocalDateGenerator(lastDate))
        .limit(daysBack)
        .collect(
            Collectors.toMap(
                date -> date,
                date -> getPhotos(rover, camera, date, apiKey),
                (first, duplicate) -> first,
                LinkedHashMap::new));
  }

  /**
   * Looks up in cache first. If entry for the date not found, fetch from the client, cache it and
   * return. Side effect of the method call is cache update.
   */
  private List<String> getPhotos(String rover, String camera, LocalDate date, String apiKey) {
    return cache.computeIfAbsent(
        date,
        d -> {
          var photos = client.fetchPhotos(rover, camera, d, apiKey, 1);
          if (photos.isEmpty()) return photos;
          return photos.subList(0, Math.min(PHOTOS_PER_DATE, photos.size()));
        });
  }

  /**
   * LocalDate generator that starts from the provided date and counts backwards. e.g. 2020-09-02,
   * 2020-09-01, 2020-08-31,...
   */
  private static class ReverseLocalDateGenerator implements Supplier<LocalDate> {
    private LocalDate date;

    ReverseLocalDateGenerator(LocalDate date) {
      this.date = date;
    }

    @Override
    public LocalDate get() {
      var ret = date;
      date = date.minusDays(1);
      return ret;
    }
  }
}
