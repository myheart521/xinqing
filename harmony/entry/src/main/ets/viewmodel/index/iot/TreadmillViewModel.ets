import { TreadmillItem } from "../../../model/index/TreadmillItem";


export class TreadmillViewModel{
  private treadmillData:TreadmillItem={
    bluetooth:true,
    isUptime:false,
    programs:"耐力",
    runningDuration:[0,0,0,0,0,0,0],
    weightCapacity:"0kg/0kg",
  }
  public setTreadmillData(treadmillData:TreadmillItem){
    this.treadmillData = treadmillData
  }

  public getTreadmillData(){
    return this.treadmillData
  }

  public setUptime(isUptime:boolean){
    this.treadmillData.isUptime = isUptime
  }
}