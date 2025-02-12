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