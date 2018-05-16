// 手机验证
export function isvalidPhone(str) {
  const reg = /^1[3|4|5|7|8][0-9]\d{8}$/;
  return reg.test(str);
}
// 汉字验证
export function isvalidSinogram(str) {
  const reg = /^[^\u4e00-\u9fa5]+$/;
  return reg.test(str);
}
// 正整数验证
export function isvalidSignlessInteger(str) {
  const reg = /^\d+$/;
  return reg.test(str);
}
