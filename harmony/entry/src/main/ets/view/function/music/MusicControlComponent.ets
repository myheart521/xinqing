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

import { display } from '@kit.ArkUI';
import { image } from '@kit.ImageKit';
import { effectKit } from '@kit.ArkGraphics2D';
import { common } from '@kit.AbilityKit';
import { BusinessError, Callback } from '@kit.BasicServicesKit';
import { LyricsComponent } from './LyricsComponent';
import { MusicInfoComponent } from './MusicInfoComponent';
import { ControlAreaComponent } from './ControlAreaComponent';
import { TopAreaComponent } from './TopAreaComponent';
import { SongItem } from '../../../model/function/music/SongData';
import { Logger } from '../../../utils/Logger';
import { CommonConstants } from '../../../common/constant/CommonConstants';
import { ColorConversion } from '../../../utils/music/ColorConversion';
import { JSON } from '@kit.ArkTS';

@Component
export struct MusicControlComponent {
  @StorageLink('songList') songList: SongItem[] = [];
  @StorageProp('selectIndex') @Watch('getImageColor') selectIndex: number = 0;
  @StorageLink('imageColor') imageColor: string = 'rgba(0, 0, 2, 1.00)';
  @StorageLink('topArea') topArea: number = 0;
  @StorageLink('bottomArea') bottomArea: number = 0;
  @State imageLabel: PixelMap | Resource = this.songList[this.selectIndex].label;
  @State isShowControl: boolean = true;
  @State isShowControlLg: boolean = false;
  @State isTablet: boolean = true;
  @State isTabletFalse: boolean = false;
  @StorageLink('pageShowTime') pageShowTime: number = 0;
  @State intervalID: number = 0;
  @Link isShowPlay: boolean;
  @StorageLink('isFoldFull') isFoldFull: boolean = false;
  private context: common.UIAbilityContext | undefined = AppStorage.get('context');
  private callback: Callback<display.FoldDisplayMode> = (data: display.FoldDisplayMode) => {
    if (canIUse('SystemCapability.Window.SessionManager')) {
      if (data === display.FoldDisplayMode.FOLD_DISPLAY_MODE_FULL) {
        this.isFoldFull = true;
      } else {
        this.isFoldFull = false;
      }
    }
  };

  aboutToAppear(): void {
    this.getImageColor();
    try {
      if (canIUse('SystemCapability.Window.SessionManager')) {
        let mode = display.getFoldDisplayMode();
        if (mode === display.FoldDisplayMode.FOLD_DISPLAY_MODE_FULL) {
          this.isFoldFull = true;
        }
        display.on('foldDisplayModeChange', this.callback);
      }
    } catch (exception) {
      Logger.error('Failed to register callback. Code: ' + JSON.stringify(exception));
    }
  }

  aboutToDisappear(): void {
    if (canIUse('SystemCapability.Window.SessionManager')) {
      display.off('foldDisplayModeChange', this.callback);
    }
  }

  build() {
    Stack() {
      Image(this.imageLabel)
        .size({ height: '120%' })
        .aspectRatio(1)
        .objectFit(ImageFit.Cover)
        .opacity(0.5)
      Row() {
        Stack({ alignContent: Alignment.TopStart }) {
          Swiper() {
            MusicInfoComponent()
              .margin({
                top: 72,
                bottom: $r('app.float.mp_lg')
              })
              .padding({
                left: $r('app.float.mp_xl'),
                right: $r('app.float.mp_xl')
              })
            LyricsComponent({ isShowControl: this.isShowControl, isTablet: this.isTabletFalse })
              .margin({
                top: 8
              })
              .padding({
                left: $r('app.float.mp_xl'),
                right: $r('app.float.mp_xl')
              })
          }
          .height(CommonConstants.FULL_PARENT)
          .indicator(
            new DotIndicator()
              .top($r('app.float.mp_sm'))
              .selectedColor("#DBFFFFFF")
              .color("#33FFFFFF")
          )
          .clip(false)
          .loop(false)
          .onChange((index: number) => {
            if (index === 1) {
              this.isShowControl = true;
              this.intervalID = setInterval(() => {
                this.pageShowTime += 1;
                if (this.pageShowTime > 5) {
                  this.isShowControl = false;
                  clearInterval(this.intervalID);
                }
              }, 1000);
            } else {
              this.pageShowTime = 0;
              clearInterval(this.intervalID);
            }
          })

          TopAreaComponent({ isShowPlay: this.isShowPlay })
            .padding({
              left: $r('app.float.mp_xl'),
              right: $r('app.float.mp_xl')
            })
        }
        .height(CommonConstants.FULL_PARENT)
      }
      .padding({
        bottom: this.bottomArea,
        top: this.topArea
      })
    }
    .height(CommonConstants.FULL_PARENT)
    .width(CommonConstants.FULL_PARENT)
    .backgroundColor(this.imageColor)
  }

  /**
   * Get largest proportion color of an image.
   */
  getImageColor() {
    console.log("音乐模块selectIndex:",this.selectIndex)
    if (!this.context) {
      console.log("音乐模块selectIndex return:",this.selectIndex)
      return;
    }
    this.context.resourceManager.getMediaContent(this.songList[this.selectIndex].label)
      .then((value: Uint8Array) => {
        let buffer = value.buffer as ArrayBuffer;
        image.createImageSource(buffer).createPixelMap().then((pixelMap) => {
          effectKit.createColorPicker(pixelMap, (error, colorPicker) => {
            if (error) {
              Logger.error('Failed to create color picker.');
            } else {
              let color = colorPicker.getLargestProportionColor();
              let colorArr = ColorConversion.dealColor(color.red, color.green, color.blue);
              this.imageColor = `rgba(${colorArr[0]}, ${colorArr[1]}, ${colorArr[2]}, 1)`;
            }
          })
          let headFilter = effectKit.createEffect(pixelMap);
          if (headFilter !== null) {
            headFilter.blur(15);
            headFilter.getEffectPixelMap().then((value) => {
              this.imageLabel = value;
              console.log("音乐模块imageLabel",JSON.stringify(this.imageLabel))
            })
          }
        })
          .catch((error: BusinessError) => {
            Logger.error(`${error.code} + ${error.message}`)
          })
      })
      .catch((error: BusinessError) => {
        Logger.error(`${error.code} + ${error.message}`)
      })
  }
}