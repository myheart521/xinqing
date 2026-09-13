import { Header } from '../../../../components/Header';
import { AdaptiveRes } from '../../../../model/function/test/basic/AdaptiveRes';
import { AdaptiveResult } from '../../../../view/function/test/Basic/AdaptiveResult';
import { AdaptiveSuggest } from '../../../../view/function/test/Basic/AdaptiveSuggest';
import { AdaptiveTheory } from '../../../../view/function/test/Basic/AdaptiveTheory';
import { router } from '@kit.ArkUI';
import { adaptiveRes } from '../../../../api/function/TestApi';

@Entry
@Component
struct AdaptiveShowResult {
  @State paramsFromIndex: object = router.getParams()
  @State resShow: AdaptiveRes = new AdaptiveRes() // 声明并初始化resShow属性
  //post请求
  aboutToAppear() {
    this.getResult(this.paramsFromIndex?.["ans"])
  }
  getResult=async (ans:string)=>{
    const res = await adaptiveRes(ans)
    this.resShow = res.data
  }

  build() {
    Scroll() {
      Column({ space: 20 }) {
        //标题
        Header({title:'心理测试结果'})
        //图片展示
        AdaptiveTheory()
        //结果界面
        AdaptiveResult({resReturn:this.resShow})
        //建议界面
        AdaptiveSuggest({resReturn:this.resShow})
      }
      .width('100%')
      .backgroundColor("#f0f0f0")
    }
  }
}