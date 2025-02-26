package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.NoticeService;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                      Model model) {
        Page<Notice> noticePage = noticeService.getAllNotices(pageable);
        model.addAttribute("notices", noticePage);
        return "main/notice/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "main/notice/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Notice notice, @RequestParam(value = "files", required = false) List<MultipartFile> files, RedirectAttributes redirectAttributes) {
        try {
            noticeService.createNotice(notice, files);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 등록 중 오류가 발생했습니다.");
            log.error("Error creating notice", e);
        }
        return "redirect:/notice";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        noticeService.getNoticeById(id)
                .ifPresent(notice -> model.addAttribute("notice", notice));
        return "main/notice/detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        noticeService.getNoticeById(id)
                .ifPresent(notice -> model.addAttribute("notice", notice));
        return "main/notice/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                        @ModelAttribute Notice notice,
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        RedirectAttributes redirectAttributes) {
        try {
            // 파일이 비어있는지 확인하고 필터링
            List<MultipartFile> validFiles = null;
            if (files != null && !files.isEmpty()) {
                validFiles = files.stream()
                                .filter(file -> !file.isEmpty())
                                .toList();
                // 모든 파일이 비어있다면 null로 설정
                if (validFiles.isEmpty()) {
                    validFiles = null;
                }
            }

            noticeService.updateNotice(id, notice, validFiles);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 수정 중 오류가 발생했습니다.");
            log.error("Error updating notice", e);
        }
        return "redirect:/notice/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 삭제 중 오류가 발생했습니다.");
            log.error("Error deleting notice", e);
        }
        return "redirect:/notice";
    }

    @PostMapping("/attachment/{noticeId}/{attachmentId}/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long noticeId, 
                                                 @PathVariable Long attachmentId) {
        try {
            noticeService.deleteAttachment(noticeId, attachmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("첨부파일 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
