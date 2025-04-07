self.addEventListener('install', function (e) {
  console.log('Service Worker installing...');
});

self.addEventListener('fetch', function (e) {
  // 캐시 전략 설정 가능
  //console.log('Service Worker fetch.............');
});

// VAPID용???
self.addEventListener('push', function(e) {
  console.log('Service Worker push.............', e.data);
  
  // const data = e.data.json();
  // const options = {
  //   body: data.body,
  //   icon: '/assets/images/baroceum_app_192x192.png',
  //   badge: '/assets/images/baroceum_app_72x72.png'
  // };
  // e.waitUntil(
  //   self.registration.showNotification(data.title, options)
  // );
});
