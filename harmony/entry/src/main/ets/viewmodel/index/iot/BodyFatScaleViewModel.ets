import { bodyFatScaleModel } from "../../../model/iot/BodyFatScale";

class BodyFatScaleViewModel{
  private bodyFatScaleData:bodyFatScaleModel= new bodyFatScaleModel()
  getBodyFatScale(){
    return this.bodyFatScaleData
  }
  setBodyFatScale(bodyFatScaleData:bodyFatScaleModel){
    this.bodyFatScaleData = bodyFatScaleData
  }
}

const bodyFatScaleViewModel = new BodyFatScaleViewModel()
export default bodyFatScaleViewModel

