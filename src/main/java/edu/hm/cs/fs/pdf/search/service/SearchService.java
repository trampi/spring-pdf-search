package edu.hm.cs.fs.pdf.search.service;

import com.adobe.xmp.impl.Base64;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class SearchService {

    @Value("${pdf-search.index}")
    private String index;

    @Value("${pdf-search.type}")
    private String type;

    private final Client client;

    @Autowired
    public SearchService(Client client) {
        this.client = client;
    }

    public void indexFile(String title, InputStream inputStream) throws IOException {
        indexFile(title, IOUtils.toByteArray(inputStream));
    }

    public void indexFile(String title, byte[] file) throws IOException {
        client.prepareIndex(index, type, title)
                .setSource(jsonBuilder()
                        .startObject()
                        .field("file", new String(Base64.encode(file), Charset.forName("utf-8")))
                        .field("title", title)
                        .endObject())
                .execute()
                .actionGet();
    }

    public long getDocumentCount() {
        return client.prepareCount(index).execute().actionGet().getCount();
    }

    public void refreshIndex() {
        client.admin().indices().prepareRefresh(index).execute().actionGet();
    }

    public SearchResponse getSearchResponse(String search, boolean fuzzy) {
        QueryBuilder query;
        if (fuzzy) {
            query = QueryBuilders.fuzzyQuery("file", search);
        } else {
            query = QueryBuilders.simpleQueryStringQuery(search);
        }
        return client.prepareSearch(index)
                .setTypes(type)
                .setQuery(query)
                .addField("title")
                .addHighlightedField("file")
                .setHighlighterPreTags("<mark>")
                .setHighlighterPostTags("</mark>")
                .setSize(Integer.MAX_VALUE)
                .execute()
                .actionGet();
    }
}
