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

import { SongItem } from '../../../model/function/music/SongData';
import { router } from '@kit.ArkUI';


const songList: SongItem[] = [
  {
    id: 1,
    title: 'Windy Hill',
    singer: '羽肿',
    mark: $rawfile('functionImage/music_vip.svg'),
    label: $rawfile('functionImage/music_picture1.png'),
    src: 'WindyHill.mp3',
    index: 0,
    lyric: ''
  },
  {
    id: 2,
    title: '起风了',
    singer: '买辣椒也用券',
    mark: $rawfile('functionImage/music_sq.svg'),
    label: $rawfile('functionImage/music_picture5.jpg'),
    src: 'qifengle.mp3',
    index: 1,
    lyric: 'lrcfiles/qifengle.lrc'
  },
  {
    id: 3,
    title: 'Letter That Writing in the Wind',
    singer: 'July',
    mark: $rawfile('functionImage/music_vip.svg'),
    label: $rawfile('functionImage/music_picture2.png'),
    src: 'LetterThatWritingInTheWind.mp3',
    index: 2,
    lyric: ''
  },
  {
    id: 4,
    title: 'Flower Dance',
    singer: 'OKAWARI',
    mark: $rawfile('functionImage/music_sq.svg'),
    label: $rawfile('functionImage/music_picture3.png'),
    src: 'FlowerDance.mp3',
    index: 3,
    lyric: ''
  },
  {
    id: 5,
    title: 'Things You Said',
    singer: 'Cody Fry&Abby Cates',
    mark: $rawfile('functionImage/music_sq.svg'),
    label: $rawfile('functionImage/music_picture4.png'),
    src: 'ThingsYouSaid.mp3',
    index: 4,
    lyric: 'lrcfiles/ThingsYouSaid.lrc'
  },
  {
    id: 6,
    title: 'Time To Love',
    singer: 'OCTOBER',
    mark: $rawfile('functionImage/music_sq.svg'),
    label: $rawfile('functionImage/music_picture5.jpg'),
    src: 'TimeToLove.mp3',
    index: 5,
    lyric: ''
  },
  {
    id: 7,
    title: 'Sacred Play Secret Place',
    singer: 'Matryoshka',
    mark: $rawfile('functionImage/music_vip.svg'),
    label: $rawfile('functionImage/music_picture6.png'),
    src: 'SacredPlaySecretPlace.mp3',
    index: 6,
    lyric: 'lrcfiles/SacredPlaySecretPlace.lrc'
  },
  {
    id: 8,
    title: 'Take Me Hand',
    singer: 'DAISHI DANCE&Cécile Corbel',
    mark: $rawfile('functionImage/music_vip.svg'),
    label: $rawfile('functionImage/music_picture7.png'),
    src: 'TakeMeHand.mp3',
    index: 7,
    lyric: 'lrcfiles/TakeMeHand.lrc'
  },
  {
    id: 9,
    title: '所念皆星河',
    singer: 'CMJ',
    mark: $rawfile('functionImage/music_vip.svg'),
    label: $rawfile('functionImage/music_picture8.png'),
    src: 'SuoNianJieXingHe.mp3',
    index: 8,
    lyric: ''
  }
]

const optionList: OptionItem[] = [
  { image: $rawfile('functionImage/music_collect.svg'), text: $r('app.string.collect') },
  { image: $rawfile('functionImage/music_download.svg'), text: $r('app.string.downLoad') },
  {
    image: $rawfile('functionImage/music_comments.svg'),
    text: $r('app.string.comment'),
    // action: (pageIndexInfos: NavPathStack) => {
    //   router.back()
    // }
  },
  { image: $rawfile('functionImage/music_share.svg'), text: $r('app.string.share') }
]

class OptionItem {
  image: Resource = $rawfile('functionImage/music_collect.svg');
  text?: Resource;
  action?: (pageIndexInfos: NavPathStack) => void;
}

export { optionList, OptionItem, songList }