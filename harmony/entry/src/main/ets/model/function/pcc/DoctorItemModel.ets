@Observed
export class DoctorItemModel{
  id:number=1
  background:string='示例资料'
  doctorName:string='示例资料'
  title:string=''
  pccId:number=1
  gender:string='女'
  rating:number=49
  specializedFields:string='自我成长，情绪管理'
  otherInformation:string='示例资料'
  photo:string='https://assets.example.invalid/placeholder.png'

  constructor(id:number,background:string,title:string,pccId:number,gender:string,rating:number,specializedFields:string,otherInformation:string,photo:string) {
    this.id = id
    this.background = background
    this.title = title
    this.pccId = pccId
    this.gender = gender
    this.rating = rating
    this.specializedFields = specializedFields
    this.otherInformation = otherInformation
    this.photo = photo
  }
}