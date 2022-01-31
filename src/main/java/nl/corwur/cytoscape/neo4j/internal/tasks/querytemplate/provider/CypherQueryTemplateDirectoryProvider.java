package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.provider;

import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.CypherQueryTemplate;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.template.Reader;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.template.ReaderException;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.template.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CypherQueryTemplateDirectoryProvider {

    private static Logger logger = LoggerFactory.getLogger(CypherQueryTemplateDirectoryProvider.class);

    private Map<Long, TemplateMapEntry> cypherQueryTemplateMap = new HashMap<>();

    public static CypherQueryTemplateDirectoryProvider create() {
        return new CypherQueryTemplateDirectoryProvider();
    }

    private CypherQueryTemplateDirectoryProvider() {
    }

    public Map<Long, CypherQueryTemplate> getCypherQueryTemplateMap() {
        return cypherQueryTemplateMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().queryTemplate));
    }

    public Optional<CypherQueryTemplate> getCypherQueryTemplate(Long id) {
        if (cypherQueryTemplateMap.containsKey(id)) {
            return Optional.of(cypherQueryTemplateMap.get(id).queryTemplate);
        } else {
            return Optional.empty();
        }
    }

    public void readDirectory(Path templateDirectory) {
        this.cypherQueryTemplateMap.clear();
        Reader reader = new Reader();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(templateDirectory, "*.xml")) {
            long id = 0;
            for (Path filePath : directoryStream) {
                CypherQueryTemplate queryTemplate = parseTemplateQueryXml(reader, filePath);
                if (queryTemplate != null) {
                    this.cypherQueryTemplateMap.put(id++, new TemplateMapEntry(queryTemplate, filePath));
                }
            }
        } catch (IOException e) {
            logger.warn("Cannot read file: " + e.getMessage());
        }
    }

    private CypherQueryTemplate parseTemplateQueryXml(Reader reader, Path filePath) throws IOException {
        InputStream in = Files.newInputStream(filePath, StandardOpenOption.READ);
        try {
            return reader.read(in);
        } catch (ReaderException e) {
            logger.warn("Cannot parse query template file: " + filePath.toAbsolutePath().toString() + " : " + e.getMessage());
            return null;
        }
    }

    public long addCypherQueryTemplate(Path path, CypherQueryTemplate cypherQuery) throws IOException, JAXBException {

        Writer writer = new Writer();
        OutputStream outputStream = Files.newOutputStream(path);
        writer.write(cypherQuery, outputStream);
        outputStream.close();

        long id = 1 + cypherQueryTemplateMap.keySet().stream().max(Long::compare).orElse(0l);
        cypherQueryTemplateMap.put(id, new TemplateMapEntry(cypherQuery, path));
        return id;
    }

    public void putCypherQueryTemplate(Long id, CypherQueryTemplate cypherQuery) throws IOException, JAXBException {

        if (cypherQueryTemplateMap.containsKey(id)) {
            Path path = cypherQueryTemplateMap.get(id).filePath;
            Writer writer = new Writer();
            OutputStream outputStream = Files.newOutputStream(path);
            writer.write(cypherQuery, outputStream);
            outputStream.close();
            cypherQueryTemplateMap.put(id, new TemplateMapEntry(cypherQuery, path));
        } else {
            throw new IllegalStateException();
        }
    }

    private class TemplateMapEntry {
        final CypherQueryTemplate queryTemplate;
        final Path filePath;

        private TemplateMapEntry(CypherQueryTemplate queryTemplate, Path filePath) {
            this.queryTemplate = queryTemplate;
            this.filePath = filePath;
        }
    }
}
