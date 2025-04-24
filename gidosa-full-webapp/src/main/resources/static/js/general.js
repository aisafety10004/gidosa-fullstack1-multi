// 전체 문서에서 우클릭 방지
//document.addEventListener('contextmenu', function (event) {
//  event.preventDefault();
//  //alert('우클릭이 비활성화되어 있습니다.');
//});
// F12, Ctrl+Shift+I, Ctrl+U 등 단축키 차단
// document.addEventListener('keydown', function (e) {
//     // F12
//     if (e.key === "F12") {
//         e.preventDefault();
//         //alert('개발자 도구 사용이 제한되어 있습니다.');
//     }
// 
//     // Ctrl+Shift+I, Ctrl+Shift+C, Ctrl+U, Ctrl+Shift+J
//     if (
//         (e.ctrlKey && e.shiftKey && (e.key === 'I' || e.key === 'J' || e.key === 'C')) ||
//         (e.ctrlKey && e.key === 'U')
//     ) {
//         e.preventDefault();
//         //alert('개발자 도구 단축키가 차단되었습니다.');
//     }
// });