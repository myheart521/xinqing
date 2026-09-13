/*
 * Copyright (c) 2023 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use pageThis file except in compliance with the License.
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
import { KnowledgeConstant } from '../../common/constant/Function';
import { AnimationItem } from '../../model/function/knowledge/AnimationItem';

export class Animation {
  /**
   * Get the title icon accord to the offset sum.
   *
   * @param pageThis
   */
  public static changeTitleIcon(animationItem: AnimationItem) {
    let imageHeightVP = (animationItem.screenHeight * animationItem.imageHeight) / KnowledgeConstant.FULL_PERCENT_NUMBER;
    let firstStarLine = imageHeightVP + KnowledgeConstant.SECOND_LIST_ITEM_HEIGHT +
    KnowledgeConstant.FIXED_ITEM_HEIGHT - KnowledgeConstant.STAR_ICON_MARGIN;
    let secondStarLine = firstStarLine + KnowledgeConstant.FIXED_ITEM_HEIGHT;
    let thirdStarLine = secondStarLine + KnowledgeConstant.FIXED_ITEM_HEIGHT;
    animationItem.offsetSum = animationItem.scrollerForList.currentOffset().yOffset;

    // Set the listPosition.
    if (animationItem.offsetSum > KnowledgeConstant.OFFSET_SUM_THRESHOLD) {
      animationItem.listPosition = KnowledgeConstant.LIST_POSITION_MIDDLE;
    }

    if (animationItem.offsetSum > firstStarLine + KnowledgeConstant.FIXED_ITEM_HEIGHT / 2) {
      animationItem.geographicPicType = KnowledgeConstant.GEOGRAPHY_LIGHT;
    }

    if (animationItem.offsetSum <= firstStarLine + KnowledgeConstant.FIXED_ITEM_HEIGHT / 2) {
      animationItem.geographicPicType = KnowledgeConstant.GEOGRAPHY_DARK;
    }
    // Change icons opacity by the sum of offset.
    if ((animationItem.currentListIndex === 2) && (animationItem.offsetSum <= firstStarLine)) {
      animationItem.iconTitle = KnowledgeConstant.ICON_SUBTITLE_ARRAY[0];
      animationItem.scaleIcon = [KnowledgeConstant.OPACITY_MAX, KnowledgeConstant.OPACITY_MIN,
        KnowledgeConstant.OPACITY_MIN];
    }
    if ((animationItem.currentListIndex === 2) && (animationItem.offsetSum > firstStarLine) &&
      (animationItem.offsetSum < secondStarLine)) {
      animationItem.iconTitle = KnowledgeConstant.ICON_SUBTITLE_ARRAY[1];
      animationItem.scaleIcon = [KnowledgeConstant.OPACITY_MIN, KnowledgeConstant.OPACITY_MAX,
        KnowledgeConstant.OPACITY_MIN];
    }
    if ((animationItem.currentListIndex === 2) && (animationItem.offsetSum >= secondStarLine) &&
      (animationItem.offsetSum < thirdStarLine)) {
      animationItem.iconTitle = KnowledgeConstant.ICON_SUBTITLE_ARRAY[2];
      animationItem.scaleIcon = [KnowledgeConstant.OPACITY_MIN, KnowledgeConstant.OPACITY_MIN,
        KnowledgeConstant.OPACITY_MAX];
    }
  }

  /**
   * Control the image scale.
   *
   * @param pageThis
   * @param offset
   * @param state
   * @returns real list offset.
   */
  public static controlImageScale(animationItem: AnimationItem, offset: number, state: ScrollState): number {
    if ((offset > 0) && (animationItem.imageHeight > KnowledgeConstant.MIN_IMAGE_HEIGHT)) {
      // Scale down the image by offset.
      let offsetHeight = (Math.abs(offset) * KnowledgeConstant.FULL_PERCENT_NUMBER) / Number(animationItem.screenHeight);
      let heightOffset = animationItem.imageHeight - KnowledgeConstant.MIN_IMAGE_HEIGHT > offsetHeight ?
        offsetHeight : animationItem.imageHeight - KnowledgeConstant.MIN_IMAGE_HEIGHT;
      animationItem.imageHeight = animationItem.imageHeight - heightOffset;
      animationItem.arrowIconOpacity = animationItem.arrowIconOpacity -
        heightOffset / (KnowledgeConstant.FULL_PERCENT_NUMBER - KnowledgeConstant.MIN_IMAGE_HEIGHT);
      return 0;
    }
    if ((animationItem.listPosition === KnowledgeConstant.LIST_POSITION_BEGIN) && (offset < 0)
      && (animationItem.imageHeight < KnowledgeConstant.FULL_PERCENT_NUMBER)) {
      // // Scale up the image by offset.
      let offsetHeight = (Math.abs(offset) * KnowledgeConstant.FULL_PERCENT_NUMBER) / Number(animationItem.screenHeight);
      let heightOffset = KnowledgeConstant.FULL_PERCENT_NUMBER - animationItem.imageHeight > offsetHeight ?
        offsetHeight : KnowledgeConstant.FULL_PERCENT_NUMBER - animationItem.imageHeight;
      const imageHeight: number = animationItem.imageHeight;
      animationItem.imageHeight = imageHeight + heightOffset;
      const arrowIconOpacity: number = animationItem.arrowIconOpacity;
      animationItem.arrowIconOpacity = arrowIconOpacity +
        heightOffset / (KnowledgeConstant.FULL_PERCENT_NUMBER - KnowledgeConstant.MIN_IMAGE_HEIGHT);
      return 0;
    }
    if ((state === ScrollState.Scroll) && (offset < KnowledgeConstant.LIST_SCROLL_DOWN_THRESHOLD)
      && (animationItem.imageHeight === KnowledgeConstant.FULL_PERCENT_NUMBER)) {
      return 0;
    }
    return offset;
  }
}