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
import { ImageView } from "./ImageViewComponent";


@Component
export default struct ImageAnimation {
  @State currentIndex: number = KnowledgeConstant.HALF_COUNT;
  @Consume('introductionData') introductionData: KnowledgeItem;
  dialogController: CustomDialogController = new CustomDialogController({
    builder: ImageView({ currentImageId: this.currentIndex }),
    customStyle: true,
    alignment: DialogAlignment.Bottom,
  });

  /**
   * Get the image offset coefficients.
   *
   * @param index
   * @returns offset coefficients
   */
  getImgCoefficients(index?: number) {
    if (index === undefined) {
      return 0;
    }
    let coefficient = this.currentIndex - index;
    let tempCoefficient = Math.abs(coefficient);
    if (tempCoefficient <= KnowledgeConstant.HALF_COUNT) {
      return coefficient;
    }
    let dataLength = this.introductionData.imageList.length;
    let tempOffset = dataLength - tempCoefficient;
    if (tempOffset <= KnowledgeConstant.HALF_COUNT) {
      if (coefficient > 0) {
        return -tempOffset;
      }
      return tempOffset;
    }
    return 0;
  }

  /**
   * Get the image offset.
   *
   * @param index
   * @returns offset
   */
  getOffSetX(index?: number) {
    if (index === undefined) {
      return 0;
    }
    let offsetIndex = this.getImgCoefficients(index);
    let tempOffset = Math.abs(offsetIndex);
    let offsetX = 0;
    if (tempOffset === 1) {
      offsetX = -KnowledgeConstant.IMAGE_X_OFFSET_MAX * offsetIndex;
    } else if (tempOffset === KnowledgeConstant.HALF_COUNT) {
      offsetX = -KnowledgeConstant.IMAGE_x_OFFSET_MIN * offsetIndex;
    }
    return offsetX;
  }

  startAnimation(isLeft: boolean) {
    animateTo({
      duration: KnowledgeConstant.IMAGE_ANIMATION_DURATION,
    }, () => {
      let dataLength = this.introductionData.imageList.length;
      let tempIndex = isLeft ? this.currentIndex + 1 : this.currentIndex - 1 + dataLength;
      this.currentIndex = tempIndex % dataLength;
    })
  }

  build() {
    Stack() {
      ForEach(this.introductionData.imageList, (item: ResourceStr, index?: number) => {
        Row() {
          Image(item)
            .objectFit(ImageFit.ScaleDown)
            .borderRadius(8)
            .opacity(KnowledgeConstant.OPACITY_MAX - KnowledgeConstant.IMAGE_OPACITY_REDUCE
              * Math.min(KnowledgeConstant.HALF_COUNT, Math.abs(this.getImgCoefficients(index))))
        }
        .aspectRatio(KnowledgeConstant.IMAGE_ASPECT_RATIO)
        .borderRadius(8)
        .offset({ x: this.getOffSetX(index), y: 0 })
        .blur(KnowledgeConstant.IMAGE_BLUR_REDUCE * Math.abs(this.getImgCoefficients(index)))
        .zIndex(index !== this.currentIndex && this.getImgCoefficients(index) === 0 ?
          0 : KnowledgeConstant.HALF_COUNT - Math.abs(this.getImgCoefficients(index)))
        .height(index !== this.currentIndex && this.getImgCoefficients(index) === 0 ?
        KnowledgeConstant.IMAGE_HEIGHT_MIN : `${KnowledgeConstant.FULL_PERCENT_NUMBER -
            KnowledgeConstant.IMAGE_WIDTH_OFFSET * Math.abs(this.getImgCoefficients(index))}%`)
        .onClick(() => {
          this.dialogController.open();
        })
      }, (item: ResourceStr) => JSON.stringify(item))
    }
    .width(KnowledgeConstant.FULL_SIZE)
    // .height(Const.FULL_SIZE)
    .alignContent(Alignment.Center)
    .gesture(
      PanGesture({ direction: PanDirection.Horizontal })
        .onActionStart((event?: GestureEvent) => {
          if (!event) {
            return;
          }
          this.startAnimation(event.offsetX < 0);
        })
    )
  }
}