package com.alexp.service;

import com.alexp.cache.Cache;
import com.alexp.http.PhotoClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {
  @Mock private PhotoClient photoClient;
  @Mock private Cache<PhotoService.CacheKey, List<String>> cache;

  @Test
  public void testGetPhotos_twoPerDay() {
    var imageNameGenerator = new ImageNameGenerator();
    Mockito.when(
            photoClient.fetchPhotos(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyInt()))
        .thenAnswer(invocation -> List.of(imageNameGenerator.get(), imageNameGenerator.get()));
    Mockito.when(cache.computeIfAbsent(Mockito.any(), Mockito.any()))
        .thenAnswer(
            invocation ->
                ((Function<PhotoService.CacheKey, List<String>>) invocation.getArgument(1))
                    .apply(invocation.getArgument(0)));

    PhotoService photoService = new PhotoService(photoClient, cache);

    Map<LocalDate, List<String>> result =
        photoService.getPhotos("rover", "cam", LocalDate.of(2020, 01, 20), 5, "key");
    Assertions.assertEquals(List.of("img1", "img2"), result.get(LocalDate.of(2020, 01, 20)));
    Assertions.assertEquals(List.of("img3", "img4"), result.get(LocalDate.of(2020, 01, 19)));
    Assertions.assertEquals(List.of("img5", "img6"), result.get(LocalDate.of(2020, 01, 18)));
    Assertions.assertEquals(List.of("img7", "img8"), result.get(LocalDate.of(2020, 01, 17)));
    Assertions.assertEquals(List.of("img9", "img10"), result.get(LocalDate.of(2020, 01, 16)));
  }

  @Test
  public void testGetPhotos_noneFetched_noneReturned() {
    Mockito.when(
            photoClient.fetchPhotos(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyInt()))
        .thenAnswer(invocation -> Collections.emptyList());
    Mockito.when(cache.computeIfAbsent(Mockito.any(), Mockito.any()))
        .thenAnswer(
            invocation ->
                ((Function<PhotoService.CacheKey, List<String>>) invocation.getArgument(1))
                    .apply(invocation.getArgument(0)));

    PhotoService photoService = new PhotoService(photoClient, cache);

    Map<LocalDate, List<String>> result =
        photoService.getPhotos("rover", "cam", LocalDate.of(2020, 01, 20), 3, "key");
    Assertions.assertEquals(Collections.emptyList(), result.get(LocalDate.of(2020, 01, 20)));
    Assertions.assertEquals(Collections.emptyList(), result.get(LocalDate.of(2020, 01, 19)));
    Assertions.assertEquals(Collections.emptyList(), result.get(LocalDate.of(2020, 01, 18)));
  }

  @Test
  public void testGetPhotos_fetchedFive_threeReturned() {
    Mockito.when(
            photoClient.fetchPhotos(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyInt()))
        .thenAnswer(invocation -> List.of("1", "2", "3", "4", "5"));
    Mockito.when(cache.computeIfAbsent(Mockito.any(), Mockito.any()))
        .thenAnswer(
            invocation ->
                ((Function<PhotoService.CacheKey, List<String>>) invocation.getArgument(1))
                    .apply(invocation.getArgument(0)));

    PhotoService photoService = new PhotoService(photoClient, cache);

    Map<LocalDate, List<String>> result =
        photoService.getPhotos("rover", "cam", LocalDate.of(2020, 01, 20), 1, "key");
    Assertions.assertEquals(List.of("1", "2", "3"), result.get(LocalDate.of(2020, 01, 20)));
  }

  private static class ImageNameGenerator implements Supplier<String> {
    private String baseName = "img";
    private int counter = 1;

    @Override
    public String get() {
      return baseName + counter++;
    }
  }
}
