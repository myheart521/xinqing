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

import { common } from '@kit.AbilityKit';
import { util } from '@kit.ArkTS';
import { CommonConstants } from '../../../common/constant/CommonConstants';
import { MusicConstant } from '../../../common/constant/Function';
import { SongItem } from '../../../model/function/music/SongData';
import { LrcEntry } from '../../../utils/music/lyric/LrcEntry';
import { parseKrcLyric, parseLrcLyric } from '../../../utils/music/lyric/LrcUtils';
import LrcView from '../../../utils/music/lyric/LrcView';
import { LyricFile, LyricScrollEffect, LyricTopPosition } from '../../../utils/music/lyric/LyricConst';
import { ControlAreaComponent } from './ControlAreaComponent';

@Component
export struct LyricsComponent {
  @StorageLink('progress') time: number = 0;
  @StorageLink('isFoldFull') isFoldFull: boolean = false;
  @State progress: number = 0.01;
  @State progressText: string = '';
  @State lyricScrollEffect: LyricScrollEffect = LyricScrollEffect.Line;
  @State mLrcEntryList: Array<LrcEntry> = [];
  @StorageLink('songList') songList: SongItem[] = [];
  @StorageProp('selectIndex') @Watch('getLrcEntryList') selectIndex: number = 0;
  private context: common.UIAbilityContext | undefined = AppStorage.get('context');
  @StorageLink('isPlay') isPlay: boolean = false;
  @Link isShowControl: boolean;
  @Link isTablet: boolean;
  @State intervalID: number = 0;
  @State pageShowTime: number = 0;

  aboutToAppear() {
    this.getLrcEntryList();
  }

  getLrcEntryList() {
    this.mLrcEntryList = [];
    if (!this.context) {
      return;
    }
    this.context.resourceManager.getRawFileContent(this.songList[this.selectIndex].lyric)
      .then((value: Uint8Array) => {
        let textDecoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
        let stringData = textDecoder.decodeWithStream(value, { stream: false });
        if (this.songList[this.selectIndex].lyric.endsWith(LyricFile.KRC)) {
          this.mLrcEntryList = parseKrcLyric(stringData);
        } else if (this.songList[this.selectIndex].lyric.endsWith(LyricFile.LRC)) {
          this.mLrcEntryList = parseLrcLyric(stringData);
        }
      })
  }

  build() {
    Column() {
      Row() {
        if (!this.isTablet) {
          Image(this.songList[this.selectIndex].label)
            .width(48)
            .borderRadius(4)
            .margin({
              right: $r('app.float.mp_sm')
            })
        }
        Column() {
          Text(this.songList[this.selectIndex].title)
            .fontSize($r("app.float.lg"))
            .fontColor(Color.White)
            .fontWeight(700)
            .fontFamily('HarmonyHeiTi-Bold')
          Text(this.songList[this.selectIndex].singer)
            .fontSize($r("app.float.xs"))
            .fontColor($r('sys.color.ohos_id_color_text_hint_contrary'))
            .fontWeight(500)
            .fontFamily('HarmonyHeiTi-Medium')
            .margin({
              top: 2
            })
        }
        .alignItems(HorizontalAlign.Start)
      }
      .width(CommonConstants.FULL_PARENT)
      .justifyContent(FlexAlign.Start)
      .margin({
        top: 48,
        bottom: $r('app.float.mp_xl')
      })
      LrcView({
        lyricMilliSecondsTime: this.time,
        mLrcEntryList: this.mLrcEntryList,
        lyricScrollEffect: this.lyricScrollEffect,
        lyricTopPosition: LyricTopPosition.Middle
      })
        .layoutWeight(1)

      Row() {
        if (this.isTablet) {
          Image($rawfile('functionImage/music_public_likes.svg'))
            .width(38)
            .height(38)
            .objectFit(ImageFit.Contain)
            .margin({
              right: 56
            })
        }
        Image($rawfile('functionImage/music_lyrics_button.svg'))
          .width(MusicConstant.MUSIC_ICON_SIZE)
          .aspectRatio(1)
          .opacity(0.86)
      }
      .width(CommonConstants.FULL_PARENT)
      .height(MusicConstant.MUSIC_ICON_SIZE)
      .justifyContent(FlexAlign.End)
      .margin({
        top: $r('app.float.mp'),
        bottom: 8,
        right: 3
      })

      if (this.isShowControl) {
        ControlAreaComponent()
          .margin({
            top: 35,
            bottom: $r('app.float.mp_lg')
          })
      }
    }
    .onClick(() => {
      this.pageShowTime = 0;
    })
  }
}