import { navItem } from "../../model/index/navItem";

export function navListData():navItem[] {
  return [
    new navItem("",$rawfile("iotImage/treadmill.svg"),"跑步机"),
    new navItem("",$rawfile("iotImage/wristwatch_normal.png"),"智能手表"),
    new navItem("",$rawfile("iotImage/body_fat_scale.svg"),"智能体脂秤"),
  ]
}

export class navListSource {
  private navList: navItem[] = []

  constructor(navList:navItem[]) {
    navList.forEach((item)=>{
      this.navList.push(item)
    })
  }
  public getNavList ():navItem[]{
    return this.navList
  }
  public setNavList(navList:navItem[]){
    this.navList = navList
  }

}
