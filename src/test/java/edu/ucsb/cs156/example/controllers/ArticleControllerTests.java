package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.ArticleRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ArticleController.class)
@Import(TestConfig.class)
public class ArticleControllerTests extends ControllerTestCase {

  @MockBean
  ArticleRepository articleRepository;

  @MockBean
  UserRepository userRepository;

  // Authorization tests for /api/Article/admin/all

  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc.perform(get("/api/Article/all"))
            .andExpect(status().is(403)); // logged out users can't get all
  }

  @WithMockUser(roles = { "USER" })
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc.perform(get("/api/Article/all"))
            .andExpect(status().is(200)); // logged
  }

  @Test
  public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc.perform(get("/api/Article?id=24"))
            .andExpect(status().is(403));
  }

  // Authorization tests for /api/Article/post
  // (Perhaps should also have these for put and delete)

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/Article/post"))
            .andExpect(status().is(403));
  }

  @WithMockUser(roles = { "USER" })
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/Article/post"))
            .andExpect(status().is(403)); // only admins can post
  }

  // // Tests with mocks for database actions

  @WithMockUser(roles = { "USER" })
  @Test
  public void logged_in_user_can_get_all_articles() throws Exception {

    // arrange
    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

    Article article1 = Article.builder()
            .title("Oski vs Ole")
            .explanation("Oski and Ole show down!")
            .url("https://example.com/oski-ole")
            .email("mapache@raccoon.islavista")
            .dateAdded(ldt1)
            .build();

    LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");


    Article article2 = Article.builder()
            .title("Ole vs Triton")
            .explanation("Triton and Ole face off!")
            .url("https://example.com/triton-ole")
            .email("mapache@raccoon.islavista")
            .dateAdded(ldt2)
            .build();


    ArrayList<Article> expected = new ArrayList<>();
    expected.addAll(Arrays.asList(article1, article2));

    when(articleRepository.findAll()).thenReturn(expected);

    // act
    MvcResult response = mockMvc.perform(get("/api/Article/all"))
            .andExpect(status().isOk()).andReturn();

    // assert

    verify(articleRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expected);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = { "ADMIN", "USER" })
  @Test
  public void an_admin_user_can_post_a_new_article() throws Exception {
    // arrange

    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

    Article article = Article.builder()
            .title("Sammy v. Ole")
            .explanation("Sammy sues Ole for civil damages!")
            .url("https://example.com/sammy-ole")
            .email("mapache@raccoon.islavista")
            .dateAdded(ldt)
            .build();

    when(articleRepository.save(eq(article))).thenReturn(article);

    // act
    MvcResult response = mockMvc.perform(
                    post("/api/Article/post?title=Sammy v. Ole&explanation=Sammy sues Ole for civil damages!&url=https://example.com/sammy-ole&email=mapache@raccoon.islavista&dateAdded=2022-01-03T00:00:00")
                            .with(csrf()))
            .andExpect(status().isOk()).andReturn();

    // assert
    verify(articleRepository, times(1)).save(article);
    String expectedJson = mapper.writeValueAsString(article);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }


  @WithMockUser(roles = { "USER" })
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
    Article article = Article.builder()
            .id(24L)
            .title("Joe Bruin Ole Collab")
            .explanation("Joe Bruin and Ole collaborate on a new hit single!")
            .url("https://example.com/joe-bruin-ole")
            .email("mapache@raccoon.islavista")
            .dateAdded(LocalDateTime.parse("2022-01-03T00:00:00"))
            .build();

    when(articleRepository.findById(eq(24L))).thenReturn(Optional.of(article));

    MvcResult response = mockMvc.perform(get("/api/Article?id=24"))
            .andExpect(status().isOk()).andReturn();

    verify(articleRepository, times(1)).findById(eq(24L));
    String expectedJson = mapper.writeValueAsString(article);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = { "USER" })
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
    when(articleRepository.findById(eq(24L))).thenReturn(Optional.empty());

    MvcResult response = mockMvc.perform(get("/api/Article?id=24"))
            .andExpect(status().isNotFound()).andReturn();

    verify(articleRepository, times(1)).findById(eq(24L));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("Article with id 24 not found", json.get("message"));
  }

}
