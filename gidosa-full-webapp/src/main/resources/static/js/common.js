// 모바일 기기 감지 함수
function isMobile() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) || window.innerWidth <= 768;
}

// iOS 감지
const isIos = /iphone|ipad|ipod/.test(window.navigator.userAgent.toLowerCase());
// standalone 모드 감지
const isInStandaloneMode = ('standalone' in window.navigator) && window.navigator.standalone;


