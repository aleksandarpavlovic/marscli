package com.alexp.cache;

import com.alexp.exception.CacheException;

import java.io.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileSystemCache<K, V> implements Cache<K, V> {

  private static final String DEFAULT_PATH = "./cache";

  private final String path;
  private final Function<K, String> keyToFilenameMapper;
  private final Function<V, Stream<String>> valueToFileMapper;
  private final Function<Stream<String>, V> fileToValueMapper;

  public FileSystemCache(
      Function<K, String> keyToFilenameMapper,
      Function<V, Stream<String>> valueToFileMapper,
      Function<Stream<String>, V> fileToValueMapper) {

    this(DEFAULT_PATH, keyToFilenameMapper, valueToFileMapper, fileToValueMapper);
  }

  public FileSystemCache(
      String path,
      Function<K, String> keyToFilenameMapper,
      Function<V, Stream<String>> valueToFileMapper,
      Function<Stream<String>, V> fileToValueMapper) {
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
      if (!dirMade) throw new CacheException("Cache directory: " + path + " cannot be created.");
    }
    if (!file.isDirectory())
      throw new CacheException("Another non-directory file already exists on the same path");
  }

  @Override
  public V get(K key) {
    String filename = keyToFilenameMapper.apply(key);
    File file = new File(path, filename);
    if (!file.exists()) return null;
    try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
      return fileToValueMapper.apply(fileReader.lines());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public V put(K key, V value) {
    String filename = keyToFilenameMapper.apply(key);
    File file = new File(path, filename);
    if (file.exists()) {
      boolean fileDeleted = file.delete();
      if (!fileDeleted)
        throw new CacheException("Failed to delete old cache file: " + file.toPath());
    }
    try (PrintWriter fileWriter = new PrintWriter(file)) {
      file.createNewFile();
      Stream<String> data = valueToFileMapper.apply(value);
      if (data == null) return value;
      data.forEach(fileWriter::println);
      fileWriter.flush();
      return value;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
