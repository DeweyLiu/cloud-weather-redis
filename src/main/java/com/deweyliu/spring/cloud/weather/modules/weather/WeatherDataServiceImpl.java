package com.deweyliu.spring.cloud.weather.modules.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.omg.CORBA.TIMEOUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author DeweyLiu
 * @version 1.0.0
 * @date 2018/12/12 16:25
 */
@Service
public class WeatherDataServiceImpl implements WeatherDataService {
    private final static Logger logger = LoggerFactory.getLogger(WeatherDataServiceImpl.class);

    private static final String WEATHER_URI = "http://wthrcdn.etouch.cn/weather_mini?";

    private static final long TIME_OUT = 10L;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public WeatherResponse getDataByCityId(String cityId) {
        String uri = WEATHER_URI + "citykey=" + cityId;
        return this.doGetWeahter(uri);
    }

    @Override
    public WeatherResponse getDataByCityName(String cityName) {
        String uri = WEATHER_URI + "city=" + cityName;
        return this.doGetWeahter(uri);
    }

    private WeatherResponse doGetWeahter(String uri) {
        String key = uri;
        String strBody = null;
        ObjectMapper mapper = new ObjectMapper();
        WeatherResponse resp = null;
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //判断缓存中是否存在数据
        if (stringRedisTemplate.hasKey(key)) {
            logger.info("Redis has data");
            strBody = ops.get(key);
        } else {
            logger.info("Redis don't has data");
            // 缓存没有，再调用服务接口来获取
            ResponseEntity<String> respString = restTemplate.getForEntity(uri, String.class);
            if (respString.getStatusCodeValue() == 200) {
                strBody = respString.getBody();
            }
            //数据写入缓存
            ops.set(key,strBody,TIME_OUT,TimeUnit.SECONDS);
        }
        try {
            resp = mapper.readValue(strBody, WeatherResponse.class);
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Error!", e);
        }
        return resp;
    }

}
