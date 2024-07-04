package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;

public class TotalizacaoNfe {
    private int totalNFe;
    private String uf;
    private double vnf;
    private double vserv;
    private double viss;
    private double vbc;
    private double vicms;
    private double vst;
    private double vfcpst;
    private double vfcpstret;
    private double vprod;
    private double vfcpufdest;
    private String arquivo;

    public TotalizacaoNfe() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public TotalizacaoNfe(int totalNFe, String uf, double vnf, double vserv, double viss, double vbc, double vicms, double vst, double vfcpst, double vfcpstret, double vprod, double vfcpufdest, String arquivo) {
        this.totalNFe = totalNFe;
        this.uf = uf;
        this.vnf = vnf;
        this.vserv = vserv;
        this.viss = viss;
        this.vbc = vbc;
        this.vicms = vicms;
        this.vst = vst;
        this.vfcpst = vfcpst;
        this.vfcpstret = vfcpstret;
        this.vprod = vprod;
        this.vfcpufdest = vfcpufdest;
        this.arquivo = arquivo;
    }


    public int getTotalNFe() {
        return totalNFe;
    }

    public void setTotalNFe(int totalNFe) {
        this.totalNFe = totalNFe;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public double getVnf() {
        return vnf;
    }

    public void setVnf(double vnf) {
        this.vnf = vnf;
    }

    public double getVserv() {
        return vserv;
    }

    public void setVserv(double vserv) {
        this.vserv = vserv;
    }

    public double getViss() {
        return viss;
    }

    public void setViss(double viss) {
        this.viss = viss;
    }

    public double getVbc() {
        return vbc;
    }

    public void setVbc(double vbc) {
        this.vbc = vbc;
    }

    public double getVicms() {
        return vicms;
    }

    public void setVicms(double vicms) {
        this.vicms = vicms;
    }

    public double getVst() {
        return vst;
    }

    public void setVst(double vst) {
        this.vst = vst;
    }

    public double getVfcpst() {
        return vfcpst;
    }

    public void setVfcpst(double vfcpst) {
        this.vfcpst = vfcpst;
    }

    public double getVfcpstret() {
        return vfcpstret;
    }

    public void setVfcpstret(double vfcpstret) {
        this.vfcpstret = vfcpstret;
    }

    public double getVprod() {
        return vprod;
    }

    public void setVprod(double vprod) {
        this.vprod = vprod;
    }

    public double getVfcpufdest() {
        return vfcpufdest;
    }

    public void setVfcpufdest(double vfcpufdest) {
        this.vfcpufdest = vfcpufdest;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public String toString() {
        return "NFeInfo{" +
                "totalNFe=" + totalNFe +
                ", uf='" + uf + '\'' +
                ", vnf=" + vnf +
                ", vserv=" + vserv +
                ", viss=" + viss +
                ", vbc=" + vbc +
                ", vicms=" + vicms +
                ", vst=" + vst +
                ", vfcpst=" + vfcpst +
                ", vfcpstret=" + vfcpstret +
                ", vprod=" + vprod +
                ", vfcpufdest=" + vfcpufdest +
                ", arquivo='" + arquivo + '\'' +
                '}';
    }
}