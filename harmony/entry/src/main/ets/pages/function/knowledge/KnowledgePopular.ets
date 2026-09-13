import { getKnowledgeData } from '../../../api/function/KnowledgeApi'
import knowledgeViewModel from '../../../viewmodel/function/knowledge/KnowledgeViewModel'
import { KnowledgeIndex } from './KnowledgeIndex'


@Entry
@Component
struct KnowledgePopular {
  aboutToAppear(): void {
    // this.getData()
  }
  //请求数据
  getData= async ()=>{
    const res = await getKnowledgeData()
    knowledgeViewModel.setZonesList(res.data)
  }
  build() {
    Column() {
      Stack(){
        KnowledgeIndex()
      }
    }
  }
}