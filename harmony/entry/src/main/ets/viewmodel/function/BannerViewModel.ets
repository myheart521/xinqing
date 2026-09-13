import { FunctionBannerModel } from "../../model/function/FunctionBannerModel";

const initBannerData: FunctionBannerModel[] = [
  {
    url: $rawfile("functionImage/music_picture1.png"),
    title: "测试题库",
    subTitle: "帮助你探索情感、认知与行为的微妙联系"
  }, {
  url: $rawfile("functionImage/music_picture2.png"),
  title: "音乐",
  subTitle: "在疗愈的旋律中找到宁静，让音乐成为你心灵的庇护所"
},  {
  url: $rawfile("functionImage/music_picture3.png"),
  title: "运动",
  subTitle: "每一次心跳都是对生命的热爱，每一次挥汗如雨都是对自我的挑战"
}, {
  url: $rawfile("functionImage/music_picture4.png"),
  title: "成长记录",
  subTitle: "每一段文字都是对过往经历的深思，每一张图片都承载着难忘的瞬间"
}
]

export class BannerViewModel {
  private bannerData: FunctionBannerModel[] = initBannerData

  public setBannerData(bannerData: FunctionBannerModel[]) {
    this.bannerData = bannerData
  }

  public getBannerData() {
    return this.bannerData
  }
}