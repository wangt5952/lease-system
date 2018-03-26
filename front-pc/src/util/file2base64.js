export default file => new Promise((resolve, reject) => {
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = function(e) {
    resolve(this.result);
  }
});
