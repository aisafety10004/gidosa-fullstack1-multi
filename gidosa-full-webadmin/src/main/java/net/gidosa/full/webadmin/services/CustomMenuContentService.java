package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType1;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType2;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType3;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType1Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType2Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType3Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomMenuContentService {
    
    private final CustomMenuContentType1Repository type1Repository;
    private final CustomMenuContentType2Repository type2Repository;
    private final CustomMenuContentType3Repository type3Repository;
    private final CustomMenuRepository customMenuRepository;
    
    /**
     * 특정 메뉴의 타입1 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType1 getType1ContentByMenuId(Long menuId) {
        return type1Repository.findByMenuId(menuId).orElse(null);
    }
    
    /**
     * 특정 메뉴와 날짜의 타입2 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType2 getType2ContentByMenuIdAndDate(Long menuId, LocalDate date) {
        return type2Repository.findByMenuIdAndContentDate(menuId, date).orElse(null);
    }
    
    /**
     * 특정 메뉴의 타입3 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType3 getType3ContentByMenuId(Long menuId) {
        return type3Repository.findByMenuId(menuId).orElse(null);
    }
    
    /**
     * 타입1 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType1 saveType1Content(Long menuId, String content) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 1) {
            throw new IllegalArgumentException("Menu is not type 1");
        }
        
        Optional<CustomMenuContentType1> existingContent = type1Repository.findByMenuId(menuId);
        
        CustomMenuContentType1 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setContent(content);
        } else {
            menuContent = CustomMenuContentType1.builder()
                    .menu(menu)
                    .content(content)
                    .build();
        }
        
        return type1Repository.save(menuContent);
    }
    
    /**
     * 타입2 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType2 saveType2Content(Long menuId, String content, LocalDate date) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 2) {
            throw new IllegalArgumentException("Menu is not type 2");
        }
        
        Optional<CustomMenuContentType2> existingContent = type2Repository.findByMenuIdAndContentDate(menuId, date);
        
        CustomMenuContentType2 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setContent(content);
        } else {
            menuContent = CustomMenuContentType2.builder()
                    .menu(menu)
                    .content(content)
                    .contentDate(date)
                    .build();
        }
        
        return type2Repository.save(menuContent);
    }
    
    /**
     * 타입3 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType3 saveType3Content(Long menuId, String content) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 3) {
            throw new IllegalArgumentException("Menu is not type 3");
        }
        
        Optional<CustomMenuContentType3> existingContent = type3Repository.findByMenuId(menuId);
        
        CustomMenuContentType3 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setContent(content);
        } else {
            menuContent = CustomMenuContentType3.builder()
                    .menu(menu)
                    .content(content)
                    .build();
        }
        
        return type3Repository.save(menuContent);
    }
    
    /**
     * 타입1 메뉴의 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType1Content(Long menuId) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 1) {
            throw new IllegalArgumentException("Menu is not type 1");
        }
        
        type1Repository.deleteByMenuId(menuId);
    }
    
    /**
     * 타입2 메뉴의 특정 날짜 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType2Content(Long menuId, LocalDate date) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 2) {
            throw new IllegalArgumentException("Menu is not type 2");
        }
        
        type2Repository.deleteByMenuIdAndContentDate(menuId, date);
    }
    
    /**
     * 타입3 메뉴의 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType3Content(Long menuId) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 3) {
            throw new IllegalArgumentException("Menu is not type 3");
        }
        
        type3Repository.deleteByMenuId(menuId);
    }
} 