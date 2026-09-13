@Observed
export default class DiscussModel {
  id: number = 0
  createTime: number = 0
  typeId: string = ''
  context: string = ""
  likeNum: number = 0
  comNum: number = 0
  userName: string = "示例资料"
  title: string = ""
  userId: number = 0
}

export class  DiscussType {
  id: number=0
  type: string=""
}

export class DiscussPage{
  total:number=0
  records:DiscussModel[] = []
}
