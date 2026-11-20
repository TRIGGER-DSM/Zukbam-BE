package com.example.zukbambe.global.swagger.auth;

import com.example.zukbambe.global.judge.JudgeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "swagger.auth.username=docs",
    "swagger.auth.password=docs-password"
})
@AutoConfigureMockMvc
class SwaggerLoginTest {

    @MockitoBean
    private JudgeProvider judgeProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 로그인하지_않으면_문서를_볼_수_없다() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, startsWith("Basic realm=\"Zukbam API Docs\"")));
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/v3/api-docs/swagger-config"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 계정이_틀리면_문서를_볼_수_없다() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("docs", "wrong")))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("wrong", "docs-password")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 계정이_맞으면_문서를_볼_수_있다() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(httpBasic("docs", "docs-password")))
            .andExpect(status().isOk());
        mockMvc.perform(get("/swagger-ui/index.html").with(httpBasic("docs", "docs-password")))
            .andExpect(status().isOk());
    }

    @Test
    void 문서_계정으로는_API를_쓸_수_없고_공개_API는_그대로_열려_있다() throws Exception {
        mockMvc.perform(get("/users/me").with(httpBasic("docs", "docs-password")))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/problem-ranks"))
            .andExpect(status().isOk());
    }
}
