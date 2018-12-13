package com.deweyliu.spring.cloud.weather.modules.weather;

/**
 * @author DeweyLiu
 * @version 1.0.0
 * @date 2018/12/12 16:23
 */
public interface WeatherDataService {

    /**
     * 根据城市ID查询天气数据
     *
     * @param cityId
     * @return
     */
    WeatherResponse getDataByCityId(String cityId);

    /**
     * 根据城市名称查询天气数据
     *
     * @param cityName
     * @return
     */
    WeatherResponse getDataByCityName(String cityName);
}
