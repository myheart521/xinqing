/*
 * Copyright (c) 2023 Huawei Device Co., Ltd.
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
import { KnowledgeConstant } from "../../../common/constant/Function";
import KnowledgeItem from "../../../model/function/knowledge/KnowledgeItem";


@Component
export default struct SubTitleItem {
  @Consume('introductionData') introductionData: KnowledgeItem;

  build() {
    Column() {
      Column() {
        Text(this.introductionData.title)
          .fontSize(24)
          .fontColor('#FFFFFF')
          .height(32)
          .fontWeight(FontWeight.Medium)
          .margin({ top: 16 })
        Text(this.introductionData.subTitle)
          .fontSize(14)
          .fontColor("#ffffff")
          .height(19)
          .opacity(KnowledgeConstant.FONT_COLOR_OPACITY_NORMAL)
          .margin({
            top: 2,
            bottom: 12
          })
        Image(this.introductionData.starLinePic[0])
          .height(20)
          .margin({ bottom: 12 })
          .width(309)
        Text(this.introductionData.createTime)
          .fontSize(14)
          .height(19)
          .margin({ bottom: 16 })
          .fontColor('#FFFFFF')
          .opacity(KnowledgeConstant.FONT_COLOR_OPACITY_NORMAL)
      }
      .width(KnowledgeConstant.FULL_SIZE)
      .alignItems(HorizontalAlign.Center)

      Image(this.introductionData.subPicTop)
        .height(214)
        .objectFit(ImageFit.Fill)
        .width(KnowledgeConstant.FULL_SIZE)
    }
    .backgroundColor(this.introductionData.backgroundColor)
  }
}