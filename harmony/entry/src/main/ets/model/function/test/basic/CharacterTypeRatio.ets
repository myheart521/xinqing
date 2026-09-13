// /ets/data/AnsReturn.ets



@Observed
export class Res {
  res: string = ''; // 性格类型简称
  eRate: string = '0.00'; // E比率
  iRate: string = '0.00'; // I比率
  sRate: string = '0.00'; // S比率
  nRate: string = '0.00'; // N比率
  tRate: string = '0.00'; // T比率
  fRate: string = '0.00'; // F比率
  jRate: string = '0.00'; // J比率
  pRate: string = '0.00'; // P比率
  name: string = ''; // 性格名称
  disc: string = ''; // 性格描述
  constructor(data?: Res) {
    if (data) {
      this.res = data.res;
      this.eRate = data.eRate;
      this.iRate = data.iRate;
      this.sRate = data.sRate;
      this.nRate = data.nRate;
      this.tRate = data.tRate;
      this.fRate = data.fRate;
      this.jRate = data.jRate;
      this.pRate = data.pRate;
      this.name = data.name;
      this.disc = data.disc;
    }
  }
}
