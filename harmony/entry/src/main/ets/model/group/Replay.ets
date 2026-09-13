import DiscussModel from "./DiscussModel"

@Observed
export class replay{
  id:number=0
  lunTanId:number=0
  userName:string="示例资料"
  comTime:number=0
  context:string=""
  likeNum:number=0
  userId:number=0
}

export class replayData{
  pingLuns:replay[]=[]
  lunTan:DiscussModel|null=null
}