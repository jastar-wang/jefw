/*
 * Copyright 2020 Jastar Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jastarwang.jefw.util.extra.ip;

/**
 * IP信息
 *
 * @author Jastar Wang
 * @date 2020/9/18
 * @since 1.0
 */
public class IpInfo {
    /**
     * 国家名称，如：中国
     */
    private String country;
    /**
     * 国家编码，如：CN
     */
    private String countryId;
    /**
     * 省（自治区或直辖市），如：北京
     */
    private String region;
    /**
     * 省（自治区或直辖市）行政编码，如：110000
     */
    private String regionId;
    /**
     * 市，如：北京
     */
    private String city;
    /**
     * 市行政编码，如：110100
     */
    private String cityId;
    /**
     * 区县，如：朝阳区
     */
    private String county;
    /**
     * 区县行政编码，如：110101
     */
    private String countyId;
    /**
     * 运营商，如：联通
     */
    private String isp;
    /**
     * 所查询的ip
     */
    private String ip;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "IpInfo{" +
                "country='" + country + '\'' +
                ", countryId='" + countryId + '\'' +
                ", region='" + region + '\'' +
                ", regionId='" + regionId + '\'' +
                ", city='" + city + '\'' +
                ", cityId='" + cityId + '\'' +
                ", county='" + county + '\'' +
                ", countyId='" + countyId + '\'' +
                ", isp='" + isp + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
