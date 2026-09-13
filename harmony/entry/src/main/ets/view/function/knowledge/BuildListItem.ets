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
export default struct BuildListItem {
  @Prop type: string = '';
  @Consume('introductionData') introductionData: KnowledgeItem;

  build() {
    Column() {
      Image(this.introductionData.starLinePic[1])
        .height(20)
        .width(KnowledgeConstant.CONTENT_WIDTH_PERCENT)
        .margin({ bottom: 30 })
      if (this.type === KnowledgeConstant.BUILDING_TEXT) {
        Text(this.introductionData.buildingInformation)
          .width(KnowledgeConstant.CONTENT_WIDTH_PERCENT)
          .fontSize(14)
          .letterSpacing(0)
      }
      Column() {
        if (this.type === KnowledgeConstant.BUILDING_TEXT) {
          Image(this.introductionData.buildingPic)
            // .height($r('app.float.introduction_image_height'))
            .width(KnowledgeConstant.FULL_SIZE)
            .borderRadius(24)
            .margin({ top: 48 })
        } else {
          Image(this.type === KnowledgeConstant.GEOGRAPHY_LIGHT ?
          this.introductionData.geographyPicLight : this.introductionData.geographyPic)
            // .height($r('app.float.introduction_image_height'))
            .width(KnowledgeConstant.FULL_SIZE)
            .borderRadius(24)
            .margin({ top: 48 })
      }}
      .width(KnowledgeConstant.CONTENT_WIDTH_PERCENT)
      // .height($r('app.float.introduction_image_height'))
      .margin({ bottom: 67 })
      .justifyContent(FlexAlign.Center)
    }
    .width(KnowledgeConstant.FULL_SIZE)
    .alignItems(HorizontalAlign.Center)
    // .height($r('app.float.fixed_item_height'))
  }
}