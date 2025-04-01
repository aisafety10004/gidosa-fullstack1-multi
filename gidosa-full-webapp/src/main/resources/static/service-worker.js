self.addEventListener('install', function (e) {
  console.log('Service Worker installing...');
});

self.addEventListener('fetch', function (e) {
  // 캐시 전략 설정 가능
  //console.log('Service Worker fetch.............');
});
