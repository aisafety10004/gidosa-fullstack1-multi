// 쿠키 설정 함수
function setCookie(name, value, days) {
    const expires = new Date();
    expires.setTime(expires.getTime() + (days * 24 * 60 * 60 * 1000));
    document.cookie = name + '=' + encodeURIComponent(value) + ';expires=' + expires.toUTCString() + ';path=/';
}

// 쿠키 가져오기 함수
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return decodeURIComponent(parts.pop().split(';').shift());
    return '';
}

// 쿠키 삭제 함수
function deleteCookie(name) {
    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/';
}

// 페이지 로드 시 저장된 아이디가 있다면 불러오기
document.addEventListener('DOMContentLoaded', function() {
    const savedUsername = getCookie('rememberedUsername');
    if (savedUsername) {
        document.getElementById('username').value = savedUsername;
        document.getElementById('id-save').checked = true;
    }
});

// 로그인 폼 제출 시 Remember Me 체크 여부에 따라 쿠키 저장
document.querySelector('.user').addEventListener('submit', function(e) {
    const username = document.getElementById('username').value;
    const rememberMe = document.getElementById('id-save').checked;
    
    if (rememberMe) {
        setCookie('rememberedUsername', username, 14); // 14일 동안 저장
    } else {
        deleteCookie('rememberedUsername');
    }
}); 