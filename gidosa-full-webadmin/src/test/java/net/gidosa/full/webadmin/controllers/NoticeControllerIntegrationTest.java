package net.gidosa.full.webadmin.controllers;

import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.services.ConstructionService;
import net.gidosa.full.webadmin.services.NoticeService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoticeController.class)
public class NoticeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private ConstructionService constructionService;

    private Notice testNotice;
    private Construction testConstruction;
    private MemberAdmin testAdmin;
    private PrincipalDetails principalDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // 테스트 데이터 설정
        testConstruction = new Construction();
        testConstruction.setId(1L);
        testConstruction.setName("테스트 건설현장");

        testNotice = new Notice();
        testNotice.setId(1L);
        testNotice.setTitle("테스트 공지사항");
        testNotice.setContent("테스트 내용입니다.");
        testNotice.setNoticeDate(LocalDateTime.now());
        testNotice.setConstruction(testConstruction);

        testAdmin = new MemberAdmin();
        testAdmin.setId(1L);
        testAdmin.setUsername("admin");
        testAdmin.setName("관리자");
        testAdmin.setRole("ROLE_ADMIN");
        testAdmin.setConstruction(testConstruction);

        principalDetails = new PrincipalDetails(testAdmin);
    }

    @Test
    @DisplayName("관리자 권한으로 공지사항 목록 조회 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listNoticesAsAdminTest() throws Exception {
        // Given
        List<Notice> notices = new ArrayList<>();
        notices.add(testNotice);
        Page<Notice> noticePage = new PageImpl<>(notices);

        when(noticeService.getAllNotices(any(Pageable.class))).thenReturn(noticePage);

        // When & Then
        mockMvc.perform(get("/notice")
                .with(SecurityMockMvcRequestPostProcessors.user(principalDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/list"))
                .andExpect(model().attributeExists("notices"));
    }

    @Test
    @DisplayName("매니저 권한으로 공지사항 목록 조회 테스트")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void listNoticesAsManagerTest() throws Exception {
        // Given
        List<Notice> notices = new ArrayList<>();
        notices.add(testNotice);
        Page<Notice> noticePage = new PageImpl<>(notices);

        // 매니저 설정
        MemberAdmin manager = new MemberAdmin();
        manager.setId(2L);
        manager.setUsername("manager");
        manager.setName("매니저");
        manager.setRole("ROLE_MANAGER");
        manager.setConstruction(testConstruction);

        PrincipalDetails managerDetails = new PrincipalDetails(manager);

        when(noticeService.getNoticesByConstructionId(anyLong(), any(Pageable.class))).thenReturn(noticePage);

        // When & Then
        mockMvc.perform(get("/notice")
                .with(SecurityMockMvcRequestPostProcessors.user(managerDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/list"))
                .andExpect(model().attributeExists("notices"));
    }

    @Test
    @DisplayName("공지사항 상세 조회 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void viewNoticeTest() throws Exception {
        // Given
        when(noticeService.getNoticeById(anyLong())).thenReturn(Optional.of(testNotice));

        // When & Then
        mockMvc.perform(get("/notice/1")
                .with(SecurityMockMvcRequestPostProcessors.user(principalDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/view"))
                .andExpect(model().attributeExists("notice"));
    }

    @Test
    @DisplayName("공지사항 작성 폼 조회 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createFormTest() throws Exception {
        // Given
        when(constructionService.findAllConstructionsWithManagementMenus()).thenReturn(Collections.singletonList(testConstruction));

        // When & Then
        mockMvc.perform(get("/notice/create")
                .with(SecurityMockMvcRequestPostProcessors.user(principalDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/create"))
                .andExpect(model().attributeExists("constructions"));
    }

    @Test
    @DisplayName("공지사항 생성 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createNoticeTest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "files", "test.txt", "text/plain", "test content".getBytes());

        when(noticeService.createNotice(any(Notice.class), anyList(), anyLong())).thenReturn(testNotice);

        // When & Then
        mockMvc.perform(multipart("/notice/create")
                .file(file)
                .param("title", "테스트 공지사항")
                .param("content", "테스트 내용입니다.")
                .param("constructionId", "1")
                .with(SecurityMockMvcRequestPostProcessors.user(principalDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notice/1"));
    }

    @Test
    @DisplayName("공지사항 수정 폼 조회 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateFormTest() throws Exception {
        // Given
        when(noticeService.getNoticeById(anyLong())).thenReturn(Optional.of(testNotice));
        when(constructionService.findAllConstructionsWithManagementMenus()).thenReturn(Collections.singletonList(testConstruction));

        // When & Then
        mockMvc.perform(get("/notice/1/edit")
                .with(SecurityMockMvcRequestPostProcessors.user(principalDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/edit"))
                .andExpect(model().attributeExists("notice"))
                .andExpect(model().attributeExists("constructions"));
    }

    @Test
    @DisplayName("공지사항 삭제 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteNoticeTest() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/notice/1")
                .with(SecurityMockMvcRequestPostProcessors.user(principalDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notice"));
    }
} 