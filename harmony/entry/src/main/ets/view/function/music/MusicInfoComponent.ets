/*
 * Copyright (c) 2024 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { CommonConstants } from '../../../common/constant/CommonConstants';
import { MusicConstant } from '../../../common/constant/Function';
import { SongItem } from '../../../model/function/music/SongData';
import { ControlAreaComponent } from './ControlAreaComponent';

@Preview
@Component
export struct MusicInfoComponent {
  @State currentTabIndex: number = 0;
  @StorageLink('isFoldFull') isFoldFull: boolean = false;
  @StorageLink('songList') songList: SongItem[] = [];
  @StorageLink('selectIndex') selectIndex: number = 0;
  @State isShowControl: boolean = false;

  build() {
    Column() {
      //歌曲图片
      this.CoverInfo()
      //歌曲信息
      this.MusicInfo()
      Blank()
      //控制歌曲是否播放
      ControlAreaComponent()
    }
    .height(CommonConstants.FULL_PARENT)
    .width(CommonConstants.FULL_PARENT)
    .clip(false)

  }

  @Builder
  CoverInfo() {
    Row() {
      Image(this.songList[this.selectIndex].label)
        .width(CommonConstants.FULL_PARENT)
        .aspectRatio(1)
        .borderRadius(8)
        .shadow({
          radius: 12,
          color: "#66000000",
          offsetX: 0,
          offsetY: 8
        })
        .margin(3)
    }
  }

  @Builder
  MusicInfo() {
    Column() {
      Flex({ justifyContent: FlexAlign.SpaceBetween, alignItems: ItemAlign.Center }) {
        //歌曲名字
        Text(this.songList[this.selectIndex].title)
          .fontSize($r("app.float.2xl"))
          .fontColor(Color.White)
          .opacity(0.86)
          .fontWeight(FontWeight.Bold)
          .fontFamily('HarmonyHeiTi-Bold')
        //收藏数量图片
        Image($rawfile('functionImage/music_public_likes.svg'))
          .width(MusicConstant.MUSIC_ICON_SIZE)
          .height(MusicConstant.MUSIC_ICON_SIZE)
          .objectFit(ImageFit.Contain)
          .fillColor(Color.White)
          .opacity(0.86)
      }

      //歌曲作者
      Text(this.songList[this.selectIndex].singer)
        .textAlign(TextAlign.Start)
        .fontSize($r("app.float.sm"))
        .fontColor("#99FFFFFF")
        .fontFamily('HarmonyHeiTi')
        .margin({ top: 0 })
        .width(CommonConstants.FULL_PARENT)
        .fontWeight(FontWeight.Regular)
    }
    .margin({ top: $r('app.float.mp_xl') })
  }
}