package testmin.bench;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

/** Minimal CSV writer for numeric benchmark output. Use with try-with-resources. */
final class CsvWriter implements AutoCloseable {

    private final BufferedWriter out;
    private final int columns;

    CsvWriter(Path path, String... header) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        this.out = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        this.columns = header.length;
        writeRow((Object[]) header);
    }

    void writeRow(Object... values) throws IOException {
        if (values.length != columns) {
            throw new IllegalArgumentException("Expected " + columns + " values, got " + values.length);
        }
        StringJoiner line = new StringJoiner(",");
        for (Object v : values) {
            line.add(String.valueOf(v));
        }
        out.write(line.toString());
        out.newLine();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
