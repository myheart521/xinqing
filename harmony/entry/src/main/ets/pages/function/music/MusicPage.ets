import { CommonConstants } from '../../../common/constant/CommonConstants';
import { FunctionConstant } from '../../../common/constant/Function';
import { SongItem } from '../../../model/function/music/SongData';
import { MediaService } from '../../../utils/music/MediaService';
import { Content } from '../../../view/function/music/Content';
import { Header } from '../../../components/Header';
import { Player } from '../../../view/function/music/Player';
import { SongDataSource } from '../../../viewmodel/function/music/SongDataSource';
import { songList } from '../../../viewmodel/function/music/SongListData';

@Entry
@Component
struct MusicPage {
  //定义音乐数据
  @Provide songDataSource:SongDataSource=new SongDataSource([])
  //定义是否播放音乐
  @StorageLink('isShowPlay') isShowPlay:boolean = false

  aboutToAppear(): void {
    //加载音乐数据
    songList.forEach((item:SongItem,index)=>{
      this.songDataSource.pushData(item)
    })
    MediaService.getInstance();
  }
  build() {
    Stack({ alignContent: Alignment.Top }) {
      // 音乐播放的顶部
      Header({title:$r("app.string.play_list")})
      // 音乐列表
      Content()
      // 音乐播放器的控制
      Player()
    }
    .width(CommonConstants.FULL_PARENT)
    .backgroundColor($r("app.color.music_bg"))
  }

  pageTransition() {
    PageTransitionEnter({ duration: FunctionConstant.TRANSITION_DURATION, curve: Curve.Smooth, type: RouteType.Pop })
    PageTransitionExit({ duration: FunctionConstant.TRANSITION_DURATION, curve: Curve.Smooth, type: RouteType.Push })
  }
}