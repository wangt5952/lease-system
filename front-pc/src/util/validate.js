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
// 验证6-10位字母数字混合密码
export function isvalidPassword(str) {
  const reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$/;
  return reg.test(str);
}
// 验证身份证(18位数)
export function isvalidIdentityID18(str) {
  const reg = /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
  return reg.test(str);
}
// 验证身份证(15位数)
export function isvalidIdentityID15(str) {
  const reg = /^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$/;
  return reg.test(str);
}
