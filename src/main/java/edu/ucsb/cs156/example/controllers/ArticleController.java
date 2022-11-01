package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ArticleRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Api(description = "Article")
@RequestMapping("/api/Article")
@RestController
@Slf4j
public class ArticleController extends ApiController {

    @Autowired
    ArticleRepository articleRepository;

    @ApiOperation(value = "Get a JSON list of all articles in the database.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Article> allArticles() {
        return articleRepository.findAll();
    }

    @ApiOperation(value = "Create a new article.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Article postArticle(
            @ApiParam("title") @RequestParam String title,
            @ApiParam("url") @RequestParam String url,
            @ApiParam("explanation") @RequestParam String explanation,
            @ApiParam("email (of submitter)") @RequestParam String email,
            @ApiParam("dateAdded (ISO-8601 datetime)") @RequestParam("dateAdded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAdded)
            throws JsonProcessingException {
        log.info("title={}, url={}, explanation={}, email={}, dateAdded={}", title, url, explanation, email, dateAdded);

        return articleRepository.save(
                Article.builder()
                        .title(title)
                        .url(url)
                        .explanation(explanation)
                        .email(email)
                        .dateAdded(dateAdded)
                        .build());
    }

    @ApiOperation(value = "Retrieve an article by its ID.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Article getById(
            @ApiParam("id") @RequestParam Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));
    }

    @ApiOperation(value = "Update an article by its ID.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Article updateArticle(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid Article incoming) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));
        article.setTitle(incoming.getTitle());
        article.setEmail(incoming.getEmail());
        article.setUrl(incoming.getUrl());
        article.setExplanation(incoming.getExplanation());
        article.setDateAdded(incoming.getDateAdded());


        articleRepository.save(article);

        return article;
    }

    @ApiOperation(value = "Delete an article by its ID.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteArticle(
            @ApiParam("id") @RequestParam Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        articleRepository.delete(article);
        return genericMessage("Article with id %s deleted".formatted(id));
    }
}
