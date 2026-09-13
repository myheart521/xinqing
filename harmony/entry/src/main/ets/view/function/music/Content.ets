import { CommonConstants } from "../../../common/constant/CommonConstants"
import { MusicConstant } from "../../../common/constant/Function"
import { AlbumCover } from "./AlbumCover"
import { PlayList } from "./PlayList"

@Component
export struct Content {
  build() {
    Column() {
      //顶部的音乐列表描述
      AlbumCover()
        .backgroundColor($r("app.color.album_bg"))
        .height(MusicConstant.MUSIC_DES_HEIGHT)
      //音乐列表
      PlayList()
        .borderRadius(MusicConstant.PLAYLIST_RADIUS)
    }
    .height(CommonConstants.FULL_PARENT)
  }
}