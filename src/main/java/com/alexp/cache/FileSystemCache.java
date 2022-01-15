package com.alexp.cache;

import com.alexp.exception.CacheInitializationException;

import java.io.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileSystemCache<K, V> implements Cache<K, V> {

  private static final String DEFAULT_PATH = "./cache";

  private final String path;
  private final Function<K, String> keyToFilenameMapper;
  private final Function<V, String> valueToFileMapper;
  private final Function<String, V> fileToValueMapper;

  public FileSystemCache(
      Function<K, String> keyToFilenameMapper,
      Function<V, String> valueToFileMapper,
      Function<String, V> fileToValueMapper) {

    this(DEFAULT_PATH, keyToFilenameMapper, valueToFileMapper, fileToValueMapper);
  }

  public FileSystemCache(
      String path,
      Function<K, String> keyToFilenameMapper,
      Function<V, String> valueToFileMapper,
      Function<String, V> fileToValueMapper) {
    this.path = path;
    this.keyToFilenameMapper = keyToFilenameMapper;
    this.valueToFileMapper = valueToFileMapper;
    this.fileToValueMapper = fileToValueMapper;

    init();
  }

  private void init() {
    File file = new File(path);
    if (!file.exists()) {
      boolean dirMade = file.mkdir();
      if (!dirMade)
        throw new CacheInitializationException("Cache directory: " + path + " cannot be created.");
    }
    if (!file.isDirectory())
      throw new CacheInitializationException(
          "Another non-directory file already exists on the same path");
  }

  @Override
  public V get(K key) {
    String filename = keyToFilenameMapper.apply(key);
    File file = new File(path, filename);
    if (!file.exists()) return null;
    try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
      String data = fileReader.lines().collect(Collectors.joining(System.lineSeparator()));
      return fileToValueMapper.apply(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public V put(K key, V value) {
    String filename = keyToFilenameMapper.apply(key);
    File file = new File(path, filename);
    if (file.exists()) file.delete();
    try (FileWriter fileWriter = new FileWriter(file)) {
      file.createNewFile();
      String data = valueToFileMapper.apply(value);
      if (data == null || data.isEmpty()) return value;
      fileWriter.write(data);
      fileWriter.flush();
      return value;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
