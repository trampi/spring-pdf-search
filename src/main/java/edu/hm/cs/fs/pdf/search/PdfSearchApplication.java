package edu.hm.cs.fs.pdf.search;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@SpringBootApplication
public class PdfSearchApplication {

    @Value("${pdf-search.index}")
    private String index;

    @Value("${pdf-search.type}")
    private String type;

    private Node node;

    public static void main(String[] args) {
        SpringApplication.run(PdfSearchApplication.class, args);
    }

    @Bean
    public Node node() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("path.home", "./elasticsearch-data")
                .build();
        this.node = nodeBuilder()
                .local(true)
                .settings(settings)
                .node();
        return node;
    }

    @PreDestroy
    public void shutdownNode() {
        node.close();
    }

    @Bean
    public Client client(Node node) {
        return node.client();
    }

    @Autowired
    public void createIndex(Client client) throws IOException {
        dropIndexIfExists(client);
        client.admin()
                .indices()
                .prepareCreate(index)
                .setSettings(getIndexSettings())
                .addMapping(type, readResourceAsUtf8("elasticsearch-mapping.json"))
                .execute()
                .actionGet();
    }

    private void dropIndexIfExists(Client client) {
        try {
            client.admin().indices().prepareDelete(index).execute().actionGet();
        } catch (IndexMissingException e) {
            // index did not yet exist, it is not a problem we have to deal with
        }
    }

    private Map<String, Object> getIndexSettings() {
        return ImmutableMap.<String, Object>builder()
                .put("index.mapping.attachment.indexed_chars", "-1")
                .put("index.mapping.attachment.detect_language", "true")
                .build();
    }

    private String readResourceAsUtf8(String filename) throws IOException {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            return IOUtils.toString(resourceAsStream, Charset.forName("utf-8"));
        }
    }

}
