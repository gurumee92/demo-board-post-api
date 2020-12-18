package com.gurumee.demoboardpostapi.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        IntStream.rangeClosed(1, 5)
                .mapToObj(i -> Post.builder()
                            .title("test title " + i)
                            .content("test content " + i)
                            .ownerName("test" + i)
                            .build()
                ).forEach(p -> repository.save(p));
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule()
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:MM:ss")))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:MM:ss"))));
    }

    @Test
    @DisplayName("GET /api/posts test")
    public void getPostListTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].owner_name").exists())
                .andExpect(jsonPath("$[0].created_at").exists())
                .andExpect(jsonPath("$[0].updated_at").exists())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        List<PostResponseDto> dtoList = objectMapper.readValue(res, objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponseDto.class));
        assertEquals(5, dtoList.size());

        for (int i=0; i<5; i++) {
            int no = i+1;
            var dto = dtoList.get(i);
            assertEquals("test title " + no, dto.getTitle());
            assertEquals("test content " + no, dto.getContent());
            assertEquals("test" + no, dto.getOwner_name());
        }
    }

    @Test
    @DisplayName("GET /api/posts?username=* test: exist username")
    public void getPostListTest_FindByUsername() throws Exception {
        int no = 4;
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(get("/api/posts?username=test" + no)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].owner_name").exists())
                .andExpect(jsonPath("$[0].created_at").exists())
                .andExpect(jsonPath("$[0].updated_at").exists())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        List<PostResponseDto> dtoList = objectMapper.readValue(res, objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponseDto.class));

        for (PostResponseDto dto : dtoList) {
            assertEquals("test title " + no, dto.getTitle());
            assertEquals("test content " + no, dto.getContent());
            assertEquals("test" + no, dto.getOwner_name());
        }
    }

    @Test
    @DisplayName("GET /api/posts?username=* test: empty list")
    public void getPostListTest_FindByUsername_empty_list() throws Exception {
        int no = 6;
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(get("/api/posts?username=test" + no)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        List<PostResponseDto> dtoList = objectMapper.readValue(res, objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponseDto.class));
        assertEquals(0, dtoList.size());
    }

    @Test
    @DisplayName("GET /api/posts/search test")
    public void getPostListSearchTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/posts/search")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].owner_name").exists())
                .andExpect(jsonPath("$[0].created_at").exists())
                .andExpect(jsonPath("$[0].updated_at").exists())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        List<PostResponseDto> dtoList = objectMapper.readValue(res, objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponseDto.class));
        assertEquals(5, dtoList.size());

        for (int i=0; i<5; i++) {
            int no = i+1;
            var dto = dtoList.get(i);
            assertEquals("test title " + no, dto.getTitle());
            assertEquals("test content " + no, dto.getContent());
            assertEquals("test" + no, dto.getOwner_name());
        }
    }

    @Test
    @DisplayName("GET /api/posts/search?keyword=* test: exist keyword")
    public void getPostListSearchTest_FindByKeyword() throws Exception {
        int no = 4;
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(get("/api/posts/search?keyword=nt " + no)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[0].owner_name").exists())
                .andExpect(jsonPath("$[0].created_at").exists())
                .andExpect(jsonPath("$[0].updated_at").exists())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        List<PostResponseDto> dtoList = objectMapper.readValue(res, objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponseDto.class));

        for (PostResponseDto dto : dtoList) {
            assertEquals("test title " + no, dto.getTitle());
            assertEquals("test content " + no, dto.getContent());
            assertEquals("test" + no, dto.getOwner_name());
        }
    }

    @Test
    @DisplayName("GET /api/posts/search?keyword=* test: empty list")
    public void getPostListSearchTest_FindByUsername_empty_list() throws Exception {
        int no = 6;
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(get("/api/posts/search?keyword=nt " + no)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        List<PostResponseDto> dtoList = objectMapper.readValue(res, objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponseDto.class));
        assertEquals(0, dtoList.size());
    }

    @Test
    @DisplayName("GET /api/posts/:id test")
    public void getPostTest() throws Exception {
        List<Post> all = repository.findAll();
        Post post = all.get(0);
        mockMvc.perform(get("/api/posts/"+post.getId())
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(post.getId()))
                .andExpect(jsonPath("title").value(post.getTitle()))
                .andExpect(jsonPath("content").value(post.getContent()))
                .andExpect(jsonPath("owner_name").value(post.getOwnerName()))
                .andExpect(jsonPath("created_at").value(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:MM:ss"))))
                .andExpect(jsonPath("updated_at").value(post.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:MM:ss"))))
                ;
    }
    @Test
    @DisplayName("GET /api/posts/:id test 실패: 존재하지 않는 postID")
    public void getPostTestFailed_not_exist_post_id() throws Exception {
        List<Post> all = repository.findAll();
        Post post = all.get(all.size()-1);
        long id = post.getId() + 1;
        mockMvc.perform(get("/api/posts/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Post ID: " + id + " is not exist."))
        ;
    }

    //create test
    //update test
    //delete test
}