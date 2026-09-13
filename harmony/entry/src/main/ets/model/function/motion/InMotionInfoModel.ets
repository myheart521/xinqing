@Observed
export class InMotionInfoModel{
  id:number=0
  text:string=''
  video:string=''
  time:number=0
  cal:string=''
  imageList:string[]=[]
  title:string=''
  league:string=''
  leagueDescribe:string=''
  motion:string=''
  motionFeatures:string[]=[]

  constructor(id:number,text:string,video:string,time:number,cal:string,imageList:string[],title:string,league:string,leagueDescribe:string,motion:string,motionFeatures:string[]) {
    this.id = id
    this.text = text
    this.video = video
    this.time = time
    this.cal = cal
    this.imageList = imageList
    this.title = title
    this.league = league
    this.leagueDescribe = leagueDescribe
    this.motion = motion
    this.motionFeatures = motionFeatures
  }
}