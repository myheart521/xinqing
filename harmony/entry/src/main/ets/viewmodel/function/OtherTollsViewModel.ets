import { OtherTollsModel } from "../../model/function/OtherTollsModel";

const tollsInitial: OtherTollsModel[] = [
  {
    url:$rawfile("functionImage/consultCenter.png"),
    title: '心理咨询',
    des: '大学生心理咨询中心'
  },
  {
    url: $rawfile("functionImage/album.png"),
    title: '知识科普',
    des: '精选的心理学小知识'
  },
  {
    url: $rawfile("functionImage/game.png"),
    title: '小游戏',
    des: '放松解压小游戏'
  },
  {
    url: $rawfile("functionImage/space.png"),
    title: '每日一句',
    des: '每日一句，不再emo'
  },

]


export class OtherTollsViewModel implements IDataSource {
  private tollsData: OtherTollsModel[] = tollsInitial

  public getTollsData() {
    return this.tollsData
  }

  public setTollsData(tollsData: OtherTollsModel[]) {
    this.tollsData = tollsData
  }


  totalCount(): number {
    return this.tollsData.length
  }

  getData(index: number): OtherTollsModel {
    return this.tollsData[index]
  }

  registerDataChangeListener(listener: DataChangeListener): void {
    console.log("数据改变")
  }

  unregisterDataChangeListener(listener: DataChangeListener): void {
    console.log("数据改变")

  }
}