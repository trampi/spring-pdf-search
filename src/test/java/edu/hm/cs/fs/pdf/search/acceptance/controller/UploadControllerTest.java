package edu.hm.cs.fs.pdf.search.acceptance.controller;

import edu.hm.cs.fs.pdf.search.FixtureHelper;
import edu.hm.cs.fs.pdf.search.PdfSearchApplication;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PdfSearchApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UploadControllerTest {

    @Autowired
    private FixtureHelper fixtureHelper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUploadOneDocument() throws Exception {
        InputStream sampleStream = getClass().getClassLoader().getResourceAsStream("Sample.pdf");

        mockMvc.perform(fileUpload("/upload")
                .file(new MockMultipartFile("documents[]", "Sample.pdf", "application/pdf", IOUtils.toByteArray(sampleStream))))
                .andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/")).andExpect(model().attribute("documentCount", 1L));
    }

    @Test
    public void testUploadTwoDocuments() throws Exception {
        InputStream sampleStream = getClass().getClassLoader().getResourceAsStream("Sample.pdf");
        InputStream veryLongStream = getClass().getClassLoader().getResourceAsStream("VeryLong.pdf");

        mockMvc.perform(fileUpload("/upload")
                .file(new MockMultipartFile("documents[]", "Sample.pdf", "application/pdf", IOUtils.toByteArray(sampleStream)))
                .file(new MockMultipartFile("documents[]", "VeryLong.pdf", "application/pdf", IOUtils.toByteArray(veryLongStream))))
                .andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/")).andExpect(model().attribute("documentCount", 2L));
    }

}
