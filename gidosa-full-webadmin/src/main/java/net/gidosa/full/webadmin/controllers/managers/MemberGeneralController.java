package net.gidosa.full.webadmin.controllers.managers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.services.FileAttachmentService;
import net.gidosa.full.webadmin.services.MemberGeneralService;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.web.PageableDefault;
import java.util.Optional;

/**
 * 일반 회원 관리 컨트롤러
 */
@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/member/general")
public class MemberGeneralController {

    private final MemberGeneralService memberGeneralService;
    private final FileAttachmentService fileAttachmentService;

    // 회원 목록 조회 - 페이징 처리 추가
    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(required = false) String searchKeyword,
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        Long constructionId = principalDetails.getMemberAdmin().getConstruction().getId();
        Page<MemberGeneral> membersPage;
        
        try {
            // 검색 조건이 있는 경우
            if (searchType != null && !searchType.isEmpty() && searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                membersPage = memberGeneralService.searchMembers(constructionId, searchType, searchKeyword, startDate, endDate, pageable);
            } else if ((startDate != null && !startDate.isEmpty()) || (endDate != null && !endDate.isEmpty())) {
                // 날짜 검색만 있는 경우
                membersPage = memberGeneralService.searchMembersByDate(constructionId, startDate, endDate, pageable);
            } else {
                // 검색 조건이 없는 경우 전체 목록 조회
                membersPage = memberGeneralService.getMembersByConstructionId(constructionId, pageable);
            }
        } catch (Exception e) {
            log.error("Error during member search", e);
            // 오류 발생 시 기본 목록 조회
            membersPage = memberGeneralService.getMembersByConstructionId(constructionId, pageable);
            model.addAttribute("error", "검색 중 오류가 발생했습니다. 기본 목록을 표시합니다.");
        }
        
        model.addAttribute("members", membersPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "main/member/general/list";
    }

    // 회원 상세 조회
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<MemberGeneral> memberOptional = memberGeneralService.getMemberById(id);
            
            if (memberOptional.isPresent()) {
                model.addAttribute("member", memberOptional.get());
                return "main/member/general/detail";
            } else {
                redirectAttributes.addFlashAttribute("error", "회원 정보를 찾을 수 없습니다.");
                return "redirect:/member/general/list";
            }
        } catch (Exception e) {
            log.error("회원 상세 정보 조회 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "회원 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/member/general/list";
        }
    }

    // 회원 정보 수정 폼
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<MemberGeneral> memberOptional = memberGeneralService.getMemberById(id);
            
            if (memberOptional.isPresent()) {
                model.addAttribute("member", memberOptional.get());
                return "main/member/general/update";
            } else {
                redirectAttributes.addFlashAttribute("error", "회원 정보를 찾을 수 없습니다.");
                return "redirect:/member/general/list";
            }
        } catch (Exception e) {
            log.error("회원 정보 수정 폼 로드 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "회원 정보 로드 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/member/general/list";
        }
    }

    // 회원 정보 수정 처리 - 파일 업로드 추가
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                        @ModelAttribute MemberGeneral memberDetails,
                        @RequestParam(value = "password", required = false) String password,
                        @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile,
                        @RequestParam(value = "laborContractFile", required = false) MultipartFile laborContractFile,
                        @RequestParam(value = "safetyEducationCertFile", required = false) MultipartFile safetyEducationCertFile,
                        @RequestParam(value = "protectiveGearPledgeFile", required = false) MultipartFile protectiveGearPledgeFile,
                        @RequestParam(value = "etcDoc1File", required = false) MultipartFile etcDoc1File,
                        @RequestParam(value = "etcDoc2File", required = false) MultipartFile etcDoc2File,
                        @RequestParam(value = "etcDoc3File", required = false) MultipartFile etcDoc3File,
                        RedirectAttributes redirectAttributes) {
        try {
            // 기존 회원 정보 조회
            MemberGeneral existingMember = memberGeneralService.getMemberById(id)
                    .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
            
            // 기본 정보 업데이트
            memberDetails.setId(id); // ID 설정
            memberDetails.setUsername(existingMember.getUsername()); // 사용자명은 변경 불가
            memberDetails.setConstruction(existingMember.getConstruction()); // 소속 공사 정보 유지
            memberDetails.setRole(existingMember.getRole()); // 권한 정보 유지
            memberDetails.setCreatedAt(existingMember.getCreatedAt()); // 생성일 유지
            
            // 기존 파일 정보 유지
            memberDetails.setProfilePhoto(existingMember.getProfilePhoto());
            memberDetails.setLaborContract(existingMember.getLaborContract());
            memberDetails.setSafetyEducationCert(existingMember.getSafetyEducationCert());
            memberDetails.setProtectiveGearPledge(existingMember.getProtectiveGearPledge());
            memberDetails.setEtcDoc1(existingMember.getEtcDoc1());
            memberDetails.setEtcDoc2(existingMember.getEtcDoc2());
            memberDetails.setEtcDoc3(existingMember.getEtcDoc3());
            
            // 비밀번호가 있을 경우에만 업데이트
            if (password != null && !password.isEmpty()) {
                memberDetails.setPassword(password);
            } else {
                // 비밀번호 변경이 없는 경우 기존 비밀번호 유지
                memberDetails.setPassword(null);
            }
            
            // 파일 업로드 처리
            handleFileUpload(memberDetails, profilePhotoFile, laborContractFile, safetyEducationCertFile, 
                           protectiveGearPledgeFile, etcDoc1File, etcDoc2File, etcDoc3File);
            
            // 회원 정보 업데이트
            memberGeneralService.updateMember(id, memberDetails);
            
            redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            log.error("Error updating member", e);
        }
        return "redirect:/member/general/detail/" + id;
    }

    // 파일 업로드 처리 메소드
    private void handleFileUpload(MemberGeneral member, 
                                MultipartFile profilePhotoFile,
                                MultipartFile laborContractFile,
                                MultipartFile safetyEducationCertFile,
                                MultipartFile protectiveGearPledgeFile,
                                MultipartFile etcDoc1File,
                                MultipartFile etcDoc2File,
                                MultipartFile etcDoc3File) {
        try {
            // 프로필 사진 처리
            if (profilePhotoFile != null && !profilePhotoFile.isEmpty()) {
                FileAttachment profilePhoto = fileAttachmentService.saveFile(profilePhotoFile, "member_profile");
                member.setProfilePhoto(profilePhoto);
            }
            
            // 근로계약서 처리
            if (laborContractFile != null && !laborContractFile.isEmpty()) {
                FileAttachment laborContract = fileAttachmentService.saveFile(laborContractFile, "labor_contract");
                member.setLaborContract(laborContract);
            }
            
            // 건설업기초안전보건교육이수증 처리
            if (safetyEducationCertFile != null && !safetyEducationCertFile.isEmpty()) {
                FileAttachment safetyEducationCert = fileAttachmentService.saveFile(safetyEducationCertFile, "safety_education_cert");
                member.setSafetyEducationCert(safetyEducationCert);
            }
            
            // 보호구착용서약서 처리
            if (protectiveGearPledgeFile != null && !protectiveGearPledgeFile.isEmpty()) {
                FileAttachment protectiveGearPledge = fileAttachmentService.saveFile(protectiveGearPledgeFile, "protective_gear_pledge");
                member.setProtectiveGearPledge(protectiveGearPledge);
            }
            
            // 기타문서1 처리
            if (etcDoc1File != null && !etcDoc1File.isEmpty()) {
                FileAttachment etcDoc1 = fileAttachmentService.saveFile(etcDoc1File, "etc_doc1");
                member.setEtcDoc1(etcDoc1);
            }
            
            // 기타문서2 처리
            if (etcDoc2File != null && !etcDoc2File.isEmpty()) {
                FileAttachment etcDoc2 = fileAttachmentService.saveFile(etcDoc2File, "etc_doc2");
                member.setEtcDoc2(etcDoc2);
            }
            
            // 기타문서3 처리
            if (etcDoc3File != null && !etcDoc3File.isEmpty()) {
                FileAttachment etcDoc3 = fileAttachmentService.saveFile(etcDoc3File, "etc_doc3");
                member.setEtcDoc3(etcDoc3);
            }
        } catch (Exception e) {
            log.error("파일 업로드 처리 중 오류 발생", e);
            throw new RuntimeException("파일 업로드 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 회원 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberGeneralService.deleteMember(id);
            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 삭제 중 오류가 발생했습니다.");
            log.error("Error deleting member", e);
        }
        return "redirect:/member/general/list";
    }
}
