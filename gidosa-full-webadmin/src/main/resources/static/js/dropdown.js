/**
 * 드롭다운 메뉴 관련 기능을 관리하는 모듈
 */
const DropdownManager = {
    /**
     * 드롭다운 초기화
     */
    init: function () {
        this.initDropdownToggles();
        this.initSubmenus();
        this.initDocumentClick();
    },

    /**
     * 드롭다운 토글 버튼 초기화
     */
    initDropdownToggles: function () {
        const dropdownToggles = document.querySelectorAll('.dropdown-toggle');
        dropdownToggles.forEach((toggle) => {
            toggle.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();

                // 다른 모든 드롭다운 메뉴 닫기
                document.querySelectorAll('.dropdown-menu.show').forEach((menu) => {
                    if (menu !== toggle.nextElementSibling) {
                        menu.classList.remove('show');
                    }
                });

                const dropdownMenu = toggle.nextElementSibling;
                if (dropdownMenu.classList.contains('dropdown-menu')) {
                    dropdownMenu.classList.toggle('show');
                }
            });
        });
    },

    /**
     * 서브메뉴 초기화
     */
    initSubmenus: function () {
        const dropdownSubmenus = document.querySelectorAll('.dropdown-submenu');
        dropdownSubmenus.forEach((submenu) => {
            submenu.addEventListener('mouseenter', () => {
                const dropdownMenu = submenu.querySelector('.dropdown-menu');
                if (dropdownMenu) {
                    dropdownMenu.classList.add('show');
                }
            });

            submenu.addEventListener('mouseleave', () => {
                const dropdownMenu = submenu.querySelector('.dropdown-menu');
                if (dropdownMenu) {
                    dropdownMenu.classList.remove('show');
                }
            });
        });
    },

    /**
     * 문서 클릭 이벤트 초기화
     */
    initDocumentClick: function () {
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.dropdown')) {
                document.querySelectorAll('.dropdown-menu.show').forEach((menu) => {
                    menu.classList.remove('show');
                });
            }
        });
    },
};

// DOM이 로드되면 드롭다운 초기화
document.addEventListener('DOMContentLoaded', () => {
    DropdownManager.init();
});
