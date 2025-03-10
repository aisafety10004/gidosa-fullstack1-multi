package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.TestConfig;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
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
public class MemberAdminJpaRepositoryTest {

    @Autowired
    private MemberAdminJpaRepository memberAdminRepository;

    @Autowired
    private ConstructionJpaRepository constructionRepository;

    @Test
    @DisplayName("관리자 회원 저장 및 조회 테스트")
    public void saveAndFindMemberAdminTest() {
        // Given
        MemberAdmin memberAdmin = new MemberAdmin();
        memberAdmin.setUsername("testadmin");
        memberAdmin.setPassword("hashedpassword123"); // 실제 테스트에서는 암호화된 비밀번호 사용
        memberAdmin.setName("테스트 관리자");
        memberAdmin.setEmail("test@example.com");
        memberAdmin.setPhone("010-1234-5678");
        memberAdmin.setRole("ROLE_ADMIN");

        // When
        MemberAdmin savedMemberAdmin = memberAdminRepository.save(memberAdmin);

        // Then
        assertThat(savedMemberAdmin.getId()).isNotNull();
        assertThat(savedMemberAdmin.getUsername()).isEqualTo("testadmin");
        assertThat(savedMemberAdmin.getName()).isEqualTo("테스트 관리자");
        assertThat(savedMemberAdmin.getPassword()).isEqualTo("hashedpassword123");
    }

    @Test
    @DisplayName("사용자명으로 관리자 회원 조회 테스트")
    public void findByUsernameTest() {
        // Given
        MemberAdmin memberAdmin = new MemberAdmin();
        memberAdmin.setUsername("testadmin");
        memberAdmin.setPassword("hashedpassword123");
        memberAdmin.setName("테스트 관리자");
        memberAdmin.setEmail("test@example.com");
        memberAdmin.setRole("ROLE_ADMIN");
        memberAdminRepository.save(memberAdmin);

        // When
        Optional<MemberAdmin> foundMemberAdmin = memberAdminRepository.findByUsername("testadmin");

        // Then
        assertThat(foundMemberAdmin).isPresent();
        assertThat(foundMemberAdmin.get().getUsername()).isEqualTo("testadmin");
        assertThat(foundMemberAdmin.get().getName()).isEqualTo("테스트 관리자");
    }

    @Test
    @DisplayName("건설현장별 관리자 회원 조회 테스트")
    public void findByConstructionIdTest() {
        // Given
        Construction construction = new Construction();
        construction.setName("테스트 건설현장");
        Construction savedConstruction = constructionRepository.save(construction);

        MemberAdmin memberAdmin = new MemberAdmin();
        memberAdmin.setUsername("testmanager");
        memberAdmin.setPassword("hashedpassword123");
        memberAdmin.setName("테스트 매니저");
        memberAdmin.setEmail("manager@example.com");
        memberAdmin.setRole("ROLE_MANAGER");
        memberAdmin.setConstruction(savedConstruction);
        memberAdmin.setActive(true);
        memberAdminRepository.save(memberAdmin);

        // When
        List<MemberAdmin> managers = memberAdminRepository.findByConstructionIdAndIsActiveTrue(savedConstruction.getId());

        // Then
        assertThat(managers.size()).isEqualTo(1);
        assertThat(managers.get(0).getUsername()).isEqualTo("testmanager");
        assertThat(managers.get(0).getRole()).isEqualTo("ROLE_MANAGER");
        assertThat(managers.get(0).getConstruction().getId()).isEqualTo(savedConstruction.getId());
    }

    @Test
    @DisplayName("관리자 회원 페이징 조회 테스트")
    public void findAllByOrderByIdDescTest() {
        // Given
        MemberAdmin admin = new MemberAdmin();
        admin.setUsername("testadmin");
        admin.setPassword("hashedpassword123");
        admin.setName("테스트 관리자");
        admin.setEmail("admin@example.com");
        admin.setRole("ROLE_ADMIN");
        memberAdminRepository.save(admin);

        MemberAdmin manager = new MemberAdmin();
        manager.setUsername("testmanager");
        manager.setPassword("hashedpassword123");
        manager.setName("테스트 매니저");
        manager.setEmail("manager@example.com");
        manager.setRole("ROLE_MANAGER");
        memberAdminRepository.save(manager);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<MemberAdmin> members = memberAdminRepository.findAllByOrderByIdDesc(pageable);

        // Then
        assertThat(members.getTotalElements()).isGreaterThanOrEqualTo(2);
        // 최신 등록 순으로 정렬되므로 첫 번째는 manager, 두 번째는 admin이어야 함
        assertThat(members.getContent().get(0).getUsername()).isEqualTo("testmanager");
        assertThat(members.getContent().get(1).getUsername()).isEqualTo("testadmin");
    }
} 