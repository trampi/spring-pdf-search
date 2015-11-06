package edu.hm.cs.fs.pdf.search.unit.service;

import edu.hm.cs.fs.pdf.search.FixtureHelper;
import edu.hm.cs.fs.pdf.search.PdfSearchApplication;
import edu.hm.cs.fs.pdf.search.service.SearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfSearchApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private FixtureHelper fixtureHelper;

    @Test
    public void testPdfIndexing() throws IOException, InterruptedException {
        fixtureHelper.addOneTestDocument();
        assertThat(searchService.getDocumentCount(), equalTo(1L));
    }

    @Test
    public void testFuzzySearchWithTypo() throws IOException, InterruptedException {
        fixtureHelper.addOneTestDocument("Sample1.pdf");
        fixtureHelper.addOneTestDocument("Sample2.pdf");
        SearchResponse response = searchService.getSearchResponse("sampel", true); // typo is by design
        assertThat(response.getHits().totalHits(), equalTo(2L));
    }

    @Test
    public void testLongDocumentSearch() throws IOException, InterruptedException {
        fixtureHelper.addOneTestDocument("Sample.pdf", "VeryLong.pdf");
        SearchResponse response = searchService.getSearchResponse("last", true);
        assertThat(response.getHits().totalHits(), equalTo(1L));
    }

    @Test
    public void testNonFuzzySearch() throws IOException, InterruptedException {
        fixtureHelper.addOneTestDocument("Sample1.pdf");
        fixtureHelper.addOneTestDocument("Sample2.pdf");
        SearchResponse response = searchService.getSearchResponse("sample", false);
        assertThat(response.getHits().totalHits(), equalTo(2L));
    }

}
