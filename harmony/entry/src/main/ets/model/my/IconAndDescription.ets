// 图标和文字描述
export class IconAndDescription {
  icon: Resource;
  description: string | Resource;
  otherIcon?:Resource

  constructor(icon: Resource, description: string | Resource,otherIcon?:Resource) {
    this.icon = icon;
    this.description = description
    this.otherIcon = otherIcon
  }
}

// 尺寸信息
export class Size {
  height: number;
  width: number;

  constructor(height: number, width: number) {
    this.height = height;
    this.width = width;
  }
}