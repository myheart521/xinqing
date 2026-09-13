
export class navItem{
  image:string|Resource=$r("app.media.load")
  id:string=""
  name:string=''

  constructor(id:string,image:string|Resource,name:string) {

    this.image = image
    this.id= id
    this.name = name
  }
}