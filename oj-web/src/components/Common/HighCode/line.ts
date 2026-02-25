import './code.scss';

const vCode = {
  mounted(el: any) {
    buildLineNumber(el);
  },
  updated(el: any) {
    buildLineNumber(el);
  }
};

const buildLineNumber = (el: any) => {
  //获取代码片段
  let code = el.querySelector('code.hljs');
  let pre = el.querySelector('pre');
  let html = code?.innerHTML;
  let size = html.split('\n').length;
  let codeNumber = el.querySelector('.hljs-code-number');
  if (codeNumber) {
    el.removeChild(codeNumber);
  }
  //插入行数
  let ul = document.createElement('ul');
  for (let i = 1; i <= size; i++) {
    let li = document.createElement('li');
    li.innerText = i + '';
    ul.appendChild(li);
  }

  ul.classList.add('hljs-code-number');

  // 确保 pre 是 el 的子元素后再进行插入
  if (pre && pre.parentNode === el) {
    el.insertBefore(ul, pre);
  }
};

export default vCode;
