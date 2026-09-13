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


@Component
export struct TopAreaComponent {
  @Link isShowPlay: boolean;

  build() {
    Row() {
      Image($rawfile('functionImage/music_back_down.svg'))
        .width(MusicConstant.MUSIC_ICON_SIZE)
        .height(MusicConstant.MUSIC_ICON_SIZE)
        .onClick(() => {
          this.isShowPlay = false;
        })
      Image($rawfile('functionImage/music_music_share.svg'))
        .width(MusicConstant.MUSIC_ICON_SIZE)
        .height(MusicConstant.MUSIC_ICON_SIZE)
        .objectFit(ImageFit.Contain)
    }
    .height(48)
    .width(CommonConstants.FULL_PARENT)
    .justifyContent(FlexAlign.SpaceBetween)
  }
}