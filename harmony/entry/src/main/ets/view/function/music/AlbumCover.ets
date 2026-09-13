import { CommonConstants } from "../../../common/constant/CommonConstants";
import { MusicConstant } from "../../../common/constant/Function";
import { OptionItem, optionList } from "../../../viewmodel/function/music/SongListData";

@Preview
@Component
export struct AlbumCover {
  @State imgHeight: Length = 70;

  @Builder
  CoverImage() {
    Stack({ alignContent: Alignment.BottomStart }) {
      Image($rawfile("functionImage/music_picture4.png"))
        .width(MusicConstant.MUSIC_DES_WH)
        .height(MusicConstant.MUSIC_DES_WH)
        .borderRadius(MusicConstant.MUSIC_DES_RADIUS)

      Text($r("app.string.forget_past"))
        .letterSpacing(1)
        .fontWeight(FontWeight.Bold)
        .fontColor(Color.White)
        .fontSize($r("app.float.xs"))
        .translate({
          x: 10,
          y:"-100%"
        })
    }
    .width(MusicConstant.MUSIC_DES_WH)
    .height(MusicConstant.MUSIC_DES_WH)
  }

  @Builder
  CoverIntroduction() {
    Column() {
      Text($r('app.string.list_name'))
        .opacity(0.6)
        .fontWeight(FontWeight.Bold)
        .fontColor($r('app.color.album_name_introduction'))
        .fontSize($r("app.float.lg"))
        .margin({ bottom: $r('app.float.mp_sm') })
      Text($r('app.string.playlist_Introduction'))
        .opacity(0.6)
        .width(CommonConstants.FULL_PARENT)
        .fontWeight(500)
        .fontColor($r('app.color.album_name_introduction'))
        .fontSize($r("app.float.xs"))
    }
    .width(CommonConstants.FULL_PARENT)
    .alignItems(HorizontalAlign.Start)
    .padding($r("app.float.mp_lg"))
    .justifyContent(FlexAlign.Center)

  }

  @Builder
  CoverOptions() {
    Row() {
      ForEach(optionList, (item: OptionItem) => {
        Column({ space: 4 }) {
          Image(item.image)
            .height(MusicConstant.MUSIC_DES_OPTION_ICON)
            .width(MusicConstant.MUSIC_DES_OPTION_ICON)
          Text(item.text)
            .fontColor($r('app.color.album_name_introduction'))
            .fontSize($r("app.float.sm"))
        }

      }, (item: OptionItem, index?: number) => index + JSON.stringify(item))
    }
    .height(MusicConstant.MUSIC_DES_OPTION_HEIGHT)
    .width(CommonConstants.FULL_PARENT)
    .padding({
      left: $r('app.float.mp_sm'),
      right: $r('app.float.mp_sm')
    })
    .justifyContent(FlexAlign.SpaceBetween)
  }

  build() {
    Column() {
      Row() {
        this.CoverImage()
        this.CoverIntroduction()
      }
      .padding({
        top: 50
      })
      Row(){
        this.CoverOptions()
      }
      .margin({top:$r("app.float.mp_sm")})
    }
    .margin({
      left: $r("app.float.mp_sm"),
      right: $r("app.float.mp_sm")
    })
  }
}