package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.hibernate.graph.internal.parse.HEGLTokenTypes;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = HelpRequestController.class)
@Import(TestConfig.class)
public class HelpRequestControllerTests extends ControllerTestCase {

        @MockBean
        HelpRequestRepository helpRequestRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/HelpRequest/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/HelpRequest/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/HelpRequest/all"))
                                .andExpect(status().is(200)); // logged
        }

        // Authorization tests for /api/HelpRequest/admin/post

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/HelpRequest/post"))
                            .andExpect(status().is(403)); // logged out users can't post
        }

        // logged in users can post
        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_post() throws Exception {
                LocalDateTime ldt = LocalDateTime.parse("2022-04-20T17:35");

                HelpRequest request = HelpRequest.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .teamId("s22-5pm-3")
                                .tableOrBreakoutRoom("7")
                                .requestTime(ldt)
                                .explanation("Need help with Swagger-ui")
                                .solved(false)
                                .build();

                when(helpRequestRepository.save(eq(request))).thenReturn(request);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/HelpRequest/post?requesterEmail=cgaucho@ucsb.edu&teamId=s22-5pm-3&tableOrBreakoutRoom=7&requestTime=2022-04-20T17:35&explanation=Need help with Swagger-ui&solved=false")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestRepository, times(1)).save(request);
                String expectedJson = mapper.writeValueAsString(request);
                String responseString = response.getResponse().getContentAsString();
                System.out.println(expectedJson);
                System.out.println(responseString);
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_post_solved_true() throws Exception {
                LocalDateTime ldt = LocalDateTime.parse("2022-04-20T17:35");

                HelpRequest request = HelpRequest.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .teamId("s22-5pm-3")
                                .tableOrBreakoutRoom("7")
                                .requestTime(ldt)
                                .explanation("Need help with Swagger-ui")
                                .solved(true)
                                .build();

                when(helpRequestRepository.save(eq(request))).thenReturn(request);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/HelpRequest/post?requesterEmail=cgaucho@ucsb.edu&teamId=s22-5pm-3&tableOrBreakoutRoom=7&requestTime=2022-04-20T17:35&explanation=Need help with Swagger-ui&solved=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestRepository, times(1)).save(request);
                String expectedJson = mapper.writeValueAsString(request);
                String responseString = response.getResponse().getContentAsString();
                System.out.println(expectedJson);
                System.out.println(responseString);
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_helpRequest() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequest request1 = HelpRequest.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .teamId("s22-5pm-3")
                                .tableOrBreakoutRoom("7")
                                .requestTime(ldt1)
                                .explanation("Need help with Swagger-ui")
                                .solved(false)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-04-20T18:31");

                HelpRequest request2 = HelpRequest.builder()
                                .requesterEmail("ldelplaya@ucsb.edu")
                                .teamId("s22-6pm-3")
                                .tableOrBreakoutRoom("11")
                                .requestTime(ldt2)
                                .explanation("Heroku problems")
                                .solved(false)
                                .build();

                ArrayList<HelpRequest> expectedDates = new ArrayList<>();
                expectedDates.addAll(Arrays.asList(request1, request2));

                when(helpRequestRepository.findAll()).thenReturn(expectedDates);

                // act
                MvcResult response = mockMvc.perform(get("/api/HelpRequest/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(helpRequestRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedDates);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

}