package edu.hm.cs.fs.pdf.search.acceptance.controller;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.hm.cs.fs.pdf.search.FixtureHelper;
import edu.hm.cs.fs.pdf.search.PdfSearchApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfSearchApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IndexControllerTest {

    @Autowired
    private FixtureHelper fixtureHelper;

    @Autowired
    private WebApplicationContext context;

    private WebClient webClient;

    @Before
    public void setup() {
        webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context)
                .withDelegate(new WebClient(BrowserVersion.FIREFOX_38))
                .build();
    }

    @Test
    public void testDocumentCountWithNoDocument() throws IOException {
        HtmlPage page = webClient.getPage("http://localhost/");
        assertThat(page.querySelector("#document-count").asText(), equalTo("0"));
    }

    @Test
    public void testDocumentCountWithOneDocument() throws IOException {
        fixtureHelper.addOneTestDocument();
        HtmlPage page = webClient.getPage("http://localhost/");
        assertThat(page.querySelector("#document-count").asText(), equalTo("1"));
    }

    @Test
    public void testSearch() throws IOException {
        fixtureHelper.addOneTestDocument();

        HtmlPage page = webClient.getPage("http://localhost/");
        HtmlInput searchInput = page.getHtmlElementById("search-text");
        searchInput.setValueAttribute("paragraph");
        webClient.waitForBackgroundJavaScriptStartingBefore(1500);

        assertThat(page.querySelectorAll(".search-hit").getLength(), greaterThan(0));
    }

}
