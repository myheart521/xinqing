import { CommonConstants } from "../../../common/constant/CommonConstants";
import { FunctionConstant, MusicConstant } from "../../../common/constant/Function";
import { SongItem } from "../../../model/function/music/SongData";
import { MediaService } from "../../../utils/music/MediaService";
import { SongDataSource } from "../../../viewmodel/function/music/SongDataSource";

@Component
export struct PlayList {
  @StorageLink('songList') songList: SongItem[] = [];
  @StorageLink('isShowPlay') isShowPlay: boolean = false;

  @Builder
  PlayAll() {
    Row() {
      Image($r('app.media.musicPlayAll'))
        .height(23)
        .width(23)
      Text($r('app.string.play_all', this.songList.length))
        .maxLines(1)
        .padding({ left: $r('app.float.mp_sm') })
        .fontColor(Color.Black)
        .fontSize($r("app.float.sm"))
      Blank()
      Image($r('app.media.musicOrderPlay'))
        .width(MusicConstant.MUSIC_ICON_SIZE)
        .height(MusicConstant.MUSIC_ICON_SIZE)
        .margin({ right: $r('app.float.mp') })
      Image($r('app.media.musicSortList'))
        .height(MusicConstant.MUSIC_ICON_SIZE)
        .width(MusicConstant.MUSIC_ICON_SIZE)
    }
    .height(60)
    .width(CommonConstants.FULL_PARENT)
    .backgroundColor(Color.White)
    .padding({
      left: $r('app.float.mp_sm'),
      right: $r('app.float.mp_sm')
    })
    .borderRadius({
      topRight: 20,
      topLeft: 20
    })
    .position({
      x: 0,
      y: 0
    })
  }

  @Builder
  SongItem(item: SongItem, index: number) {
    Row() {
      Column() {
        Text(item.title)
          .fontColor(Color.Black)
          .fontSize($r("app.float.sm"))
          .margin({ bottom: $r('app.float.mp_xs') })
        Row() {
          Image(item.mark)
            .width(16)
            .height(16)
            .margin({ right: $r('app.float.mp_xs') })
          Text(item.singer)
            .opacity(0.38)
            .fontColor(Color.Black)
            .fontSize($r("app.float.xs"))
        }
      }
      .alignItems(HorizontalAlign.Start)

      Blank()
      Image($r('app.media.musicListMore'))
        .height(MusicConstant.MUSIC_ICON_SIZE)
        .width(MusicConstant.MUSIC_ICON_SIZE)
    }
    .onClick(() => {
      MediaService.getInstance().loadAssent(index)
      this.isShowPlay = true;
    })
    .height(60)
    .width(CommonConstants.FULL_PARENT)
  }

  build() {
    Column() {
      this.PlayAll()
      List() {
        LazyForEach(new SongDataSource(this.songList), (item: SongItem, index: number) => {
          ListItem() {
            Column() {
              this.SongItem(item, index)
            }
            .padding({
              left: $r('app.float.mp'),
              right: $r('app.float.mp')
            })
          }
        }, (item: SongItem, index?: number) => JSON.stringify(item) + index)
      }
      .width(CommonConstants.FULL_PARENT)
      .backgroundColor(Color.White)
      .margin({ top: 60 })
      .lanes(1)
      .layoutWeight(1)
      .divider({
        color: '#1A182431',
        strokeWidth: 0.5,
        startMargin: $r("app.float.mp"),
        endMargin: $r("app.float.mp")
      })
    }
    .padding({
      bottom: 350
    })
  }
}