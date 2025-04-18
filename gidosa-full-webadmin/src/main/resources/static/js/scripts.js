/*!
* Start Bootstrap - Simple Sidebar v6.0.6 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
// 사이드바 토글 스크립트
// Scripts

window.addEventListener('DOMContentLoaded', event => {
    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

    // 마지막으로 선택한 메뉴 기억 및 복원 기능
    // 현재 페이지 타입 감지 (admin 또는 manager)
    const pageType = document.title.includes('어드민') ? 'admin' : 'manager';
    const storageKey = `lastOpenedMenu_${pageType}`;
    
    // 1. 모든 메뉴 토글 버튼에 이벤트 리스너 추가
    const menuToggles = document.querySelectorAll('[data-bs-toggle="collapse"]');
    menuToggles.forEach(toggle => {
        // 메뉴 클릭 시 localStorage에 상태 저장
        toggle.addEventListener('click', () => {
            const targetId = toggle.getAttribute('href');
            if (targetId && targetId.startsWith('#')) {
                localStorage.setItem(storageKey, targetId.substring(1));
                
                // 화살표 아이콘 회전
                const icon = toggle.querySelector('.bi-chevron-down');
                if (icon) {
                    icon.classList.toggle('rotate-icon');
                }
            }
        });
        
        // 부트스트랩 collapse 이벤트 리스너 추가
        const targetId = toggle.getAttribute('href');
        if (targetId && targetId.startsWith('#')) {
            const targetEl = document.querySelector(targetId);
            if (targetEl) {
                targetEl.addEventListener('show.bs.collapse', () => {
                    const icon = toggle.querySelector('.bi-chevron-down');
                    if (icon) {
                        icon.classList.add('rotate-icon');
                    }
                });
                
                targetEl.addEventListener('hide.bs.collapse', () => {
                    const icon = toggle.querySelector('.bi-chevron-down');
                    if (icon) {
                        icon.classList.remove('rotate-icon');
                    }
                });
            }
        }
    });

    // 2. 페이지 로드 시 마지막으로 열었던 메뉴 복원
    const lastOpenedMenuId = localStorage.getItem(storageKey);
    if (lastOpenedMenuId) {
        // 해당 메뉴가 존재하는지 확인
        const lastMenu = document.getElementById(lastOpenedMenuId);
        if (lastMenu) {
            try {
                // 부트스트랩 collapse 요소 가져오기
                const bsCollapse = new bootstrap.Collapse(lastMenu, {
                    toggle: false
                });
                
                // 메뉴 열기
                bsCollapse.show();
                
                // 해당 메뉴의 토글 버튼 찾기
                const toggle = document.querySelector(`[href="#${lastOpenedMenuId}"]`);
                if (toggle) {
                    // 화살표 아이콘 회전
                    const icon = toggle.querySelector('.bi-chevron-down');
                    if (icon) {
                        icon.classList.add('rotate-icon');
                    }
                }
                
                // 부모 메뉴도 열기 (중첩 메뉴의 경우)
                let parent = lastMenu.parentElement;
                while (parent) {
                    const parentCollapseEl = parent.closest('.collapse');
                    if (parentCollapseEl) {
                        try {
                            const parentBsCollapse = new bootstrap.Collapse(parentCollapseEl, {
                                toggle: false
                            });
                            parentBsCollapse.show();
                            
                            // 부모 메뉴의 토글 버튼 찾기
                            const parentToggle = document.querySelector(`[href="#${parentCollapseEl.id}"]`);
                            if (parentToggle) {
                                // 화살표 아이콘 회전
                                const icon = parentToggle.querySelector('.bi-chevron-down');
                                if (icon) {
                                    icon.classList.add('rotate-icon');
                                }
                            }
                            
                            parent = parentCollapseEl.parentElement;
                        } catch (e) {
                            console.log('부모 메뉴를 열 수 없습니다:', e);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } catch (e) {
                console.log('메뉴를 열 수 없습니다:', e);
                // 오류 발생 시 localStorage 항목 삭제
                localStorage.removeItem(storageKey);
            }
        } else {
            // 메뉴가 존재하지 않는 경우 localStorage 항목 삭제
            localStorage.removeItem(storageKey);
        }
    }

    // 3. 현재 URL 기반으로 활성 메뉴 항목 표시
    const currentPath = window.location.pathname;
    const menuLinks = document.querySelectorAll('.list-group-item-action');
    let activeMenuFound = false;
    
    // 먼저 모든 메뉴에서 active 클래스 제거
    menuLinks.forEach(link => {
        link.classList.remove('active');
    });
    
    // 정확히 일치하는 메뉴 찾기
    let exactMatch = false;
    menuLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && !href.startsWith('#') && href === currentPath) {
            link.classList.add('active');
            activeMenuFound = true;
            exactMatch = true;
            
            // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
            openParentMenus(link, storageKey);
        }
    });
    
    // 정확히 일치하는 메뉴가 없는 경우에만 부분 일치 검사
    if (!exactMatch) {
        menuLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href && href.includes('/request/construction') && currentPath.includes('/request/construction')) {
                link.classList.add('active');
                activeMenuFound = true;
                
                // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                openParentMenus(link, storageKey);
            }
            else if (href && href.includes('/inquiry/admin') && currentPath.includes('/inquiry/admin')) {
                link.classList.add('active');
                activeMenuFound = true;
                
                // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                openParentMenus(link, storageKey);
            }
            else if (href && href.includes('/construction') && currentPath.includes('/construction')) {
                link.classList.add('active');
                activeMenuFound = true;
                
                // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                openParentMenus(link, storageKey);
            }
            else if (href && href.includes('/member/admin') && currentPath.includes('/member/admin')) {
                link.classList.add('active');
                activeMenuFound = true;
                
                // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                openParentMenus(link, storageKey);
            }
            else if (href && href.includes('/notice') && currentPath.includes('/notice')) {
                link.classList.add('active');
                activeMenuFound = true;
                
                // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                openParentMenus(link, storageKey);
            }
            else if (href && href.includes('/settings/html-manager') && currentPath.includes('/settings/html-manager')) {
                link.classList.add('active');
                activeMenuFound = true;
                
                // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                openParentMenus(link, storageKey);
            }
            else if (href && !href.startsWith('#') && currentPath.includes(href) && href !== '/') {
                // 특수 케이스 처리: Mobile용과 PC용 메뉴
                if ((href.includes('/list-pc') && currentPath.includes('/list-pc')) || 
                    (href.includes('/list') && !href.includes('/list-pc') && !currentPath.includes('/list-pc'))) {
                    link.classList.add('active');
                    activeMenuFound = true;
                    
                    // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                    openParentMenus(link, storageKey);
                }
                // 다른 일반적인 부분 일치 케이스
                else if (!href.includes('/list')) {
                    link.classList.add('active');
                    activeMenuFound = true;
                    
                    // 링크가 중첩 메뉴 내부에 있는 경우 부모 메뉴도 열기
                    openParentMenus(link, storageKey);
                }
            }
        });
    }
    
    // 활성 메뉴를 찾지 못한 경우 localStorage 항목 삭제
    if (!activeMenuFound && currentPath !== '/' && currentPath !== '/main') {
        localStorage.removeItem(storageKey);
    }
    
    // 부모 메뉴 열기 함수
    function openParentMenus(link, storageKey) {
        let parent = link.parentElement;
        while (parent) {
            const parentCollapseEl = parent.closest('.collapse');
            if (parentCollapseEl) {
                try {
                    const parentBsCollapse = new bootstrap.Collapse(parentCollapseEl, {
                        toggle: false
                    });
                    parentBsCollapse.show();
                    
                    // 부모 메뉴 ID를 localStorage에 저장
                    localStorage.setItem(storageKey, parentCollapseEl.id);
                    
                    // 부모 메뉴의 토글 버튼 찾기
                    const parentToggle = document.querySelector(`[href="#${parentCollapseEl.id}"]`);
                    if (parentToggle) {
                        // 화살표 아이콘 회전
                        const icon = parentToggle.querySelector('.bi-chevron-down');
                        if (icon) {
                            icon.classList.add('rotate-icon');
                        }
                    }
                    
                    parent = parentCollapseEl.parentElement;
                } catch (e) {
                    console.log('부모 메뉴를 열 수 없습니다:', e);
                    break;
                }
            } else {
                break;
            }
        }
    }
});

// 스타일 추가
document.addEventListener('DOMContentLoaded', () => {
    // 동적으로 스타일 추가
    const style = document.createElement('style');
    style.textContent = `
        .rotate-icon {
            transform: rotate(180deg);
            transition: transform 0.3s ease;
        }
        
        .bi-chevron-down {
            transition: transform 0.3s ease;
        }
        
        .list-group-item-action.active {
            background-color: #f8f9fa;
            color: #0d6efd;
            font-weight: bold;
        }
    `;
    document.head.appendChild(style);
});
