package net.gidosa.full.webadmin.services;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberGeneralService {
    private final MemberGeneralJpaRepository memberGeneralJpaRepository;
    private final PasswordEncoder passwordEncoder;

    // 전체 회원 조회
    public Page<MemberGeneral> getAllMembersWithPaging(Pageable pageable) {
        return memberGeneralJpaRepository.findAllByOrderByIdDesc(pageable);
    }

    // ID로 회원 조회
    public Optional<MemberGeneral> getMemberById(Long id) {
        return memberGeneralJpaRepository.findByIdWithAttachments(id);
    }

    // 사용자명으로 회원 조회
    public Optional<MemberGeneral> getMemberByUsername(String username) {
        return memberGeneralJpaRepository.findByUsername(username);
    }

    // 이메일로 회원 조회
    public Optional<MemberGeneral> getMemberByEmail(String email) {
        return memberGeneralJpaRepository.findByEmail(email);
    }

    // 회원 정보 수정
    @Transactional
    public MemberGeneral updateMember(Long id, MemberGeneral memberDetails) {
        return memberGeneralJpaRepository.findById(id)
            .map(member -> {
                // 수정 가능한 필드 업데이트
                member.setName(memberDetails.getName());
                member.setPhone(memberDetails.getPhone());
                member.setEmail(memberDetails.getEmail());
                
                // 추가 필드 업데이트
                member.setPosition(memberDetails.getPosition());
                member.setJobType(memberDetails.getJobType());
                member.setEmergencyContact(memberDetails.getEmergencyContact());
                member.setEtcDoc1Description(memberDetails.getEtcDoc1Description());
                member.setEtcDoc2Description(memberDetails.getEtcDoc2Description());
                member.setEtcDoc3Description(memberDetails.getEtcDoc3Description());
                
                // 파일 관련 필드 업데이트
                if (memberDetails.getProfilePhoto() != null) {
                    member.setProfilePhoto(memberDetails.getProfilePhoto());
                }
                if (memberDetails.getLaborContract() != null) {
                    member.setLaborContract(memberDetails.getLaborContract());
                }
                if (memberDetails.getSafetyEducationCert() != null) {
                    member.setSafetyEducationCert(memberDetails.getSafetyEducationCert());
                }
                if (memberDetails.getProtectiveGearPledge() != null) {
                    member.setProtectiveGearPledge(memberDetails.getProtectiveGearPledge());
                }
                if (memberDetails.getEtcDoc1() != null) {
                    member.setEtcDoc1(memberDetails.getEtcDoc1());
                }
                if (memberDetails.getEtcDoc2() != null) {
                    member.setEtcDoc2(memberDetails.getEtcDoc2());
                }
                if (memberDetails.getEtcDoc3() != null) {
                    member.setEtcDoc3(memberDetails.getEtcDoc3());
                }
                
                // 비밀번호는 별도의 암호화 처리가 필요할 수 있음
                if (memberDetails.getPassword() != null && !memberDetails.getPassword().isEmpty()) {
                    member.setPassword(passwordEncoder.encode(memberDetails.getPassword()));
                }
                return memberGeneralJpaRepository.save(member);
            })
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id) {
        memberGeneralJpaRepository.deleteById(id);
    }

    // 회원 존재 여부 확인 (사용자명)
    public boolean existsByUsername(String username) {
        return memberGeneralJpaRepository.existsByUsername(username);
    }

    // 회원 존재 여부 확인 (이메일)
    public boolean existsByEmail(String email) {
        return memberGeneralJpaRepository.existsByEmail(email);
    }

    public Page<MemberGeneral> getMembersByConstructionId(Long constructionId, Pageable pageable) {
        return memberGeneralJpaRepository.findByConstructionId(constructionId, pageable);
    }
    
    // 검색 조건에 따른 회원 검색
    public Page<MemberGeneral> searchMembers(Long constructionId, String searchType, String searchKeyword,
                                            String startDate, String endDate, Pageable pageable) {
        // 날짜 검색 조건이 있는 경우
        if ((startDate != null && !startDate.isEmpty()) || (endDate != null && !endDate.isEmpty())) {
            return searchMembersWithDateAndType(constructionId, searchType, searchKeyword, startDate, endDate, pageable);
        }
        
        // 검색 타입에 따른 검색
        if (Strings.isNullOrEmpty(searchType)) {
            return memberGeneralJpaRepository.findByConstructionId(constructionId, pageable);
        }
        
        switch (searchType) {
            case "username":
                return memberGeneralJpaRepository.findByConstructionIdAndUsernameContaining(
                        constructionId, searchKeyword, pageable);
            case "name":
                return memberGeneralJpaRepository.findByConstructionIdAndNameContaining(
                    constructionId, searchKeyword, pageable);
            case "email":
                return memberGeneralJpaRepository.findByConstructionIdAndEmailContaining(
                    constructionId, searchKeyword, pageable);
            case "phone":
                return memberGeneralJpaRepository.findByConstructionIdAndPhoneContaining(
                    constructionId, searchKeyword, pageable);
            default:
                return memberGeneralJpaRepository.findByConstructionId(constructionId, pageable);
        }
    }
    
    // 날짜 검색
    public Page<MemberGeneral> searchMembersByDate(Long constructionId, String startDate, String endDate, Pageable pageable) {
        try {
            java.time.LocalDateTime start = null;
            java.time.LocalDateTime end = null;
            
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    start = java.time.LocalDate.parse(startDate).atStartOfDay();
                } catch (Exception e) {
                    log.error("Error parsing start date: " + startDate, e);
                }
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
                } catch (Exception e) {
                    log.error("Error parsing end date: " + endDate, e);
                }
            }
            
            if (start != null && end != null) {
                return memberGeneralJpaRepository.findByConstructionIdAndCreatedAtBetween(
                    constructionId, start, end, pageable);
            } else if (start != null) {
                return memberGeneralJpaRepository.findByConstructionIdAndCreatedAtGreaterThanEqual(
                    constructionId, start, pageable);
            } else if (end != null) {
                return memberGeneralJpaRepository.findByConstructionIdAndCreatedAtLessThanEqual(
                    constructionId, end, pageable);
            }
        } catch (Exception e) {
            log.error("Date search error", e);
        }
        
        return memberGeneralJpaRepository.findByConstructionId(constructionId, pageable);
    }
    
    // 검색 타입과 날짜 조건을 모두 적용한 검색
    private Page<MemberGeneral> searchMembersWithDateAndType(Long constructionId, String searchType, 
                                                           String searchKeyword, String startDate, 
                                                           String endDate, Pageable pageable) {
        try {
            java.time.LocalDateTime start = null;
            java.time.LocalDateTime end = null;
            
            if (startDate != null && !startDate.isEmpty()) {
                start = java.time.LocalDate.parse(startDate).atStartOfDay();
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
            }
            
            // searchType이 null이거나 비어있는 경우 날짜 검색만 수행
            if (searchType == null || searchType.isEmpty() || searchKeyword == null || searchKeyword.isEmpty()) {
                if (start != null && end != null) {
                    return memberGeneralJpaRepository.findByConstructionIdAndCreatedAtBetween(
                        constructionId, start, end, pageable);
                } else if (start != null) {
                    return memberGeneralJpaRepository.findByConstructionIdAndCreatedAtGreaterThanEqual(
                        constructionId, start, pageable);
                } else if (end != null) {
                    return memberGeneralJpaRepository.findByConstructionIdAndCreatedAtLessThanEqual(
                        constructionId, end, pageable);
                }
                return memberGeneralJpaRepository.findByConstructionId(constructionId, pageable);
            }
            
            // 검색 타입과 날짜 조건을 모두 적용
            switch (searchType) {
                case "username":
                    if (start != null && end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndUsernameContainingAndCreatedAtBetween(
                            constructionId, searchKeyword, start, end, pageable);
                    } else if (start != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndUsernameContainingAndCreatedAtGreaterThanEqual(
                            constructionId, searchKeyword, start, pageable);
                    } else if (end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndUsernameContainingAndCreatedAtLessThanEqual(
                            constructionId, searchKeyword, end, pageable);
                    }
                    break;
                case "name":
                    if (start != null && end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndNameContainingAndCreatedAtBetween(
                            constructionId, searchKeyword, start, end, pageable);
                    } else if (start != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndNameContainingAndCreatedAtGreaterThanEqual(
                            constructionId, searchKeyword, start, pageable);
                    } else if (end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndNameContainingAndCreatedAtLessThanEqual(
                            constructionId, searchKeyword, end, pageable);
                    }
                    break;
                case "email":
                    if (start != null && end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndEmailContainingAndCreatedAtBetween(
                            constructionId, searchKeyword, start, end, pageable);
                    } else if (start != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndEmailContainingAndCreatedAtGreaterThanEqual(
                            constructionId, searchKeyword, start, pageable);
                    } else if (end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndEmailContainingAndCreatedAtLessThanEqual(
                            constructionId, searchKeyword, end, pageable);
                    }
                    break;
                case "phone":
                    if (start != null && end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndPhoneContainingAndCreatedAtBetween(
                            constructionId, searchKeyword, start, end, pageable);
                    } else if (start != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndPhoneContainingAndCreatedAtGreaterThanEqual(
                            constructionId, searchKeyword, start, pageable);
                    } else if (end != null) {
                        return memberGeneralJpaRepository.findByConstructionIdAndPhoneContainingAndCreatedAtLessThanEqual(
                            constructionId, searchKeyword, end, pageable);
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("Date parsing error", e);
        }
        
        // 기본 검색 결과 반환
        return searchMembers(constructionId, searchType, searchKeyword, null, null, pageable);
    }
}
