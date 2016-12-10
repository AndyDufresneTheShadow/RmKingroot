package com.sensetime.bughit.crashreport.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * sprout : 上传的json信息
 */
public class Sprout {
    private String imei;
    private String brand;
    private String deviceName;
    private Boolean isRooted;
    private String osVersion;
    private String osBuild;
    private String pkgName;
    private Integer versionCode;
    private Long timestamp;
    private String errorMsg;
    private String line;
    private String crashId;

    public static Sprout transfer(CrashInfo crashInfo) {
        Sprout sprout = new Sprout();
        DeviceInfo deviceInfo = crashInfo.getDeviceInfo();

        sprout.imei = deviceInfo.getImei();
        sprout.brand = deviceInfo.getBrand();
        sprout.deviceName = deviceInfo.getDeviceName();
        sprout.isRooted = deviceInfo.isRooted();
        sprout.osVersion = deviceInfo.getOsVersion();
        sprout.osBuild = deviceInfo.getOsBuild();
        sprout.pkgName = crashInfo.getAppInfo().getPkgName();
        sprout.versionCode = crashInfo.getAppInfo().getVersionCode();
        sprout.timestamp = crashInfo.getTimestamp();
        sprout.errorMsg = crashInfo.getErrorMsg();
        sprout.line = crashInfo.getLine();
        sprout.crashId = crashInfo.getCrashId();
        return sprout;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("imei: " + imei + "\n")
                .append("crashId: " + crashId + "\n")
                .append("brand: " + brand + "\n")
                .append("deviceName: " + deviceName + "\n")
                .append("isRooted: " + isRooted + "\n")
                .append("osVersion: " + osVersion + "\n")
                .append("osBuild: " + osBuild + "\n")
                .append("pkgName: " + pkgName + "\n")
                .append("versionCode: " + versionCode + "\n")
                .append("time: " + parseTime(timestamp) + "\n")
                .append("errorMsg: " + errorMsg + "\n")
                .append("line: " + line + "\n")
                .toString();

    }

    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     *
     * @param seconds
     * @return
     */
    private String parseTime(long seconds) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(seconds*1000));
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getRooted() {
        return isRooted;
    }

    public void setRooted(Boolean rooted) {
        isRooted = rooted;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsBuild() {
        return osBuild;
    }

    public void setOsBuild(String osBuild) {
        this.osBuild = osBuild;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
