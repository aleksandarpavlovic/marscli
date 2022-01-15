package com.alexp.cli;

import com.alexp.service.PhotoService;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.help.Examples;
import com.github.rvesse.airline.annotations.restrictions.AllowedValues;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

@Command(name = "photos")
@Examples(
    examples = {"photos", "photos -d 2020-06-10 -n 2 -r spirit -c navcam"},
    descriptions = {
      "Runs with default parameters.",
      "Gets the photos from 10th and 9th of June 2020, from the Spirit rover, shot with NAVCAM."
    })
public class PhotosCommand implements Runnable {

  @Option(
      name = {"-d", "--date"},
      description = "Date of the photos in format yyyy-mm-dd (default: today's date)",
      typeConverterProvider = LocalDateConverterProvider.class)
  private LocalDate date = LocalDate.now();

  @Option(
      name = {"-n", "--days"},
      description =
          "Number of days prior to and including --date to get the photos for (default: 10)")
  private int daysBack = 10;

  @Option(
      name = {"-r", "--rover"},
      description = "Rover: (default: curiosity)")
  @AllowedValues(allowedValues = {"curiosity", "opportunity", "spirit"})
  private String rover = "curiosity";

  @Option(
      name = {"-c", "--camera"},
      description = "Camera: (default: navcam)")
  @AllowedValues(
      allowedValues = {
        "fhaz", "rhaz", "mast", "chemcam", "mahli", "mardi", "navcam", "pancam", "minites"
      })
  private String camera = "navcam";

  @Option(
      name = {"-k", "--apikey"},
      description = "API Key: (default: DEMO_KEY)")
  private String apiKey = "DEMO_KEY";

  @Override
  public void run() {
    var photoService = new PhotoService();
    System.out.println(formatToJson(photoService.getPhotos(rover, camera, date, daysBack, apiKey)));
  }

  private String formatToJson(Object o) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(o);
  }
}
