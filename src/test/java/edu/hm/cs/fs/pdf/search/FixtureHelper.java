package edu.hm.cs.fs.pdf.search;

import edu.hm.cs.fs.pdf.search.service.SearchService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FixtureHelper {

    @Autowired
    private SearchService searchService;

    public void addOneTestDocument() throws IOException {
        addOneTestDocument("Sample.pdf", "Sample.pdf");
    }

    public void addOneTestDocument(String title) throws IOException {
        addOneTestDocument(title, "Sample.pdf");
    }

    public void addOneTestDocument(String title, String filename) throws IOException {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            byte[] document = IOUtils.toByteArray(resourceAsStream);
            searchService.indexFile(title, document);
            searchService.refreshIndex();
        }
    }
}
