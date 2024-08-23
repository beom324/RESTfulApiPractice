package com.example.inflearnrestapi.events;

import com.example.inflearnrestapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    EventRepository eventRepository;

    @Test
    @TestDescription("테스트를 정상적으로 종료")
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024,8,21,13,50))
                .closeEnrollmentDateTime(LocalDateTime.of(2024,8,22,13,50))
                .beginEventDateTime(LocalDateTime.of(2024,8,23,13,50))
                .endEventDateTime(LocalDateTime.of(2024,8,24,13,50))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(200)
                .location("강남역 D2 스사텁 팩토리")
                .free(true)
                .offline(false)
                .build();



        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))  //perform 안에 주는게 요청
                .andDo(print())
                .andExpect(status   ().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)));

    }
    @Test
    public void bad_RequestcreateEvent() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024,8,21,13,50))
                .closeEnrollmentDateTime(LocalDateTime.of(2024,8,22,13,50))
                .beginEventDateTime(LocalDateTime.of(2024,8,23,13,50))
                .endEventDateTime(LocalDateTime.of(2024,8,24,13,50))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(200)
                .location("강남역 D2 스사텁 팩토리")
                .free(true)
                .offline(false)
                .build();



        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))  //perform 안에 주는게 요청
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024,8,21,13,50))
                .closeEnrollmentDateTime(LocalDateTime.of(2024,8,22,13,50))
                .beginEventDateTime(LocalDateTime.of(2024,8,25,13,50))
                .endEventDateTime(LocalDateTime.of(2024,8,24,13,50))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(200)
                .location("강남역 D2 스사텁 팩토리")
                .build();


        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                        .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists());
    }
}