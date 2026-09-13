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

import KnowledgeItem from '../../../model/function/knowledge/KnowledgeItem';
import { KnowledgeConstant } from '../../../common/constant/Function';
import KnowledgeViewModel from '../../../viewmodel/function/knowledge/KnowledgeViewModel';
import { AnimationItem } from '../../../model/function/knowledge/AnimationItem';
import { RouteTitle } from '../../../viewmodel/function/knowledge/TrainsMap';
import SwiperListItem from '../../../view/function/knowledge/SwiperListItem';
import SubTitleItem from '../../../view/function/knowledge/SubTitleItem';
import StyleListItem from '../../../view/function/knowledge/StyleListItem';
import BuildListItem from '../../../view/function/knowledge/BuildListItem';
import { Animation } from '../../../utils/knowledge/Animation';
import { router } from '@kit.ArkUI';
import { DeviceScreen } from '../../../utils/knowledge/DeviceScreen';

@Entry
@Component
struct IntroductionPage {
  @State zoneList: KnowledgeItem[] = KnowledgeViewModel.getZonesList();
  @Provide('introductionData') introductionData: KnowledgeItem = KnowledgeViewModel.getZonesList()[0];
  @State currentZoneId: number = 0;
  @State isShowReplay: Array<boolean> = [];
  @State isReachStart: boolean = false;
  @State buildingType: string = KnowledgeConstant.BUILDING_TEXT;
  @State homeIconOpacity: number = KnowledgeConstant.OPACITY_MIN;
  @Provide animationItem: AnimationItem = new AnimationItem();

  @Builder
  TitleIcon($$: RouteTitle) {
    Column() {
      Image($$.src)
        .width(56)
        .height(36)
        .scale({ x: $$.scale })
        .opacity($$.scale)
        .animation({
          duration: KnowledgeConstant.TITLE_ICON_ANIMATION_DURATION,
          curve: Curve.EaseOut
        })
    }
  }

  @Builder
  StickyHeader() {
    Column() {
      Image(this.introductionData.subPicBottom)
        .height(124)
        .objectFit(ImageFit.Fill)
        .width(KnowledgeConstant.FULL_SIZE)
        .backgroundColor(this.introductionData.backgroundColor)

      Stack({ alignContent: Alignment.Bottom }) {
        Image(this.introductionData.titleIcon)
          .height(27)
          .width(57)
          .margin({ bottom: 23 })

        this.TitleIcon({
          src: $rawfile('functionImage/knowledge_design_style.png'),
          scale: this.animationItem.scaleIcon[0]
        })
        this.TitleIcon({
          src: $rawfile('functionImage/knowledge_popular_science_ic.png'),
          scale: this.animationItem.scaleIcon[1]
        })
        this.TitleIcon({
          src: $rawfile('functionImage/knowledge_popular_science2.png'),
          scale: this.animationItem.scaleIcon[2]
        })
      }
      .width(KnowledgeConstant.FULL_SIZE)
      .height(73)

      Text(this.animationItem.iconTitle)
        .fontSize(10)
        .height(14)
        .fontColor("#000000")
        .opacity(KnowledgeConstant.FONT_COLOR_OPACITY_NORMAL)
        .margin({ bottom: 18 })
        .fontWeight(FontWeight.Bold)
    }
    .width(KnowledgeConstant.FULL_SIZE)
    .backgroundColor('#F1F3F5')
    .height(KnowledgeConstant.STICKY_HEIGHT)
    .alignItems(HorizontalAlign.Center)
  }

  @Builder
  StickyFooter() {
    Column()
      .height(161)
  }

  /**
   * The transition animation from page to page.
   */
  pageTransition() {
    PageTransitionEnter({ duration: KnowledgeConstant.SHARED_DURATION })
      .slide(SlideEffect.Bottom)
      .scale({
        x: 0,
        y: 0,
        z: 0,
        centerX: KnowledgeConstant.HALF_PERCENT,
        centerY: KnowledgeConstant.HALF_PERCENT
      })
    PageTransitionExit({ delay: KnowledgeConstant.SWIPER_DURATION })
      .slide(SlideEffect.Bottom)
      .scale({
        x: 0,
        y: 0,
        z: 0,
        centerX: KnowledgeConstant.HALF_PERCENT,
        centerY: KnowledgeConstant.HALF_PERCENT
      })
  }

  build() {
    Column() {
      Stack({ alignContent: Alignment.Bottom }) {
        Column() {
          List({ scroller: this.animationItem.scrollerForList }) {
            ListItem() {
              SwiperListItem({
                imageHeight: this.animationItem.imageHeight,
                currentZoneId: this.currentZoneId,
                arrowIconOpacity: this.animationItem.arrowIconOpacity
              })
            }

            ListItem() {
              SubTitleItem()
            }

            ListItemGroup({ header: this.StickyHeader(), footer: this.StickyFooter() }) {
              ListItem() {
                StyleListItem()
              }

              ListItem() {
                BuildListItem({ type: this.buildingType })
              }

              ListItem() {
                BuildListItem({ type: this.animationItem.geographicPicType })
              }
            }
          }
          .width(KnowledgeConstant.FULL_SIZE)
          .height(KnowledgeConstant.FULL_SIZE)
          .edgeEffect(EdgeEffect.None)
          .scrollBar(BarState.Off)
          .sticky(StickyStyle.Header)
          .onReachStart(() => {
            this.resetParameters();
          })
          .onScrollIndex((start: number) => {
            this.animationItem.currentListIndex = start;
          })
          .onReachEnd(() => {
            this.animationItem.listPosition = KnowledgeConstant.LIST_POSITION_END;
          })
          .onScroll(() => {
            this.startAnimation();
          })
          .onScrollFrameBegin((offset: number, state: ScrollState) => {
            let realOffset = Animation.controlImageScale(this.animationItem, offset, state);
            return { offsetRemain: realOffset };
          })
        }
        .height(KnowledgeConstant.FULL_SIZE)

        Image($r('app.media.up_icon'))
          .height(40)
          .aspectRatio(1)
          .margin({ bottom: 33, left: 308 })
          .onClick(() => {
            this.scrollToTop();
          })
          .opacity(1 - this.animationItem.arrowIconOpacity)

        Image($r('app.media.back'))
          .height(40)
          .aspectRatio(1)
          .position({top:15,left:15})
          // .margin({
          //   right: 308,
          //   top:30
          //   // bottom: this.animationItem.screenHeight - KnowledgeConstant.HOME_ICON_MARGIN_TOP
          // })
          .zIndex(999)
          .onClick(() => {
            router.back();
          })
      }
      .backgroundColor("#F1F3F5")
      .height(KnowledgeConstant.FULL_SIZE)
    }
    .height(KnowledgeConstant.FULL_SIZE)
  }

  aboutToAppear() {
    // 整改后的代码
    let params = router.getParams() as Record<string, Object>;
    this.currentZoneId = params.id as number;

    this.getIntroductionData();
    this.animationItem.screenHeight = DeviceScreen.getDeviceHeight();
  }

  /**
   * Get the introduction by currentZoneId.
   */
  getIntroductionData() {
    this.introductionData = this.zoneList.filter((item) => item.id === this.currentZoneId)[0];
  }

  /**
   * Reset all parameters if the list arrive begin by animation.
   */
  resetParameters() {
    this.animationItem.listPosition = KnowledgeConstant.LIST_POSITION_BEGIN;
    if (this.animationItem.listPosition === KnowledgeConstant.LIST_POSITION_BEGIN && this.isReachStart) {
      this.animationItem.imageHeight = KnowledgeConstant.FULL_PERCENT_NUMBER;
      this.animationItem.arrowIconOpacity = KnowledgeConstant.OPACITY_MAX;
    }
    this.isReachStart = false;
  }

  /**
   * Start animation when scroll the list.
   */
  startAnimation() {
    Animation.changeTitleIcon(this.animationItem);
  }

  /**
   * Click the up arrow scroll to the beginning.
   */
  scrollToTop() {
    if (this.animationItem.listPosition !== KnowledgeConstant.LIST_POSITION_BEGIN) {
      this.animationItem.scrollerForList.scrollTo({
        xOffset: 0,
        yOffset: KnowledgeConstant.SCROLL_UP_TOP_THRESHOLD,
        animation: {
          duration: this.animationItem.offsetSum / KnowledgeConstant.OFFSET_DIVIDE_RATIO,
          curve: Curve.Linear,
        }
      })
      this.isReachStart = true;
    } else {
      this.animationItem.imageHeight = KnowledgeConstant.FULL_PERCENT_NUMBER;
      this.animationItem.arrowIconOpacity = KnowledgeConstant.OPACITY_MAX;
    }
  }
}