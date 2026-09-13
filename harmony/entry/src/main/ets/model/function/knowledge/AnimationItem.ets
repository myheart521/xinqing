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
import { KnowledgeConstant } from "../../../common/constant/Function";


@Observed
export class AnimationItem {
  screenHeight: number = 0;
  offsetSum: number = 0;
  scrollerForList: Scroller = new Scroller();
  arrowIconOpacity: number = KnowledgeConstant.OPACITY_MAX;
  imageHeight: number = KnowledgeConstant.FULL_PERCENT_NUMBER;
  listPosition: number = KnowledgeConstant.LIST_POSITION_BEGIN;
  geographicPicType: string = KnowledgeConstant.GEOGRAPHY_LIGHT;
  currentListIndex: number = KnowledgeConstant.LIST_POSITION_BEGIN;
  iconTitle: string = KnowledgeConstant.ICON_SUBTITLE_ARRAY[0];
  scaleIcon: Array<number> = [KnowledgeConstant.OPACITY_MAX, KnowledgeConstant.OPACITY_MIN, KnowledgeConstant.OPACITY_MIN];
}