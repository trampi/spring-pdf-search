package edu.hm.cs.fs.pdf.search.controller;

import edu.hm.cs.fs.pdf.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class UploadController {

    @Autowired
    SearchService elasticsearch;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadDocuments(@RequestParam("documents[]") final List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                elasticsearch.indexFile(file.getOriginalFilename(), file.getBytes());
            }
        }
        elasticsearch.refreshIndex();
        return "redirect:/";
    }

}
