package me.whiteship.demobootweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.Marshaller;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServlet;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SampleControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Marshaller marshaller;

    @Test
    public void jsonMessage() throws Exception {
        Person person = new Person();
        person.setId(2019L);
        person.setName("keesun");

        String jsonString = objectMapper.writeValueAsString(person);

        this.mockMvc.perform(get("/jsonMessage")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2019))
                .andExpect(jsonPath("$.name").value("keesun"))
        ;
    }

    @Test
    public void xmlMessage() throws Exception {
        Person person = new Person();
        person.setId(2019L);
        person.setName("keesun");

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        marshaller.marshal(person, result);
        String xmlString = stringWriter.toString();

        this.mockMvc.perform(get("/jsonMessage")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(xmlString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("person/id").string("2019"))
                .andExpect(xpath("person/name").string("keesun"))
        ;
    }

    @Test
    void hello() throws Exception {
        Person person = new Person();
        person.setName("keesun");
        Person savedPerson = personRepository.save(person);

        this.mockMvc.perform(get("/hello")
                        .param("id", savedPerson.getId().toString()))
                .andDo(print())
                .andExpect(content().string("hello keesun"));
    }

    @Test
    void helloStatic() throws Exception {
        this.mockMvc.perform(get("/mobile/index.html"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hello mobile")))
                .andExpect(header().exists(HttpHeaders.CACHE_CONTROL))
        ;
    }

    @Test
    void stringMessage() throws Exception {
        this.mockMvc.perform(get("/message")
                        .content("hello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

}
