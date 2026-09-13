export class FunctionConstant {
  //顶部轮播图高度
  static readonly BANNER_HEIGHT = 240
  //其他工具标题文字
  static readonly OTHER_FUNCTION_TITLE = "其 / 他 / 工 / 具 / 集 / 合"
  //单个工具的高度
  static readonly TOOLS_HEIGHT = 180
  //动画时长
  static readonly TRANSITION_DURATION: number = 500;
}

export class MusicConstant {
  //音乐描述start
  //音乐描述高度
  static readonly MUSIC_DES_HEIGHT = 300
  //音乐描述图片
  static readonly MUSIC_DES_WH = 170
  //音乐描述图片圆角
  static readonly MUSIC_DES_RADIUS = 8
  //音乐点赞收藏关注等图片大小
  static readonly MUSIC_DES_OPTION_ICON = 30
  //音乐点赞收藏关注等高度
  static readonly MUSIC_DES_OPTION_HEIGHT = 70
  //音乐描述end

  //音乐列表start
  //音乐列表圆角度数
  static readonly PLAYLIST_RADIUS = 40
  // 音乐列表的icon大小
  static readonly MUSIC_ICON_SIZE = 24
  //音乐列表end
}

//日记
export class DiaryConstant {
  static readonly DIARY_ICON_SIZE: number = 20
}

//运动
export class MotionConstant {
  static readonly MOTION_ICON_SIZE: number = 20
}

export class KnowledgeConstant {
  static readonly DURATION_1000: number = 1000; // 1000ms
  static readonly LETTER_1: number = 0.1;
  /**
   * Input length of the account.
   */
  static readonly INPUT_ACCOUNT_LENGTH = 11;
  /**
   *  Input length of the password.
   */
  static readonly INPUT_PASSWORD_LENGTH = 8;
  /**
   *  Left padding of the input box
   */
  static readonly INPUT_PADDING_LEFT = 0;
  /**
   * Delay time of simulated login
   */
  static readonly LOGIN_DELAY_TIME = 2000;
  /**
   * Common Spacing of Components
   */
  static readonly COMMON_SPACE = 10;
  /**
   * Title text of the home page
   */
  static readonly HOME_TITLE = '首页';
  /**
   * Title text of the setting page
   */
  static readonly MINE_TITLE = '我的';
  static readonly CAHT_TITLE = '聊天';
  /**
   * Spacing of other login methods
   */
  static readonly LOGIN_METHODS_SPACE = 44;
  /**
   * The width or height of the component is spread across the parent component.
   */
  static readonly FULL_PARENT = '100%';
  /**
   * The width of button
   */
  static readonly BUTTON_WIDTH = '90%';
  /**
   * The width of setting list
   */
  static readonly SET_LIST_WIDTH = '42%';
  /**
   * Home tab index
   */
  static readonly HOME_TAB_INDEX = 0;
  static readonly MOTION_TAB_INDEX = 1
  /**
   * Mine tab index
   */
  static readonly CHAT_TAB_INDEX = 2;
  static readonly MINE_TAB_INDEX = 3;
  //歌曲
  /**
   * Spacing between list items.
   */
  static readonly LIST_SPACE: string = '10vp';
  /**
   * A maximum of 4 reviews can be displayed under sm and md device types.
   */
  static readonly LIST_COUNT: number = 4;
  /**
   * Prefix of the reply message.
   */
  static readonly NICKNAME_PREV: string = '@';
  /**
   * Suffix of the reply message.
   */
  static readonly NICKNAME_SUFFIX: string = '：';
  //知识科普
  static readonly SECOND_LIST_ITEM_HEIGHT: number = 350;
  static readonly FIXED_ITEM_HEIGHT: number = 500;
  static readonly STAR_ICON_MARGIN: number = 80;
  static readonly OFFSET_SUM_THRESHOLD: number = 10;
  static readonly LIST_POSITION_MIDDLE: number = 1;
  static readonly GEOGRAPHY_LIGHT: string = 'geography_light';
  static readonly FULL_PERCENT_NUMBER: number = 100;
  static readonly GEOGRAPHY_DARK: string = 'geography_dark';
  static readonly ICON_SUBTITLE_ARRAY: Array<string> = ['DESIGN STYLE', 'BUILDING INFORMATION', 'GEOGRAPHIC LOCATION'];
  static OPACITY_MAX: number = 1;
  static readonly OPACITY_MIN: number = 0;
  static readonly MIN_IMAGE_HEIGHT: number = 40;
  static readonly LIST_POSITION_BEGIN: number = 0;
  static readonly LIST_SCROLL_DOWN_THRESHOLD: number = -40;
  static readonly DEVICE_DPI: number = 160;
  static readonly BUILDING_TEXT: string = 'building';
  static readonly TITLE_ICON_ANIMATION_DURATION: number = 500;
  static readonly FULL_SIZE: string = '100%';
  static readonly FONT_COLOR_OPACITY_NORMAL: number = 0.6;
  static readonly STICKY_HEIGHT: number = 229;
  static readonly SHARED_DURATION: number = 800;
  static readonly HALF_PERCENT: string = '50%';
  static readonly SWIPER_DURATION: number = 300;
  static readonly LIST_POSITION_END: number = 2;
  static readonly HOME_ICON_MARGIN_TOP: number = 80;
  static readonly SCROLL_UP_TOP_THRESHOLD: number = -100;
  static readonly OFFSET_DIVIDE_RATIO: number = 5;
  static readonly CONTENT_WIDTH_PERCENT: string = '87%';
  static readonly INTRODUCTION_IMAGE_COUNT: number = 5;
  static readonly DOUBLE_OR_HALF: number = 2;
  static readonly HALF_COUNT = Math.floor(5 / 2);
  static readonly IMAGE_X_OFFSET_MAX: number = 70;
  static readonly IMAGE_x_OFFSET_MIN: number = 45;
  static readonly IMAGE_ANIMATION_DURATION: number = 300;
  static readonly IMAGE_OPACITY_REDUCE: number = 0.1;
  static readonly IMAGE_ASPECT_RATIO: number = 16 / 21;
  static readonly IMAGE_BLUR_REDUCE: number = 10;
  static readonly IMAGE_HEIGHT_MIN: string = '78%';
  static readonly IMAGE_WIDTH_OFFSET: number = 6.7;
  static readonly TRANSITION_DURATION: number = 600;
  static readonly IMAGE_SWIPER_CACHE_COUNT: number = 3;
  static readonly ANIMATION_DURATION_NORMAL: number = 1000;
  static readonly INTRODUCTION_TITLE: string = '心理知识科普';
  static readonly SWIPER_DEFAULT_WIDTH: string = '66%';
  static readonly SWIPER_ASPECT_RATIO: number = 6 / 5;
  static readonly FULL_PERCENT: string = '100%';
  static readonly SHADOW_RADIUS: number = 50;
  static readonly DOUBLE_NUM: number = 50;
  static readonly INTRODUCTION_URL: string = 'pages/function/knowledge/IntroductionPage';
  static readonly COLOR_SCALE_1: number = 0.8;
  static readonly COLOR_SCALE_2: number = 1.0;
  static readonly MAX_SCALE: number = 4;
  static readonly ITEM_WIDTH: number = 86;
  static readonly SHOW_COUNT: number = 5;
  static readonly OPACITY_COEFFICIENTS: number = 0.1;
  static readonly OFFSET_COEFFICIENTS: number = 10;
  static readonly MAX_OFFSET_Y: number = 100;
  static readonly MAX_MOVE_OFFSET: number = 60;
}