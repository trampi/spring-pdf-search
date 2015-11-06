package edu.hm.cs.fs.pdf.search.controller;

import edu.hm.cs.fs.pdf.search.service.SearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class IndexController {

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index(Model model) {
        model.addAttribute("documentCount", searchService.getDocumentCount());
        return "index";
    }

    @RequestMapping("/search")
    @ResponseBody
    String result(@RequestParam("q") String search, @RequestParam("fuzzy") boolean fuzzy) throws IOException {
        SearchResponse searchResponse = searchService.getSearchResponse(search, fuzzy);
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        searchResponse.toXContent(builder, ToXContent.EMPTY_PARAMS);
        builder.endObject();
        return builder.string();
    }

}
