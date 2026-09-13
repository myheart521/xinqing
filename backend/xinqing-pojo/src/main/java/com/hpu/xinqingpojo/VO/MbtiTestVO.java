package com.hpu.xinqingpojo.VO;

public class MbtiTestVO {
    private String res;
    private String eRate;
    private String iRate;
    private String sRate;
    private String nRate;
    private String tRate;
    private String fRate;
    private String jRate;
    private String pRate;
    private String name;
    private String disc;

    public MbtiTestVO(String res, String eRate, String iRate, String sRate, String nRate, String tRate, String fRate, String jRate, String pRate, String name, String disc) {
        this.res = res;
        this.eRate = eRate;
        this.iRate = iRate;
        this.sRate = sRate;
        this.nRate = nRate;
        this.tRate = tRate;
        this.fRate = fRate;
        this.jRate = jRate;
        this.pRate = pRate;
        this.name = name;
        this.disc = disc;
    }

    // Getters
    public String getRes() {
        return res;
    }

    public String geteRate() {
        return eRate;
    }

    public String getiRate() {
        return iRate;
    }

    public String getsRate() {
        return sRate;
    }

    public String getnRate() {
        return nRate;
    }

    public String gettRate() {
        return tRate;
    }

    public String getfRate() {
        return fRate;
    }

    public String getjRate() {
        return jRate;
    }

    public String getpRate() {
        return pRate;
    }

    public String getName() {
        return name;
    }

    public String getDisc() {
        return disc;
    }
}
