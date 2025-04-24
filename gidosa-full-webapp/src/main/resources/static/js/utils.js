// EXIF 메타데이터 추출 함수2
function extractExifData2(file) {
  return new Promise((resolve, reject) => {
      if (!file) {
          resolve(null);
          return;
      }

      const reader = new FileReader();
      reader.onload = function(e) {
          const image = new Image();
          image.src = e.target.result;
          image.onload = function() {
              const exifData = {};
              
              EXIF.getData(image, function() {
                  const lat = EXIF.getTag(this, "GPSLatitude");
                  const lon = EXIF.getTag(this, "GPSLongitude");
                  const latRef = EXIF.getTag(this, "GPSLatitudeRef") || "N";
                  const lonRef = EXIF.getTag(this, "GPSLongitudeRef") || "E";

                  if (!lat || !lon) {
                      //document.getElementById("output").innerText = "위치 정보 없음";
                      resolve(null);
                      return;
                  }

                  const latitude = convertDMSToDD(lat, latRef);
                  const longitude = convertDMSToDD(lon, lonRef);
                  
                  //document.getElementById("output").innerText = `위도: ${latitude}, 경도: ${longitude}`;
                  exifData.latitude = latitude;
                  exifData.longitude = longitude;
                  resolve(exifData);

                  return;
              });
          };
      };
      // reader.onerror = function() {
      //     reject(new Error('EXIF 데이터를 읽는 중 오류가 발생했습니다.'));
      // };
      reader.readAsDataURL(file);
      // ArrayBuffer로 파일 읽기(안됨)
      //reader.readAsArrayBuffer(file);
  });
}   
function convertDMSToDD(dms, direction) {
  const degrees = dms[0];
  const minutes = dms[1];
  const seconds = dms[2];
  let decimal = degrees + minutes / 60 + seconds / 3600;
  return (direction === "S" || direction === "W") ? decimal * -1 : decimal;
}

// EXIF 메타데이터 추출 함수(안됨!!!)
function extractExifData(file) {
  return new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onload = function(e) {
          const view = new DataView(e.target.result);
          
          // EXIF 데이터가 없는 경우
          if (view.getUint16(0, false) != 0xFFD8) {
              resolve(null);
              return;
          }
          
          const length = view.byteLength;
          let offset = 2;
          
          while (offset < length) {
              const marker = view.getUint16(offset, false);
              offset += 2;
              
              // EXIF 마커 찾기
              if (marker === 0xFFE1) {
                  if (view.getUint32(offset + 2, false) !== 0x45786966) {
                      resolve(null);
                      return;
                  }
                  
                  const little = view.getUint16(offset + 8, false) === 0x4949;
                  offset += 8;
                  
                  const tags = view.getUint16(offset, little);
                  offset += 2;
                  
                  const exifData = {};
                  
                  for (let i = 0; i < tags; i++) {
                      const tagOffset = offset + (i * 12);
                      const tag = view.getUint16(tagOffset, little);
                      
                      // GPS IFD 포인터 (태그 34853)
                      if (tag === 0x8825) {
                          const gpsInfoOffset = view.getUint32(tagOffset + 8, little);
                          const gpsInfoAddress = offset + gpsInfoOffset;
                          const gpsTagCount = view.getUint16(gpsInfoAddress, little);
                          
                          let latitude = null;
                          let longitude = null;
                          let latitudeRef = null;
                          let longitudeRef = null;
                          
                          for (let j = 0; j < gpsTagCount; j++) {
                              const gpsTagOffset = gpsInfoAddress + 2 + (j * 12);
                              const gpsTag = view.getUint16(gpsTagOffset, little);
                              
                              // GPS 태그 처리
                              if (gpsTag === 1) { // GPSLatitudeRef
                                  const valueOffset = view.getUint32(gpsTagOffset + 8, little);
                                  latitudeRef = String.fromCharCode(view.getUint8(gpsInfoAddress + valueOffset));
                              } else if (gpsTag === 2) { // GPSLatitude
                                  const valueOffset = view.getUint32(gpsTagOffset + 8, little);
                                  const rationals = [];
                                  
                                  for (let k = 0; k < 3; k++) {
                                      const rationalOffset = gpsInfoAddress + valueOffset + (k * 8);
                                      const numerator = view.getUint32(rationalOffset, little);
                                      const denominator = view.getUint32(rationalOffset + 4, little);
                                      rationals.push(numerator / denominator);
                                  }
                                  
                                  latitude = rationals[0] + (rationals[1] / 60) + (rationals[2] / 3600);
                              } else if (gpsTag === 3) { // GPSLongitudeRef
                                  const valueOffset = view.getUint32(gpsTagOffset + 8, little);
                                  longitudeRef = String.fromCharCode(view.getUint8(gpsInfoAddress + valueOffset));
                              } else if (gpsTag === 4) { // GPSLongitude
                                  const valueOffset = view.getUint32(gpsTagOffset + 8, little);
                                  const rationals = [];
                                  
                                  for (let k = 0; k < 3; k++) {
                                      const rationalOffset = gpsInfoAddress + valueOffset + (k * 8);
                                      const numerator = view.getUint32(rationalOffset, little);
                                      const denominator = view.getUint32(rationalOffset + 4, little);
                                      rationals.push(numerator / denominator);
                                  }
                                  
                                  longitude = rationals[0] + (rationals[1] / 60) + (rationals[2] / 3600);
                              }
                          }
                          
                          if (latitude !== null && longitude !== null) {
                              if (latitudeRef === 'S') latitude = -latitude;
                              if (longitudeRef === 'W') longitude = -longitude;
                              
                              exifData.latitude = latitude;
                              exifData.longitude = longitude;
                          }
                      }
                  }
                  
                  resolve(exifData);
                  return;
              } else if ((marker & 0xFF00) !== 0xFF00) {
                  break;
              } else {
                  offset += view.getUint16(offset, false);
              }
          }
          
          resolve(null);
      };
      reader.onerror = function() {
          reject(new Error('EXIF 데이터를 읽는 중 오류가 발생했습니다.'));
      };
      // ArrayBuffer로 파일 읽기
      reader.readAsArrayBuffer(file);
  });
}