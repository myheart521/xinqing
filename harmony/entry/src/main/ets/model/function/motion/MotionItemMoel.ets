@Observed
export class MotionItemModel{
  id:number=3
  time:number=3
  image:string=''
  title:string=''
  league:string=''
  leagueDescribe:string=""
  motion:string=""
  motionFeatures:string[]=[]

  constructor(id:number,time:number,image:string,title:string,league:string,leagueDescribe:string,motion:string,motionFeatures:string[]) {
    this.id = id
    this.time=time
    this.image = image
    this.title = title
    this.league = league
    this.leagueDescribe = leagueDescribe
    this.motion = motion
    this.motionFeatures = motionFeatures
  }
}
