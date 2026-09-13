import { router } from '@kit.ArkUI';
import { characterRes } from '../../../../api/function/TestApi';
import { Header } from '../../../../components/Header';
import { ImageComponent } from '../../../../components/ImageComponent';
import { Res } from '../../../../model/function/test/basic/CharacterTypeRatio';
import { CharacterTypeRatio } from '../../../../view/function/test/Basic/CharacterTypeRatio';

interface ansType{
  ans:string
}

@Entry
@Component
struct CharacterShowResult {
  @State paramsFromIndex: object = router.getParams() as ansType

  @State resShow: Res = new Res() // 声明并初始化resShow属性
  @State resStr: string = ""; // 声明并初始化resStr属性
  //post请求
  aboutToAppear() {
    //获取网络请求
    this.getCharacterResult(this.paramsFromIndex?.['ans'])
    console.log("this.paramsFromIndex",this.paramsFromIndex?.['ans'])
  }
  //请求数据,ans为用户答题选项的记录
  getCharacterResult= async (ans:string)=>{
    const res = await characterRes(ans)
    this.resShow = res.data
    this.resStr = this.resShow.res
  }

  build() {
    Scroll() {
      Column({ space: 20 }) {
        //标题
        Header({title:"性格测试结果"})
        //图片展示
        ImageComponent({ resStr: this.resStr })
        //结果界面
        CharacterTypeRatio({ resReturn: this.resShow })
      }
      .width('100%')
      .backgroundColor("#f0f0f0")
    }
  }
}