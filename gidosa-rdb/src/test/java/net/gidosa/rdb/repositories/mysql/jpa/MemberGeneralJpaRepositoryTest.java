package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.TestConfig;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class MemberGeneralJpaRepositoryTest {

    @Autowired
    private MemberGeneralJpaRepository memberGeneralRepository;

    @Autowired
    private ConstructionJpaRepository constructionRepository;

    private Construction construction;

    @BeforeEach
    public void setup() {
        // 테스트용 건설현장 생성
        construction = new Construction();
        construction.setName("테스트 건설현장");
        construction.setLocation("서울시 강남구");
        construction = constructionRepository.save(construction);

        // 테스트용 회원 데이터 생성
        createTestMember("user1", "사용자1", "user1@example.com", "010-1111-1111");
        createTestMember("user2", "사용자2", "user2@example.com", "010-2222-2222");
        createTestMember("admin1", "관리자1", "admin1@example.com", "010-3333-3333");
        createTestMember("test_user", "테스트유저", "test@example.com", "010-4444-4444");
        createTestMember("another_user", "다른유저", "another@example.com", "010-5555-5555");
    }

    private MemberGeneral createTestMember(String username, String name, String email, String phone) {
        MemberGeneral member = new MemberGeneral();
        member.setUsername(username);
        member.setPassword("hashedpassword123");
        member.setName(name);
        member.setEmail(email);
        member.setPhone(phone);
        member.setRole("ROLE_USER");
        member.setConstruction(construction);
        return memberGeneralRepository.save(member);
    }

//    @Test
//    @DisplayName("건설현장 ID와 사용자명 일부로 회원 검색 테스트")
//    public void findByConstructionIdAndUsernameContainingTest() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
//
//        // When - "user"가 포함된 사용자명 검색
//        Page<MemberGeneral> usersWithUser = memberGeneralRepository.findByConstructionIdAndUsernameContaining(
//                construction.getId(), "user", pageable);
//
//        // Then
//        assertThat(usersWithUser.getTotalElements()).isEqualTo(3); // user1, user2, test_user
//        assertThat(usersWithUser.getContent())
//                .extracting(MemberGeneral::getUsername)
//                .containsExactlyInAnyOrder("user1", "user2", "test_user");
//
//        // When - "admin"이 포함된 사용자명 검색
//        Page<MemberGeneral> usersWithAdmin = memberGeneralRepository.findByConstructionIdAndUsernameContaining(
//                construction.getId(), "admin", pageable);
//
//        // Then
//        assertThat(usersWithAdmin.getTotalElements()).isEqualTo(1); // admin1
//        assertThat(usersWithAdmin.getContent().get(0).getUsername()).isEqualTo("admin1");
//
//        // When - 존재하지 않는 사용자명 검색
//        Page<MemberGeneral> usersWithNonExistent = memberGeneralRepository.findByConstructionIdAndUsernameContaining(
//                construction.getId(), "nonexistent", pageable);
//
//        // Then
//        assertThat(usersWithNonExistent.getTotalElements()).isEqualTo(0);
//        assertThat(usersWithNonExistent.getContent()).isEmpty();
//    }
    
//    @Test
//    @DisplayName("페이징 처리된 건설현장 ID와 사용자명 검색 테스트")
//    public void findByConstructionIdAndUsernameContainingWithPagingTest() {
//        // Given
//        // 추가 테스트 데이터 생성
//        createTestMember("user3", "사용자3", "user3@example.com", "010-6666-6666");
//        createTestMember("user4", "사용자4", "user4@example.com", "010-7777-7777");
//        createTestMember("user5", "사용자5", "user5@example.com", "010-8888-8888");
//
//        // 첫 번째 페이지 (2개 항목)
//        Pageable firstPageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "username"));
//
//        // When
//        Page<MemberGeneral> firstPage = memberGeneralRepository.findByConstructionIdAndUsernameContaining(
//                construction.getId(), "user", firstPageable);
//
//        // Then
//        assertThat(firstPage.getTotalElements()).isEqualTo(5); // user1, user2, user3, user4, user5
//        assertThat(firstPage.getTotalPages()).isEqualTo(3); // 5개 항목을 2개씩 페이징하면 3페이지
//        assertThat(firstPage.getNumber()).isEqualTo(0); // 현재 페이지 번호
//        assertThat(firstPage.getSize()).isEqualTo(2); // 페이지 크기
//        assertThat(firstPage.getContent().size()).isEqualTo(2); // 현재 페이지의 항목 수
//
//        // 사용자명 오름차순 정렬이므로 user1, user2가 나와야 함
//        assertThat(firstPage.getContent())
//                .extracting(MemberGeneral::getUsername)
//                .containsExactly("user1", "user2");
//
//        // 두 번째 페이지
//        Pageable secondPageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "username"));
//
//        // When
//        Page<MemberGeneral> secondPage = memberGeneralRepository.findByConstructionIdAndUsernameContaining(
//                construction.getId(), "user", secondPageable);
//
//        // Then
//        assertThat(secondPage.getNumber()).isEqualTo(1); // 현재 페이지 번호
//        assertThat(secondPage.getContent().size()).isEqualTo(2); // 현재 페이지의 항목 수
//
//        // 사용자명 오름차순 정렬이므로 user3, user4가 나와야 함
//        assertThat(secondPage.getContent())
//                .extracting(MemberGeneral::getUsername)
//                .containsExactly("user3", "user4");
//    }
    
//    @Test
//    @DisplayName("대소문자 구분 없이 사용자명 검색 테스트")
//    public void findByConstructionIdAndUsernameContainingCaseInsensitiveTest() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // 대문자로 검색
//        Page<MemberGeneral> usersWithUpperCase = memberGeneralRepository.findByConstructionIdAndUsernameContaining(
//                construction.getId(), "USER", pageable);
//
//        // Then - JPA의 기본 동작은 대소문자를 구분하지만, 데이터베이스 설정에 따라 다를 수 있음
//        // MySQL은 기본적으로 대소문자를 구분하지 않으므로, 결과가 나올 수 있음
//        // 이 테스트는 실제 데이터베이스 설정에 따라 결과가 달라질 수 있음
//        if (usersWithUpperCase.getTotalElements() > 0) {
//            assertThat(usersWithUpperCase.getContent())
//                    .extracting(MemberGeneral::getUsername)
//                    .containsAnyOf("user1", "user2", "test_user");
//        }
//    }
} 