package edu.hm.cs.fs.pdf.search.service;

import edu.hm.cs.fs.pdf.search.PdfSearchApplication;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfSearchApplication.class)
@WebAppConfiguration
public class SearchServiceTest {

	@Autowired
	private SearchService searchService;

	@Test
	@DirtiesContext
	public void testPdfIndexing() throws IOException, InterruptedException {
		addOneTestDocument();
		assertThat(searchService.getDocumentCount(), equalTo(1L));
	}

	@Test
	@DirtiesContext
	public void testFuzzySearchWithTypo() throws IOException, InterruptedException {
		addOneTestDocument("Sample1.pdf");
		addOneTestDocument("Sample2.pdf");
		SearchResponse response = searchService.getSearchResponse("sampel", true); // typo is by design
		assertThat(response.getHits().totalHits(), equalTo(2L));
	}

	@Test
	@DirtiesContext
	public void testLongDocumentSearch() throws IOException, InterruptedException {
		addOneTestDocument("Sample.pdf", "VeryLong.pdf");
		SearchResponse response = searchService.getSearchResponse("last", true);
		assertThat(response.getHits().totalHits(), equalTo(1L));
	}

	@Test
	@DirtiesContext
	public void testNonFuzzySearch() throws IOException, InterruptedException {
		addOneTestDocument("Sample1.pdf");
		addOneTestDocument("Sample2.pdf");
		SearchResponse response = searchService.getSearchResponse("sample", false);
		assertThat(response.getHits().totalHits(), equalTo(2L));
	}

	private void addOneTestDocument() throws IOException {
		addOneTestDocument("Sample.pdf", "Sample.pdf");
	}

	private void addOneTestDocument(String title) throws IOException {
		addOneTestDocument(title, "Sample.pdf");
	}

	private void addOneTestDocument(String title, String filename) throws IOException {
		try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename)) {
			byte[] document = IOUtils.toByteArray(resourceAsStream);
			searchService.indexFile(title, document);
			searchService.refreshIndex();
		}
	}
}
