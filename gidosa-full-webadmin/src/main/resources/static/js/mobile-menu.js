document.addEventListener('DOMContentLoaded', function () {
    const toggleBtn = document.querySelector('.mobile-toggle-btn');
    const mobileMenu = document.querySelector('.mobile-menu');
    const body = document.body;

    // 오버레이 생성
    const overlay = document.createElement('div');
    overlay.className = 'menu-overlay';
    body.appendChild(overlay);

    // 토글 버튼 클릭 이벤트
    toggleBtn.addEventListener('click', function () {
        mobileMenu.classList.toggle('show');
        overlay.classList.toggle('show');
        body.style.overflow = mobileMenu.classList.contains('show') ? 'hidden' : '';
    });

    // 오버레이 클릭 이벤트
    overlay.addEventListener('click', function () {
        mobileMenu.classList.remove('show');
        overlay.classList.remove('show');
        body.style.overflow = '';
    });
});
