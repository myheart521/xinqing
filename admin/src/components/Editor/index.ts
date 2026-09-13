// 定义编辑器接口
interface Editor {
  content: string;
  selection: {
    start: number;
    end: number;
  };
}

// 编辑器类
class TextEditor implements Editor {
  content: string;
  selection: {
    start: number;
    end: number;
  };

  constructor() {
    this.content = '';
    this.selection = {
      start: 0,
      end: 0
    };
  }

  // 设置内容
  setContent(text: string) {
    this.content = text;
  }

  // 获取内容
  getContent(): string {
    return this.content;
  }

  // 设置选区
  setSelection(start: number, end: number) {
    this.selection = {
      start,
      end
    };
  }

  // 获取选中内容
  getSelectedText(): string {
    return this.content.substring(this.selection.start, this.selection.end);
  }
}

export default TextEditor;
