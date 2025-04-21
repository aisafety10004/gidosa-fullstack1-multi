function toggleMenu() {
    const mobileMenu = document.getElementById('mobile-menu');
    const isOpen = mobileMenu.classList.contains('hidden');
    const topBar = document.getElementById('hamburger-top');
    const middleBar = document.getElementById('hamburger-middle');
    const bottomBar = document.getElementById('hamburger-bottom');

    if (isOpen) {
        mobileMenu.classList.remove('hidden');
        topBar.classList.add('transform', 'rotate-45', 'translate-y-[8px]');
        middleBar.classList.add('opacity-0');
        bottomBar.classList.add('transform', '-rotate-45', '-translate-y-[8px]');
    } else {
        closeMenu();
    }
}

function toggleMenu3() {
    const mobileMenu = document.getElementById('mobile-menu');
    const isOpen = mobileMenu.classList.contains('hidden');
    const topBar = document.getElementById('hamburger-top');
    const middleBar = document.getElementById('hamburger-middle');
    const bottomBar = document.getElementById('hamburger-bottom');
    const overlay = document.createElement('div');

    // 오버레이 설정
    overlay.id = 'menu-overlay';
    overlay.className = 'fixed inset-0 bg-black bg-opacity-50 z-40 hidden transition-opacity duration-300';
    overlay.style.backdropFilter = 'blur(4px)';
    overlay.onclick = closeMenu3;

    if (isOpen) {
        // body에 menu-open 클래스 추가
        document.body.classList.add('menu-open');
        
        // 오버레이가 없으면 추가
        if (!document.getElementById('menu-overlay')) {
            document.body.appendChild(overlay);
        }
        
        // 오버레이 표시
        const menuOverlay = document.getElementById('menu-overlay');
        menuOverlay.classList.remove('hidden');
        menuOverlay.classList.add('opacity-100');
        
        // 메뉴 초기 위치 설정 (오른쪽 바깥에서 시작)
        mobileMenu.style.transform = 'translateX(100%)';
        mobileMenu.classList.remove('hidden');
        
        // 애니메이션 시작
        setTimeout(() => {
            mobileMenu.style.transition = 'transform 0.3s ease-out';
            mobileMenu.style.transform = 'translateX(0)';
        }, 10);
        
        // 햄버거 아이콘을 X로 변환
        topBar.classList.add('transform', 'rotate-45', 'translate-y-[8px]');
        middleBar.classList.add('opacity-0');
        bottomBar.classList.add('transform', '-rotate-45', '-translate-y-[8px]');
    } else {
        closeMenu3();
    }
}

function closeMenu() {
    const mobileMenu = document.getElementById('mobile-menu');
    const topBar = document.getElementById('hamburger-top');
    const middleBar = document.getElementById('hamburger-middle');
    const bottomBar = document.getElementById('hamburger-bottom');

    mobileMenu.classList.add('hidden');
    topBar.classList.remove('transform', 'rotate-45', 'translate-y-[8px]');
    middleBar.classList.remove('opacity-0');
    bottomBar.classList.remove('transform', '-rotate-45', '-translate-y-[8px]');
}

function closeMenu3() {
    const mobileMenu = document.getElementById('mobile-menu');
    const topBar = document.getElementById('hamburger-top');
    const middleBar = document.getElementById('hamburger-middle');
    const bottomBar = document.getElementById('hamburger-bottom');
    const menuOverlay = document.getElementById('menu-overlay');

    // body에서 menu-open 클래스 제거
    document.body.classList.remove('menu-open');
    
    // 메뉴 애니메이션 (오른쪽으로 슬라이드)
    mobileMenu.style.transition = 'transform 0.3s ease-in';
    mobileMenu.style.transform = 'translateX(100%)';

    // 오버레이 숨기기
    if (menuOverlay) {
        menuOverlay.classList.remove('opacity-100');
        menuOverlay.classList.add('opacity-0');
    }

    // 애니메이션 완료 후 실제 숨기기
    setTimeout(() => {
        mobileMenu.classList.add('hidden');
        mobileMenu.style.transform = '';
        mobileMenu.style.transition = '';
        
        // 오버레이 제거
        if (menuOverlay) {
            menuOverlay.classList.add('hidden');
        }
    }, 300);

    // 햄버거 아이콘 복원
    topBar.classList.remove('transform', 'rotate-45', 'translate-y-[8px]');
    middleBar.classList.remove('opacity-0');
    bottomBar.classList.remove('transform', '-rotate-45', '-translate-y-[8px]');
}

