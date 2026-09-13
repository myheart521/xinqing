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
import { CommonConstants } from "../../../common/constant/CommonConstants";
import { MusicConstant } from "../../../common/constant/Function";
import { MediaService } from "../../../utils/music/MediaService";


@Preview
@Component
export struct ControlAreaComponent {
  @StorageLink('isPlay') isPlay: boolean = false;
  @StorageLink('currentTime') currentTime: string = '00:00';
  @StorageLink('totalTime') totalTime: string = '00:00';
  @StorageLink('progress') value: number = 0;
  @StorageLink('progressMax') max: number = 0;
  @State playModeIndex: number = 0;
  @StorageLink('imageColor') imageColor: string = '';
  @StorageLink('pageShowTime') pageShowTime: number = 0;

  build() {
    Column() {
      Row() {
        Image($rawfile('functionImage/music_public_list_cycled.svg'))
          .controlImageBuilder()
          .width(MusicConstant.MUSIC_ICON_SIZE)
        Image($rawfile('functionImage/music_sequence.svg'))
          .controlImageBuilder()
          .width(MusicConstant.MUSIC_ICON_SIZE)
        Image($rawfile('functionImage/music_ring.svg'))
          .controlImageBuilder()
          .width(MusicConstant.MUSIC_ICON_SIZE)
        Image($r('app.media.musicMore'))
          .controlImageBuilder()
          .width(MusicConstant.MUSIC_ICON_SIZE)
      }
      .width(CommonConstants.FULL_PARENT)
      .justifyContent(FlexAlign.SpaceBetween)

      Column() {
        Slider({
          min: 0,
          max: this.max,
          step: 1,
          value: this.value
        })
          .blockColor("#00FFFFFF")
          .selectedColor("#DBFFFFFF")
          .trackColor("#33FFFFFF")
          .blockSize({
            width: 1,
            height: 1
          })
          .onChange((value: number, mode: SliderChangeMode) => {
            if (mode === SliderChangeMode.End || mode === SliderChangeMode.Begin) {
              MediaService.getInstance().seek(value);
            }
            this.pageShowTime = 0;
          })
          .height(16)
          .margin({
            left: -4,
            right: -4
          })
          .hitTestBehavior(HitTestMode.Block)
        Row() {
          Text(this.currentTime)
            .fontColor("#99FFFFFF")
            .fontSize($r('app.float.xs'))
            .fontFamily("HarmonyHeiTi")
            .lineHeight('14vp')
          Text(this.totalTime)
            .fontColor("#99FFFFFF")
            .fontSize($r('app.float.xs'))
            .fontFamily("HarmonyHeiTi")
            .lineHeight('14vp')
        }
        .width(CommonConstants.FULL_PARENT)
        .justifyContent(FlexAlign.SpaceBetween)
      }
      .margin({
        top: $r('app.float.mp_xl'),
        bottom: $r("app.float.lg")
      })

      Row() {
        Image($rawfile('functionImage/music_public_forward.svg'))
          .controlImageBuilder()
          .width(32)
          .onClick(() => {
            MediaService.getInstance().playPrevious();
            this.pageShowTime = 0;
          })
        Image(this.isPlay ? $rawfile('functionImage/music_public_play.svg') :
        $rawfile('functionImage/music_public_pause.svg'))
          .controlImageBuilder()
          .width(72)
          .onClick(() => {
            if (this.isPlay) {
              MediaService.getInstance().pause();
            } else {
              if (MediaService.getInstance().getFirst()) {
                MediaService.getInstance().loadAssent(0);
              } else {
                MediaService.getInstance().play();
              }
            }
            this.pageShowTime = 0;
          })
        Image($rawfile('functionImage/music_public_next.svg'))
          .controlImageBuilder()
          .width(32)
          .onClick(() => {
            MediaService.getInstance().playNextAuto(true);
            this.pageShowTime = 0;
          })
      }
      .width(CommonConstants.FULL_PARENT)
      .justifyContent(FlexAlign.SpaceBetween)
      .padding({
        left: 36,
        right: 36
      })
    }
  }
}

@Extend(Image)
function controlImageBuilder() {
  .aspectRatio(1)
  .opacity(0.86)
  .objectFit(ImageFit.Contain)
}
