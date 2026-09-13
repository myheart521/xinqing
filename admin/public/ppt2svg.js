/**
 * PPT渲染SVG工具
 * 注意：这是一个简化版，实际使用时需要从文多多示例中复制完整版
 */
(function() {
  class Ppt2Svg {
    constructor(svgElement) {
      this.svg = svgElement;
      this.width = 1000;
      this.height = 562.5;
      this.mode = 'view';
    }
    
    setMode(mode) {
      this.mode = mode;
    }
    
    resetSize(width, height) {
      this.width = width;
      this.height = height;
      
      if (this.svg) {
        this.svg.setAttribute('width', width);
        this.svg.setAttribute('height', height);
        this.svg.setAttribute('viewBox', `0 0 ${width} ${height}`);
      }
    }
    
    drawPptx(pptxObj, pageIndex) {
      if (!this.svg || !pptxObj || !pptxObj.pages || pageIndex >= pptxObj.pages.length) {
        console.error('无法渲染PPT：参数无效');
        return;
      }
      
      // 清空SVG
      while (this.svg.firstChild) {
        this.svg.removeChild(this.svg.firstChild);
      }
      
      // 设置大小
      this.resetSize(this.width, this.height);
      
      // 添加背景
      const background = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
      background.setAttribute('width', '100%');
      background.setAttribute('height', '100%');
      background.setAttribute('fill', '#ffffff');
      this.svg.appendChild(background);
      
      // 添加提示文本
      const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      text.setAttribute('x', this.width / 2);
      text.setAttribute('y', this.height / 2);
      text.setAttribute('text-anchor', 'middle');
      text.setAttribute('font-size', '24');
      text.setAttribute('fill', '#666666');
      text.textContent = `PPT 第 ${pageIndex + 1} 页`;
      
      const subtext = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      subtext.setAttribute('x', this.width / 2);
      subtext.setAttribute('y', this.height / 2 + 40);
      subtext.setAttribute('text-anchor', 'middle');
      subtext.setAttribute('font-size', '16');
      subtext.setAttribute('fill', '#999999');
      subtext.textContent = '(请从文多多示例中复制完整的渲染脚本)';
      
      this.svg.appendChild(text);
      this.svg.appendChild(subtext);
    }
  }
  
  // 暴露到全局
  window.Ppt2Svg = Ppt2Svg;
})(); 